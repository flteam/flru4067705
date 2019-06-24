package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class SearchBody {

    public static final int MAX_PER_PAGE = 30;

    public String query = "";

    public Long page;

    @JsonProperty("per_page")
    public Integer perPage = 30;// [0;30]

    public List<Filter> filters;

    public SearchBody(Long page, List<Filter> filters) {
        this.page = page;
        this.perPage = MAX_PER_PAGE;
        this.filters = filters;
    }

    public SearchBody(Long page, Filter filter) {
        this.page = page;
        this.perPage = MAX_PER_PAGE;
        this.filters = Collections.singletonList(filter);
    }

}
