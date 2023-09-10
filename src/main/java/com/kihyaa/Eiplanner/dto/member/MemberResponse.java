package com.kihyaa.Eiplanner.dto.member;

import lombok.Builder;

@Builder
public record MemberResponse(String nickname, String profileImageUrl) {
}
