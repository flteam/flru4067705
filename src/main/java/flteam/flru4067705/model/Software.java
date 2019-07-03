package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Software {

    @JsonProperty("software_name")
    public String softwareName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Software software = (Software) o;
        return Objects.equals(softwareName, software.softwareName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(softwareName);
    }

}
