package com.kihyaa.Eiplanner.security.custom;

import com.kihyaa.Eiplanner.exception.exceptions.JwtAuthenticationException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.security.wrapper.MemberWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userInfo) throws UsernameNotFoundException {
        String[] idAndRoleArray = userInfo.split(":");
        validateUserInfo(idAndRoleArray);

        String id = idAndRoleArray[0];

        return loadMemberById(id);
    }

    private UserDetails loadMemberById(String id) {
        return memberRepository.findById(Long.valueOf(id))
                .map(MemberWrapper::new)
                .orElseThrow(() -> new JwtAuthenticationException("해당 토큰과 맞는 사용자가 존재하지 않습니다!"));
    }

    private static void validateUserInfo(String[] idAndRoleArray) {
        if (idAndRoleArray.length != 2 || idAndRoleArray[0].isEmpty() || idAndRoleArray[1].isEmpty()) {
            throw new JwtAuthenticationException("페이로드가 유효하지 않습니다!");
        }
    }
}