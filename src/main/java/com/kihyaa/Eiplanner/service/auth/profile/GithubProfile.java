package com.kihyaa.Eiplanner.service.auth.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GithubProfile extends CommonProfile {
    @JsonProperty("id")
    private String id;

    @JsonProperty("login")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar_url")
    private String profileImage;

    @JsonProperty("html_url")
    private String githubLink;
}
