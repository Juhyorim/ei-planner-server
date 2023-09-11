package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.dto.request.GetSettingRequest;
import com.kihyaa.Eiplanner.dto.request.SetAutoUrgentRequest;
import com.kihyaa.Eiplanner.dto.request.SetViewDateTimeRequest;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.dto.response.GetSettingResponse;
import com.kihyaa.Eiplanner.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<GetSettingResponse> getSettingDetail(@RequestBody @Valid GetSettingRequest request) {
        GetSettingResponse setting = settingService.getSettingDetail(request.getUser_pk());
        return ResponseEntity.ok(setting);
    }

    @PutMapping("/datetime-display")
    public ResponseEntity<ApiResponse> setViewDateTime(@RequestBody @Valid SetViewDateTimeRequest request){

        boolean result = settingService.setDisplayDateTime(request.getDisplay_date_time(), request.getUser_pk());

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), LocalDateTime.now(), "날짜, 시간 표시 설정이 정상적으로 변경되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/auto-urgent")
    public ResponseEntity<ApiResponse> setAutoUrgent(@RequestBody @Valid SetAutoUrgentRequest request){

        boolean result = settingService.setAutoUrgent(request.getAuto_urgent_day(), request.getUser_pk());

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), LocalDateTime.now(), "Auto Urgent 설정이 정상적으로 변경되었습니다.");

        return ResponseEntity.ok(response);
    }





}
