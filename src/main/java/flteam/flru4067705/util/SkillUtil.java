package flteam.flru4067705.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.model.Skill;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SkillUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private SkillUtil() {
    }

    public static List<Skill> getAllSkills() {
        try (FileReader fileReader = new FileReader(SkillUtil.class.getResource("/skills.json").getFile())) {
            return OBJECT_MAPPER.readValue(fileReader, new TypeReference<List<Skill>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
