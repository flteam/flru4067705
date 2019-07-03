package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Profile;
import flteam.flru4067705.model.ProfileCsv;
import flteam.flru4067705.model.Skill;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final Set<Skill> SKILLS = SkillUtil.getAllSkills();

    private CsvUtil() {
    }

    /**
     * Метод конвертирования профиля в CSV с добавлением доп. полей
     */
    public static void convertProfileToCsv(String jsonFileName, String proxyHost, int proxyPort) {
        try (FileReader fileReader = new FileReader("profiles/" + jsonFileName);
             FileWriter fileWriter = new FileWriter(new File("csv/" + jsonFileName.replace("json", "csv")))) {
            Set<Profile> profiles = OBJECT_MAPPER.readValue(fileReader, new TypeReference<Set<Profile>>() {
            });
            for (Profile profile : profiles) {
                ProfileCsv profileCsv = new ProfileCsv();
                profileCsv.fullName = profile.fullName;
                profileCsv.aboutUrl = profile.artstationProfileUrl;
                profileCsv.skills = convertSkills(profile.skills);
                addInfoToProfileCsv(profileCsv, profile.username, proxyHost, proxyPort);
                if ((profileCsv.email != null && !profileCsv.email.isEmpty())
                        || (profileCsv.fbUrl != null && !profileCsv.fbUrl.isEmpty())) {
                    fileWriter.write(profileCsv + "\n");
                }
            }
            System.out.println("Converting for profile " + jsonFileName + " is completed!");
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("For proxy " + proxyHost + ":" + proxyPort + " and country " + jsonFileName.replace(".json", ""));
        }
    }

    private static List<String> convertSkills(@NotNull List<Skill> skills) {
        List<String> convertedSkills = new ArrayList<>();
        List<String> skillsStrings = skills.stream()
                .map(Skill::getSkillName)
                .collect(Collectors.toList());
        for (Skill skill : SKILLS) {
            if (skillsStrings.contains(skill.skillName)) {
                convertedSkills.add(skill.skillName);
            } else {
                convertedSkills.add("");
            }
        }
        return convertedSkills;
    }

    private static void addInfoToProfileCsv(@NotNull ProfileCsv profileCsv,
                                            String username,
                                            String proxyHost,
                                            int proxyPort) throws IOException {
        String url = "https://www.artstation.com/" + username + "/profile";
        Connection connection = Jsoup.connect(url);
        if (proxyHost != null) {
            connection.proxy(proxyHost, proxyPort);
        }
        try {
            Document document = connection.get();
            Element element = document.getElementsByClass("wrapper-main").get(0);
            Element scriptElement = element.getElementsByTag("script").get(1);
            String script = scriptElement.html();
            Pattern pattern = Pattern.compile(".*cache\\.put\\('.*', '(.*)'\\).*");
            Matcher matcher = pattern.matcher(script.replace("\n", ""));
            if (matcher.matches()) {
                String json = matcher.group(1);
                JSONTokener jsonTokener = new JSONTokener(
                        json.replace("\\\"", "\"")
                                .replace("\\\\\"", "\\\"")
                                .replaceAll("\\\\{2,}", "")
                );
                try {
                    JSONObject jsonObject = new JSONObject(jsonTokener);
                    profileCsv.country = jsonObject.optString("country", null);
                    profileCsv.city = jsonObject.optString("city", null);
                    profileCsv.email = jsonObject.optString("public_email", null);
                    profileCsv.webSite = jsonObject.optString("website_url", null);
                    profileCsv.fbUrl = jsonObject.optString("facebook_url", null);
                    profileCsv.linkedInUrl = jsonObject.optString("linkedin_url", null);
                    profileCsv.instagramUrl = jsonObject.optString("instagram_url", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println(json);
                    System.out.println();
                }
            } else {
                System.out.println("Not found script for " + username + "\n" + script);
                System.out.println();
            }
        } catch (HttpStatusException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            }
        }
    }

    public static void compactAllCsvInOne() {
        int count = 0;
        try (FileWriter fileWriter = new FileWriter("all.csv")) {
            List<File> csvs = Stream.of(new File("csv").listFiles())
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(File::getName))
                    .collect(Collectors.toList());
            for (File file : csvs) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        fileWriter.write(line + "\n");
                        count++;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(count);
    }

}
