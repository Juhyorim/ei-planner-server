package com.kihyaa.Eiplanner.service.auth;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.auth.GoogleProfile;
import com.kihyaa.Eiplanner.dto.auth.UserProfile;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2Service {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public String login(GoogleProfile googleProfile, LoginType loginType) {
        Member member = findOrCreateMember(googleProfile, loginType);
        return jwtProvider.createToken(member);
    }

    private Member findOrCreateMember(GoogleProfile googleProfile, LoginType loginType) {
        return memberRepository.findByEmailAndLoginType(googleProfile.getEmail(), loginType)
                .orElseGet(() -> createNewMember(googleProfile, loginType));
    }

    private Member createNewMember(GoogleProfile googleProfile, LoginType loginType) {
        Member newMember = Member.builder()
                .nickname(googleProfile.getName())
                .loginType(loginType)
                .email(googleProfile.getEmail())
                .setting(Setting.defaultSetting())
                .build();
        return memberRepository.save(newMember);
    }
}

