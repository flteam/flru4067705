package flteam.flru4067705.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class Profile {

    public Long id;

    public String username;

    @JsonProperty("large_avatar_url")
    public String largeAvatarUrl;

    @JsonProperty("small_cover_url")
    public String smallCoverUrl;

    @JsonProperty("is_staff")
    public Boolean isStaff;

    @JsonProperty("pro_member")
    public Boolean proMember;

    @JsonProperty("artstation_profile_url")
    public String artstationProfileUrl;

    @JsonProperty("likes_count")
    public Long likesCount;

    @JsonProperty("followers_count")
    public Long followersCount;

    @JsonProperty("available_full_time")
    public Boolean availableFullTime;

    @JsonProperty("available_contract")
    public Boolean availableContract;

    @JsonProperty("available_freelance")
    public Boolean availableFreelance;

    @JsonProperty("location")
    public String location;

    @JsonProperty("project_views_count")
    public Long projectViewsCount;

    @JsonProperty("full_name")
    public String fullName;

    @JsonProperty("headline")
    public String headline;

    @JsonProperty("followed")
    public Boolean followed;

    @JsonProperty("following_back")
    public Boolean followingBack;

    @JsonProperty("sample_projects")
    public List<Project> sampleProjects;

    public List<Skill> skills;

    public List<Software> softwares;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id) &&
                Objects.equals(username, profile.username) &&
                Objects.equals(largeAvatarUrl, profile.largeAvatarUrl) &&
                Objects.equals(smallCoverUrl, profile.smallCoverUrl) &&
                Objects.equals(isStaff, profile.isStaff) &&
                Objects.equals(proMember, profile.proMember) &&
                Objects.equals(artstationProfileUrl, profile.artstationProfileUrl) &&
                Objects.equals(likesCount, profile.likesCount) &&
                Objects.equals(followersCount, profile.followersCount) &&
                Objects.equals(availableFullTime, profile.availableFullTime) &&
                Objects.equals(availableContract, profile.availableContract) &&
                Objects.equals(availableFreelance, profile.availableFreelance) &&
                Objects.equals(location, profile.location) &&
                Objects.equals(projectViewsCount, profile.projectViewsCount) &&
                Objects.equals(fullName, profile.fullName) &&
                Objects.equals(headline, profile.headline) &&
                Objects.equals(followed, profile.followed) &&
                Objects.equals(followingBack, profile.followingBack) &&
                Objects.equals(sampleProjects, profile.sampleProjects) &&
                Objects.equals(skills, profile.skills) &&
                Objects.equals(softwares, profile.softwares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, largeAvatarUrl, smallCoverUrl, isStaff, proMember, artstationProfileUrl, likesCount, followersCount, availableFullTime, availableContract, availableFreelance, location, projectViewsCount, fullName, headline, followed, followingBack, sampleProjects, skills, softwares);
    }

}
