package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Skill {

    @JsonProperty("skill_name")
    public String skillName;

    public Integer id;

    public String getSkillName() {
        return skillName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(skillName, skill.skillName) &&
                Objects.equals(id, skill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillName, id);
    }

}
