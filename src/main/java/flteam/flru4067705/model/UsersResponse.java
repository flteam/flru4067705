package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UsersResponse {

    @JsonProperty("total_count")
    public Integer totalCount;

    @JsonProperty("data")
    public List<Profile> profiles;

}
