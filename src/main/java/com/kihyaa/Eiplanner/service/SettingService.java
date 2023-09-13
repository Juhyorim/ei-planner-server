package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.response.GetSettingResponse;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingService {
    final MemberRepository memberRepository;
    final SettingRepository settingRepository;

    @Transactional
    public void setDisplayDateTime(Boolean isView, Long memberId) {
        Member member = getMemberOrElseThrow(memberId);
        member.getSetting().setIsViewDateTime(isView);
    }

    @Transactional
    public void setAutoUrgent(int auto_urgent_time, Long memberId) {
        Member member = getMemberOrElseThrow(memberId);
        if (auto_urgent_time < 0){ throw new IllegalArgumentException("auto_urgent_time은 0 혹은 양수여야 합니다.");}
        member.getSetting().setAutoEmergencySwitch(auto_urgent_time);
    }

    public GetSettingResponse getSettingDetail(Long memberId) {
        Member member = getMemberOrElseThrow(memberId);
        Setting setting = member.getSetting();
        return GetSettingResponse.of(setting.getAutoEmergencySwitch(), setting.getIsViewDateTime());
    }

    private Member getMemberOrElseThrow(Long memberId) {
        return  memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("해당 멤버가 존재하지 않습니다."));
    }
}
