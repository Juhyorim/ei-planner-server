package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.member.MemberResponse;
import com.kihyaa.Eiplanner.exception.exceptions.ConflictException;
import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kihyaa.Eiplanner.exception.MessageCode.CONFLICT_NICKNAME;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void updateNickname(Member member, String newNickname) {
        if (memberRepository.existsByNickname(newNickname)) {
            throw new ConflictException(CONFLICT_NICKNAME);
        }

        member.changeNickname(newNickname);
        memberRepository.save(member);
    }

    public void deleteProfileImage(Member member) {
        member.deleteProfileImg();
        memberRepository.save(member);
    }

    public MemberResponse getInfo(Member member) {
        Setting setting = member.getSetting();

        return MemberResponse.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImageUrl(member.getProfileImgUrl())
            .isViewDateTime(setting.getIsViewDateTime())
            .build();
    }
}
