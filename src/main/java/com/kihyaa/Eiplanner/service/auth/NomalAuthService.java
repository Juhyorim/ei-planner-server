package com.kihyaa.Eiplanner.service.auth;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
import com.kihyaa.Eiplanner.exception.exceptions.ConflictException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.utils.JwtProvider;
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
public class NomalAuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest registerRequest, LoginType loginType) {
        validateRegisterRequest(registerRequest);
        Member member = createMember(registerRequest, loginType);
        memberRepository.save(member);
        return jwtProvider.createToken(member);
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest loginRequest, LoginType loginType) {
        Member member = findValidMember(loginRequest, loginType);
        return jwtProvider.createToken(member);
    }

    //회원 탈퇴
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new ConflictException(CONFLICT_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ConflictException(CONFLICT_NICKNAME);
        }
    }

    private Member createMember(RegisterRequest registerRequest, LoginType loginType) {
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        return registerRequest.toEntity(encodedPassword, loginType);
    }

    // 유효한 멤버 찾기
    private Member findValidMember(LoginRequest loginRequest, LoginType loginType) {
        return memberRepository.findByEmailAndLoginType(loginRequest.email(), loginType)
                .filter(member -> passwordEncoder.matches(loginRequest.password(), member.getPassword()))
                .orElseThrow(() -> new AuthClientException("비밀번호가 일치하지 않거나 회원 정보가 없습니다."));
    }
}

