package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.response.GetSettingResponse;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingService {
    final MemberRepository memberRepository;
    final SettingRepository settingRepository;

    @Transactional
    public boolean setDisplayDateTime(Boolean isView, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("해당 멤버가 존재하지 않습니다."));
        member.getSetting().setIsViewDateTime(isView);
        return true;
    }

    @Transactional
    public boolean setAutoUrgent(int auto_urgent_day, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("해당 멤버가 존재하지 않습니다."));
        member.getSetting().setAutoEmergencySwitch(auto_urgent_day);
        return true;
    }

    public GetSettingResponse getSettingDetail(Long memberId) {
        validateMemberExists(memberId);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("해당 멤버가 존재하지 않습니다."));
        Setting setting = member.getSetting();
        return GetSettingResponse.of(setting.getAutoEmergencySwitch(), setting.getIsViewDateTime());
    }

    private void validateMemberExists(Long memberId) {
        // Todo : 해당 memberId가 본인 Id　인지 검증 하는 코드 추가

        memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("해당 멤버가 존재하지 않습니다."));

    }
}
