package com.kihyaa.Eiplanner.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubProfile {
    @JsonProperty("id")
    private String id;

    @JsonProperty("login")
    private String name;

    @JsonProperty("email")
    private String email;

    public GoogleProfile toCommon() {
        return new GoogleProfile(name, email);
    }
}
