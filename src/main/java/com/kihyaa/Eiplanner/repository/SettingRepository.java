package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {

}
