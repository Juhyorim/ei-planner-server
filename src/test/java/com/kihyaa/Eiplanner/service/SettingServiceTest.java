package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.IntegrationTestSupport;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.dto.request.SetAutoUrgentRequest;
import com.kihyaa.Eiplanner.dto.request.SetViewDateTimeRequest;
import com.kihyaa.Eiplanner.dto.response.GetSettingResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class SettingServiceTest extends IntegrationTestSupport {
    private final String testEmail = "testUser@gmail.com";

    @DisplayName("요청 받은 Boolean 값으로 시간 및 날짜 보기 설정를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setDisplayDateTime(Boolean isDisplayDateTime){

        //given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);

        memberRepository.save(member);
        settingRepository.save(setting);

        Member findMember = memberRepository.findByEmail(testEmail).get();
        SetViewDateTimeRequest request = new SetViewDateTimeRequest(isDisplayDateTime);

        //when
        settingservice.setDisplayDateTime(request.getIs_display_date_time(), findMember.getId());

        //then
        assertThat(member.getSetting().getIsViewDateTime()).isEqualTo(isDisplayDateTime);
    }

    @DisplayName("요청 받은 0이나 양수로 자동 긴급 전환 시간 설정를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void setAutoUrgent(int autoUrgentTime){

        //given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);

        memberRepository.save(member);
        settingRepository.save(setting);
        Member findMember = memberRepository.findByEmail(testEmail).get();

        SetAutoUrgentRequest request = new SetAutoUrgentRequest(autoUrgentTime);

        //when
        settingservice.setAutoUrgent(request.getAuto_urgent_day(), findMember.getId());

        //then
        assertThat(member.getSetting().getAutoEmergencySwitch()).isEqualTo(autoUrgentTime);
    }

    @DisplayName("요청 받은 값이 음수라면 자동 긴급 전환 시간 설정를 변경할 수 없다.")
    @Test
    void setNegativeAutoUrgent(){

        //given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);

        memberRepository.save(member);
        settingRepository.save(setting);
        Member findMember = memberRepository.findByEmail(testEmail).get();

        SetAutoUrgentRequest request = new SetAutoUrgentRequest(-1);

        //when & then
        assertThatThrownBy(() -> settingservice.setAutoUrgent(request.getAuto_urgent_day(), findMember.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("auto_urgent_time은 0 혹은 양수여야 합니다.");

    }



    @DisplayName("본인의 설정 값을 조회할 수 있다.")
    @Test
    void getSettingDetail(){
        //given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);

        memberRepository.save(member);
        settingRepository.save(setting);
        Member findMember = memberRepository.findByEmail(testEmail).get();
        //when
        GetSettingResponse response = settingservice.getSettingDetail(findMember.getId());

        //then
        assertThat(member.getSetting().getAutoEmergencySwitch()).isEqualTo(response.getAuto_urgent());
        assertThat(member.getSetting().getIsViewDateTime()).isEqualTo(response.getDatetime_display());
    }




}
