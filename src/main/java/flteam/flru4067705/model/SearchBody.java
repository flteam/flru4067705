package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchBody {

    public static final int MAX_PER_PAGE = 30;

    public String query;

    public Long page;

    @JsonProperty("per_page")
    public Integer perPage;// [0;30]

    public SearchBody(String query, Long page, Integer perPage) {
        if (perPage < 0 || perPage > MAX_PER_PAGE) {
            throw new IllegalArgumentException("perPage must be from [0;30]!");
        }
        this.query = query;
        this.page = page;
        this.perPage = perPage;
    }

    public SearchBody(String query, Long page) {
        this.query = query;
        this.page = page;
        this.perPage = MAX_PER_PAGE;
    }

}
