package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Skill {

    @JsonProperty("skill_name")
    public String skillName;

    public String getSkillName() {
        return skillName;
    }

}