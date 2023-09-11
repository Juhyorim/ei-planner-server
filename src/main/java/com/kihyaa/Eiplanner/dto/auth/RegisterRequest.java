package com.kihyaa.Eiplanner.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "닉네임")
        String nickname,
        @Email
        @Schema(example = "test@gmail.com")
        String email,
        @Schema(example = "testpassword")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야 합니다.")
        String password) {
}
