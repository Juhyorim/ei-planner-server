package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
