package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.exception.MessageCode;
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

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<GetSettingResponse> getSettingDetail(@CurrentMember Member member) {
        
        return ResponseEntity.ok(settingService.getSettingDetail(member.getId()));
    }

    @PutMapping("/datetime-display")
    public ResponseEntity<ApiResponse> setViewDateTime(@RequestBody @Valid SetViewDateTimeRequest request,
                                                       @CurrentMember Member member){

        settingService.setDisplayDateTime(request.getDisplay_date_time(), member.getId());

        return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
    }

    @PutMapping("/auto-urgent")
    public ResponseEntity<ApiResponse> setAutoUrgent(@RequestBody @Valid SetAutoUrgentRequest request,
                                                     @CurrentMember Member member){

        settingService.setAutoUrgent(request.getAuto_urgent_day(), member.getId());

        return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
    }





}
