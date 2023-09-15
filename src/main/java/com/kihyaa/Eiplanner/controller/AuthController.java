package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.response.MapResponse;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.service.auth.NomalAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final NomalAuthService nomalAuthService;

    @PostMapping("/register")
    public ResponseEntity<MapResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {

        String token = nomalAuthService.register(registerRequest, LoginType.nomal);

        return ResponseEntity.ok(MapResponse.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<MapResponse> login(@RequestBody LoginRequest loginRequest) {

        String token  = nomalAuthService.login(loginRequest, LoginType.nomal);
        log.info("일반 로그인 token = {}", token);

        return ResponseEntity.ok(MapResponse.of("token", token));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@CurrentMember Member member) {

        nomalAuthService.delete(member);

        return ApiResponse.createResponse(MessageCode.SUCCESS_DELETE_RESOURCE, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
    }
}
