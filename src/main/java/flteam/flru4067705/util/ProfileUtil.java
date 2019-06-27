package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.*;
import flteam.flru4067705.parser.Parser;
import org.apache.http.HttpHost;

import java.io.File;
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

    public static void checkDownloadedProfilesForCountry(Country country,
                                                         HttpHost proxy,
                                                         String cookieValue,
                                                         String publicCsrfTokenValue) {
        System.out.println("Checking " + country.name + "...");
        Set<Profile> profiles = getProfilesByCountry(country);
        int count = getCountOfProfilesByCountry(country, proxy, cookieValue, publicCsrfTokenValue);
        if (count != profiles.size()) {
            System.out.printf("For %s count of profiles are not equal! (downloaded: %d, actual: %d)\n", country.name, profiles.size(), count);
        }
    }

    private static Set<Profile> getProfilesByCountry(Country country) {
        try {
            return OBJECT_MAPPER.readValue(new File("profiles/" + country.name + ".json"), new TypeReference<Set<Profile>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getCountOfProfilesByCountry(Country country,
                                                   HttpHost proxy,
                                                   String cookieValue,
                                                   String publicCsrfTokenValue) {
        SearchBody searchBody = new SearchBody(1, Filter.buildCountryFilter(country));
        try {
            Parser parser = new Parser(cookieValue, publicCsrfTokenValue, proxy);
            UsersResponse usersResponse = parser.doRequest(searchBody);
            return usersResponse.totalCount;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(searchBody + ", country: " + country.name);
        }
        return 0;
    }

}
