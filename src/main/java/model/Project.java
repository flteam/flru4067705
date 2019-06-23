package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {

    public Long id;

    @JsonProperty("smaller_square_cover_url")
    public String smallerSquareCoverUrl;

    public String url;

    public String title;

}
