package com.kihyaa.Eiplanner.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record MemberResponse(
        String nickname,
        @JsonProperty("profile_image_url") String profileImageUrl,
        String email,
        @JsonProperty("is_view_date_time") Boolean isViewDateTime) {
}
