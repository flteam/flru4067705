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
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

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
                profileCsv.fio = profile.fullName;
                profileCsv.country = profile.location;
                profileCsv.about = profile.artstationProfileUrl;
                profileCsv.skills = convertSkills(profile.skills);
                addInfoToProfileCsv(profileCsv, profile.username);
                fileWriter.write(profileCsv + "\n");
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

    //todo: капча не даёт получить JSON, придётся вытаскивать его из страницы профиля
    private static void addInfoToProfileCsv(@NotNull ProfileCsv profileCsv, String username) throws IOException {
        String url = "https://www.artstation.com/users/" + username + "/quick.json";
        Response response = Request.Get(url).execute();
        HttpResponse httpResponse = response.returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            JSONTokener jsonTokener = new JSONTokener(httpResponse.getEntity().getContent());
            JSONObject jsonObject = new JSONObject(jsonTokener);
            profileCsv.email = jsonObject.getString("public_email");
            profileCsv.site = jsonObject.getString("website_url");
            profileCsv.fb = jsonObject.getString("facebook_url");
            profileCsv.linkedId = jsonObject.getString("linkedin_url");
            profileCsv.instagram = jsonObject.getString("instagram_url");
        } else {
            throw new IOException("Status code is " + statusCode + " on " + url);
        }
    }

}
