package com.kihyaa.Eiplanner.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LoginResponse(String token, String email, @JsonProperty("profile_image_url") String profileImageUrl, String nickname) {
}
