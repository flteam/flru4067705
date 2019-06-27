package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Country;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountryUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private CountryUtil() {
    }

    public static Set<Country> getAllCountries() {
        try (FileReader fileReader = new FileReader(CountryUtil.class.getResource("/countries.json").getFile())) {
            return OBJECT_MAPPER.readValue(fileReader, new TypeReference<Set<Country>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void checkThatAllCountriesArePresent() {
        Set<Country> countries = CountryUtil.getAllCountries();
        File file = new File("profiles");
        List<String> jsons = Stream.of(Objects.requireNonNull(file.listFiles()))
                .map(File::getName)
                .map(s -> s.replace(".json", ""))
                .collect(Collectors.toList());
        for (Country country : countries) {
            if (!jsons.contains(country.name)) {
                System.out.println("Country " + country.name + " is absent!");
            }
        }
    }

}
