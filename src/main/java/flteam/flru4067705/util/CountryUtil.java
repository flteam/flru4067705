package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Country;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CountryUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private CountryUtil() {
    }

    @Nullable
    public static List<Country> getAllCountries() {
        try (FileReader fileReader = new FileReader(CountryUtil.class.getResource("/countries.json").getFile())) {
            return OBJECT_MAPPER.readValue(fileReader, new TypeReference<List<Country>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
