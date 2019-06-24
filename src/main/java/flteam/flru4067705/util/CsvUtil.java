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
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CsvUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final List<Skill> SKILLS = SkillUtil.getAllSkills();

    private CsvUtil() {
    }

    /**
     * Метод конвертирования профиля в CSV с добавлением доп. полей
     */
    public static void convertProfileToCsv(String jsonFileName) {
        try (FileReader fileReader = new FileReader("profiles/" + jsonFileName);
             FileWriter fileWriter = new FileWriter(new File("csv/" + jsonFileName.replace("json", "csv")))) {
            Set<Profile> profiles = OBJECT_MAPPER.readValue(fileReader, new TypeReference<Set<Profile>>() {
            });
            for (Profile profile : profiles) {
                ProfileCsv profileCsv = new ProfileCsv();
                profileCsv.fullName = profile.fullName;
                profileCsv.country = profile.location;
                profileCsv.aboutUrl = profile.artstationProfileUrl;
                profileCsv.skills = convertSkills(profile.skills);
                addInfoToProfileCsv(profileCsv, profile.username);
                if ((profileCsv.email != null && !profileCsv.email.isEmpty())
                        || (profileCsv.fbUrl != null && !profileCsv.fbUrl.isEmpty())) {
                    fileWriter.write(profileCsv + "\n");
                }
            }
        } catch (IOException e) {
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

    private static void addInfoToProfileCsv(@NotNull ProfileCsv profileCsv, String username) throws IOException {
        String url = "https://www.artstation.com/" + username + "/profile";
        Document document = Jsoup.connect(url).get();
        Element element = document.getElementsByClass("wrapper-main").get(0);
        Element scriptElement = element.getElementsByTag("script").get(1);
        String script = scriptElement.html();
        String start = "cache.put('/users/" + username + "/quick.json', '";
        int startIndex = script.indexOf(start) + start.length();
        String json = script.substring(startIndex);
        json = json.substring(0, json.length() - 7).replace("\\\"", "\"");
        JSONTokener jsonTokener = new JSONTokener(json);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        profileCsv.email = jsonObject.optString("public_email", null);
        profileCsv.webSite = jsonObject.optString("website_url", null);
        profileCsv.fbUrl = jsonObject.optString("facebook_url", null);
        profileCsv.linkedInUrl = jsonObject.optString("linkedin_url", null);
        profileCsv.instagramUrl = jsonObject.optString("instagram_url", null);
    }

}
