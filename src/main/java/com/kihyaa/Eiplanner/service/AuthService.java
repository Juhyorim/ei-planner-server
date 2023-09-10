package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.Exception.NotFoundException;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.LoginResponse;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    public void register(RegisterRequest registerRequest) {
        Member member = buildMember(registerRequest);
        memberRepository.save(member);
    }

    // 로그인
    public LoginResponse login(LoginRequest loginRequest) {
        Member member = findValidMember(loginRequest);
        return createLoginResponse(member);
    }

    // Member 객체 생성
    private Member buildMember(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        return Member.builder()
                .email(registerRequest.email())
                .nickname(registerRequest.nickname())
                .profileImgUrl("default.jpg")
                .password(encodedPassword)
                .build();
    }

    // 유효한 멤버 찾기
    private Member findValidMember(LoginRequest loginRequest) {
        return memberRepository.findByEmail(loginRequest.email())
                .filter(member -> passwordEncoder.matches(loginRequest.password(), member.getPassword()))
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일이거나 잘못된 비밀번호입니다."));
    }

    // 로그인 응답 생성
    private LoginResponse createLoginResponse(Member member) {
        String token = jwtProvider.createToken(String.format("%s:%s", member.getId(), "MEMBER"));

        return LoginResponse.builder()
                .token(token)
                .build();
    }
}

