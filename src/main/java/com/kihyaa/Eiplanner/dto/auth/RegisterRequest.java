package com.kihyaa.Eiplanner.dto.auth;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @Schema(example = "닉네임")
        String nickname,
        @Email
        @Schema(example = "test@gmail.com")
        String email,
        @Schema(example = "testpassword")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야 합니다.")
        String password) {

        public static RegisterRequest of(String nickname, String email, String password) {
                return new RegisterRequest(nickname, email, password);
        }

        public Member toEntity(LoginType loginType) {
                return Member.builder()
                        .nickname(this.nickname)
                        .email(this.email)
                        .password(this.password)
                        .setting(Setting.defaultSetting())
                        .loginType(loginType)
                        .build();
        }
}
