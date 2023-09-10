package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {
}
