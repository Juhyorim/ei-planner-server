package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.response.MapResponse;
import com.kihyaa.Eiplanner.dto.s3.PresignedRequest;
import com.kihyaa.Eiplanner.dto.s3.PresignedResonse;
import com.kihyaa.Eiplanner.dto.member.MemberResponse;
import com.kihyaa.Eiplanner.service.MemberService;
import com.kihyaa.Eiplanner.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImgUrl())
                .build());
    }

    @PutMapping("/nickname")
    public ResponseEntity<MapResponse> updateNickname(@CurrentMember Member member, String nickname) {
        memberService.updateNickname(member, nickname);
        return ResponseEntity.ok(MapResponse.of("nickname", nickname));
    }

    @PutMapping("/profile-image")
    public ResponseEntity<PresignedResonse> getPresignedUrls(@CurrentMember Member member, @Valid @RequestBody PresignedRequest presignedRequest) {
        return ResponseEntity.ok(s3Service.createPresignedUrl(member, presignedRequest));
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<MapResponse> deleteImage(@CurrentMember Member member) {
        memberService.deleteProfileImage(member);
        return ResponseEntity.ok(MapResponse.of("profile_image_url", " "));
    }
}
