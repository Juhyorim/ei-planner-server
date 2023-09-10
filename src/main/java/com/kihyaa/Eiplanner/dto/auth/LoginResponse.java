package com.kihyaa.Eiplanner.dto.auth;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
