package com.kihyaa.Eiplanner.dto.s3;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedRequest(
        @Schema(example = "test")
        @JsonProperty("file_name")
        String fileName,

        @Schema(example = "image/jpeg")
        @JsonProperty("file_type")
        String fileType) {
}
