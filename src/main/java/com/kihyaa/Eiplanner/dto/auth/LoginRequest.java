package com.kihyaa.Eiplanner.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "test@gmail.com")
        String email,
        @Schema(example = "testpassword")
        String password) {
}
