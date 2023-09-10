package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.member.MemberResponse;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    @GetMapping
    public MemberResponse getMember(@CurrentMember Member member) {
        return MemberResponse.builder()
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImgUrl())
                .build();
    }
}
