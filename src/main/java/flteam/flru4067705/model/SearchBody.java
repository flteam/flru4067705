package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Collections;
import java.util.List;

public class SearchBody {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static final int MAX_PER_PAGE = 30;

    public String query = "";

    public Integer page;

    @JsonProperty("per_page")
    public Integer perPage = 30;// [0;30]

    public List<Filter> filters;

    public SearchBody(Integer page, List<Filter> filters) {
        this.page = page;
        this.perPage = MAX_PER_PAGE;
        this.filters = filters;
    }

    public SearchBody(Integer page, Filter filter) {
        this.page = page;
        this.perPage = MAX_PER_PAGE;
        this.filters = Collections.singletonList(filter);
    }

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
