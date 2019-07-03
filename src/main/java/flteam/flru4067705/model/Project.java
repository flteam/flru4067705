package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Project {

    public Long id;

    @JsonProperty("smaller_square_cover_url")
    public String smallerSquareCoverUrl;

    public String url;

    public String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) &&
                Objects.equals(smallerSquareCoverUrl, project.smallerSquareCoverUrl) &&
                Objects.equals(url, project.url) &&
                Objects.equals(title, project.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, smallerSquareCoverUrl, url, title);
    }

}
