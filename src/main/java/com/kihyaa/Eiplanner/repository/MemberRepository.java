package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);
    Optional<Member> findByUidAndLoginType(String Uid, LoginType loginType);
}
