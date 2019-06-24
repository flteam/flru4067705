package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Country;
import flteam.flru4067705.model.Profile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class ProfileUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private ProfileUtil() {
    }

    public static void saveProfilesByCountry(Set<Profile> profiles, Country country) {
        try (FileWriter fileWriter = new FileWriter("profiles/" + country.name + ".json")) {
            OBJECT_MAPPER.writeValue(fileWriter, profiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
