package com.kihyaa.Eiplanner.service.auth;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.utils.JwtProvider;
import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;
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

    public String login(CommonProfile commonProfile, LoginType loginType) {
        Member member = findOrCreateMember(commonProfile, loginType);
        String token = jwtProvider.createToken(member);
        log.info("{}의 토큰 : {}", loginType, token);
        return token;
    }

    // CommonProfile을 파라미터로 받음
    private Member findOrCreateMember(CommonProfile commonProfile, LoginType loginType) {
        return memberRepository.findByUidAndLoginType(commonProfile.getId(), loginType)
                .orElseGet(() -> createNewMember(commonProfile, loginType));
    }

    // CommonProfile을 파라미터로 받음
    private Member createNewMember(CommonProfile commonProfile, LoginType loginType) {
        log.info("UID = {}", commonProfile.getId());
        Member newMember = Member.builder()
                .nickname(commonProfile.getName())
                .loginType(loginType)
                .email(commonProfile.getEmail())
                .uid(commonProfile.getId())
                .setting(Setting.defaultSetting())
                .build();
        return memberRepository.save(newMember);
    }
}
