package flteam.flru4067705.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter {

    public String field;
    public String method = "include";
    public List<String> value;

    private Filter(String field, List<String> value) {
        this.field = field;
        this.value = value;
    }

    public static Filter buildCountryFilter(Country country) {
        return new Filter("countries", Collections.singletonList(country.id));
    }

    public static List<Filter> buildCountryAndSkillFilters(Country country, Skill skill) {
        return Stream.of(
                new Filter("countries", Collections.singletonList(country.id)),
                new Filter("skill_ids", Collections.singletonList(String.valueOf(skill.id)))
        ).collect(Collectors.toList());
    }

    public static Filter buildCountriesFilter(List<Country> countries) {
        return new Filter(
                "countries",
                countries.stream()
                        .map(Country::getId)
                        .collect(Collectors.toList())
        );
    }

}
