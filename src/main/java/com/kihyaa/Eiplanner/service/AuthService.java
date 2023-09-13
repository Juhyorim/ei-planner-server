package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.LoginResponse;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.dto.auth.TokenResponse;
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
    public String register(RegisterRequest registerRequest, LoginType loginType) {
        validateRegisterRequest(registerRequest);
        Member member = registerRequest.toEntity(loginType);
        memberRepository.save(member);
        return jwtProvider.createToken(member);
    }

    // 로그인
    public String login(LoginRequest loginRequest) {
        Member member = findValidMember(loginRequest);
        return jwtProvider.createToken(member);
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

    // 유효한 멤버 찾기
    private Member findValidMember(LoginRequest loginRequest) {
        return memberRepository.findByEmail(loginRequest.email())
                .filter(member -> passwordEncoder.matches(loginRequest.password(), member.getPassword()))
                .orElseThrow(() -> new NotFoundException(MessageCode.NOT_FOUND));
    }

    //회원 탈퇴
    public void delete(Member member) {
        memberRepository.delete(member);
    }
}

