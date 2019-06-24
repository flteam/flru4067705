package flteam.flru4067705.model;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class ProfileCsv {

    private static final String DELIMITER = ";";

    public String fio;
    public String country;
    public String email;
    public String about;
    public String site;
    public String fb;
    public String linkedId;
    public String instagram;
    public List<String> skills;

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(Optional.ofNullable(fio).orElse(""));
        stringJoiner.add(Optional.ofNullable(country).orElse(""));
        stringJoiner.add(Optional.ofNullable(email).orElse(""));
        stringJoiner.add(Optional.ofNullable(about).orElse(""));
        stringJoiner.add(Optional.ofNullable(site).orElse(""));
        stringJoiner.add(Optional.ofNullable(fb).orElse(""));
        stringJoiner.add(Optional.ofNullable(linkedId).orElse(""));
        stringJoiner.add(Optional.ofNullable(instagram).orElse(""));
        stringJoiner.add(String.join(DELIMITER, skills));
        return stringJoiner.toString();
    }

}
