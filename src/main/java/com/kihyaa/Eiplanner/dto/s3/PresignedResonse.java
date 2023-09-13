package com.kihyaa.Eiplanner.dto.s3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.net.URL;

@Builder
public record PresignedResonse(
        @JsonProperty("changed_url") String changedUrl,
        @JsonProperty("presiged_url") URL ProfileImageUrl) {
}
