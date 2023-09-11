package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.s3.PresignedRequest;
import com.kihyaa.Eiplanner.dto.s3.PresignedResonse;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.dto.member.MemberResponse;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.service.MemberService;
import com.kihyaa.Eiplanner.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<MemberResponse> getMember(@CurrentMember Member member) {
        return ResponseEntity.ok(
                MemberResponse.builder()
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImgUrl())
                .build());
    }

    @PutMapping("/nickname")
    public ResponseEntity<ApiResponse> updateNickname(@CurrentMember Member member, String nickname) {
        memberService.updateNickname(member, nickname);
        return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
    }

    @PutMapping("/profile-image")
    public ResponseEntity<PresignedResonse> getPresignedUrls(@CurrentMember Member member, @Valid @RequestBody PresignedRequest presignedRequest) {
        return ResponseEntity.ok(s3Service.createPresignedUrl(member, presignedRequest));
    }
}
