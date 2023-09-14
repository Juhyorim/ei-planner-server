package com.kihyaa.Eiplanner.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessToken {
    @JsonProperty("access_token")
    private String accessToken;
}
