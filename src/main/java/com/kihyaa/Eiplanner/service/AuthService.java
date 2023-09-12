package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.LoginResponse;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.exception.exceptions.ConflictException;
import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.utils.JwtProvider;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kihyaa.Eiplanner.exception.MessageCode.CONFLICT_EMAIL;
import static com.kihyaa.Eiplanner.exception.MessageCode.CONFLICT_NICKNAME;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    public String register(RegisterRequest registerRequest) {
        validateRegisterRequest(registerRequest);

        Setting setting = buildSetting();
        Member member = buildMember(registerRequest, setting);

        memberRepository.save(member);

        return createToken(member);
    }

    private Setting buildSetting() {
        return Setting.builder()
                .isViewDateTime(true)
                .autoEmergencySwitch(3)
                .build();
    }

    private void validateRegisterRequest(RegisterRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new ConflictException(CONFLICT_EMAIL);
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ConflictException(CONFLICT_NICKNAME);
        }
    }

    // 로그인
    public String login(LoginRequest loginRequest) {
        Member member = findValidMember(loginRequest);
        return createToken(member);
    }

    // Member 객체 생성
    private Member buildMember(RegisterRequest registerRequest, Setting setting) {
        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        return Member.builder()
                .email(registerRequest.email())
                .nickname(registerRequest.nickname())
                .profileImgUrl("default.jpg")
                .password(encodedPassword)
                .setting(setting)
                .build();
    }

    // 유효한 멤버 찾기
    private Member findValidMember(LoginRequest loginRequest) {
        return memberRepository.findByEmail(loginRequest.email())
                .filter(member -> passwordEncoder.matches(loginRequest.password(), member.getPassword()))
                .orElseThrow(() -> new NotFoundException(MessageCode.NOT_FOUND));
    }

    // 로그인 응답 생성
    private String createToken(Member member) {
        return  jwtProvider.createToken(String.format("%s:%s", member.getId(), "MEMBER"));
    }

    //회원 탈퇴
    public void delete(Member member) {
        memberRepository.delete(member);
    }
}

