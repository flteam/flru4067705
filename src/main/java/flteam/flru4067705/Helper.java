package flteam.flru4067705;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Profile;
import flteam.flru4067705.model.ProfileCsv;
import flteam.flru4067705.model.Skill;
import flteam.flru4067705.util.SkillUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final List<Skill> SKILLS = SkillUtil.getAllSkills();

    public static void main(String[] args) {
        try {
            merge("China");
            merge("United Kingdom");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void merge(String countryName) throws IOException {
        String country1 = countryName + ".json";
        String country2 = countryName + "_add.json";
        String country3 = countryName + ".json";
        File file1 = new File("profiles/" + country1);
        File file2 = new File("profiles/" + country2);
        File file3 = new File("merge/" + country3);
        Set<Profile> profiles1 = OBJECT_MAPPER.readValue(file1, new TypeReference<Set<Profile>>() {
        });
        Set<Profile> profiles2 = OBJECT_MAPPER.readValue(file2, new TypeReference<Set<Profile>>() {
        });
        profiles1.addAll(profiles2);
        OBJECT_MAPPER.writeValue(file3, profiles1);
    }

    private static void temp3() {
        String username = "radzio";
        String country = "Netherlands.json";
        File file = new File("profiles/" + country);
        try {
            Set<Profile> profiles = OBJECT_MAPPER.readValue(file, new TypeReference<Set<Profile>>() {
            });
            for (Profile profile : profiles) {
                if (profile.username.equals(username)) {
                    ProfileCsv profileCsv = new ProfileCsv();
                    profileCsv.fullName = profile.fullName;
                    profileCsv.aboutUrl = profile.artstationProfileUrl;
                    profileCsv.skills = convertSkills(profile.skills);
                    try (FileReader fileReader = new FileReader("test.json")) {
                        JSONTokener jsonTokener = new JSONTokener(fileReader);
                        JSONObject jsonObject = new JSONObject(jsonTokener);
                        profileCsv.country = jsonObject.optString("country", null);
                        profileCsv.city = jsonObject.optString("city", null);
                        profileCsv.email = jsonObject.optString("public_email", null);
                        profileCsv.webSite = jsonObject.optString("website_url", null);
                        profileCsv.fbUrl = jsonObject.optString("facebook_url", null);
                        profileCsv.linkedInUrl = jsonObject.optString("linkedin_url", null);
                        profileCsv.instagramUrl = jsonObject.optString("instagram_url", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(profileCsv);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void temp2() {
        File csvFile = new File("csv");
        List<String> csvs = Stream.of(Objects.requireNonNull(csvFile.listFiles()))
                .map(File::getName)
                .map(s -> s.replace(".csv", ""))
                .collect(Collectors.toList());
        File jsonFile = new File("profiles");
        File[] jsons = Objects.requireNonNull(jsonFile.listFiles());
        for (File json : jsons) {
            if (csvs.contains(json.getName().replace(".json", ""))) {
                json.delete();
            }
        }
    }

    private static void temp1() {
        File file = new File("profiles");
        File[] jsons = Objects.requireNonNull(file.listFiles());
        List<String> usernames = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("test.txt"))) {
            while (scanner.hasNextLine()) {
                String username = scanner.nextLine();
                usernames.add(username);
            }
            Map<String, List<ProfileCsv>> map = new HashMap<>();
            for (File json : jsons) {
                try {
                    Set<Profile> profiles = OBJECT_MAPPER.readValue(json, new TypeReference<Set<Profile>>() {
                    });
                    for (Profile profile : profiles) {
                        if (usernames.contains(profile.username)) {
                            ProfileCsv profileCsv = new ProfileCsv();
                            profileCsv.fullName = profile.fullName;
                            profileCsv.aboutUrl = profile.artstationProfileUrl;
                            profileCsv.skills = convertSkills(profile.skills);
                            addInfoToProfileCsv(profileCsv, profile.username, null, 0);
                            if ((profileCsv.email != null && !profileCsv.email.isEmpty())
                                    || (profileCsv.fbUrl != null && !profileCsv.fbUrl.isEmpty())) {
                                List<ProfileCsv> v = map.computeIfAbsent(json.getName(), k -> new ArrayList<>());
                                v.add(profileCsv);
                            }
                            usernames.remove(profile.username);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Map.Entry<String, List<ProfileCsv>> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":\n" + entry.getValue().stream().map(ProfileCsv::toString).collect(Collectors.joining("\n")));
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        Document document = connection.get();
        Element element = document.getElementsByClass("wrapper-main").get(0);
        Element scriptElement = element.getElementsByTag("script").get(1);
        String script = scriptElement.html();
        Pattern pattern = Pattern.compile(".*cache\\.put\\('.*', '(.*)'\\).*");
        Matcher matcher = pattern.matcher(script.replace("\n", ""));
        if (matcher.matches()) {
            String json = matcher.group(1);
            JSONTokener jsonTokener = new JSONTokener(json.replace("\\", ""));
            JSONObject jsonObject = new JSONObject(jsonTokener);
            profileCsv.country = jsonObject.optString("country", null);
            profileCsv.city = jsonObject.optString("city", null);
            profileCsv.email = jsonObject.optString("public_email", null);
            profileCsv.webSite = jsonObject.optString("website_url", null);
            profileCsv.fbUrl = jsonObject.optString("facebook_url", null);
            profileCsv.linkedInUrl = jsonObject.optString("linkedin_url", null);
            profileCsv.instagramUrl = jsonObject.optString("instagram_url", null);
        } else {
            System.out.println("For " + username + "\n" + script);
            System.out.println();
        }
    }

}
