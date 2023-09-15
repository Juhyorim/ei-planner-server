package com.kihyaa.Eiplanner.service.auth.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleProfile extends CommonProfile {
    @JsonProperty("id")
    private String id;
    private String name;
    private String email;
}
