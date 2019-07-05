package flteam.flru4067705.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class ProfileCsv {

    private static final String DELIMITER = ";";

    public String fullName;
    public String city;
    public String country;
    public String email;
    public String aboutUrl;
    public String webSite;
    public String fbUrl;
    public String linkedInUrl;
    public String instagramUrl;
    public List<String> skills;

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(Optional.ofNullable(fullName).orElse(""));
        stringJoiner.add(Optional.ofNullable(country).orElse(""));
        stringJoiner.add(Optional.ofNullable(city).orElse(""));
        stringJoiner.add(Optional.ofNullable(email).orElse(""));
        stringJoiner.add(Optional.ofNullable(aboutUrl).orElse(""));
        stringJoiner.add(Optional.ofNullable(webSite).orElse(""));
        stringJoiner.add(Optional.ofNullable(fbUrl).orElse(""));
        stringJoiner.add(Optional.ofNullable(linkedInUrl).orElse(""));
        stringJoiner.add(Optional.ofNullable(instagramUrl).orElse(""));
        stringJoiner.add(String.join(DELIMITER, skills));
        return stringJoiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileCsv that = (ProfileCsv) o;
        return Objects.equals(fullName, that.fullName) &&
                Objects.equals(city, that.city) &&
                Objects.equals(country, that.country) &&
                Objects.equals(email, that.email) &&
                Objects.equals(aboutUrl, that.aboutUrl) &&
                Objects.equals(webSite, that.webSite) &&
                Objects.equals(fbUrl, that.fbUrl) &&
                Objects.equals(linkedInUrl, that.linkedInUrl) &&
                Objects.equals(instagramUrl, that.instagramUrl) &&
                Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, city, country, email, aboutUrl, webSite, fbUrl, linkedInUrl, instagramUrl, skills);
    }

}
