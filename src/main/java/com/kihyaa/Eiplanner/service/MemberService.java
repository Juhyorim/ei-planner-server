package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.exception.exceptions.ConflictException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
