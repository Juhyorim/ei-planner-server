package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.LoginResponse;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.dto.auth.TokenResponse;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
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

import java.util.Optional;

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
        String password = passwordEncoder.encode(registerRequest.password());
        Member member = registerRequest.toEntity(password, loginType);
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

    private Member findValidMember(LoginRequest loginRequest) {
        Optional<Member> optionalMember = memberRepository.findByEmail(loginRequest.email());

        if (optionalMember.isEmpty()) {
            throw new NotFoundException("회원 정보가 없습니다.");
        }

        Member member = optionalMember.get();
        log.info("loginRequest의 비밀번호 = {}", loginRequest.password());
        log.info("데이터베이스의 비밀번호 = {}", member.getPassword());

        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new AuthClientException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    //회원 탈퇴
    public void delete(Member member) {
        memberRepository.delete(member);
    }
}

