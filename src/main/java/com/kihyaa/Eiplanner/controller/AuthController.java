package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Value("${frontendUrl}")
    private String frontendUrl;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest,  HttpServletResponse response) throws IOException {

        String token = authService.register(registerRequest);
        String redirectUrl = frontendUrl + "?token=" + token;
        log.info("일반 회원가입 token = {}", token);
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        String token = authService.login(loginRequest);
        String redirectUrl = frontendUrl + "?token=" + token;
        log.info("일반 로그인 token = {}", token);
        response.sendRedirect(redirectUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(@CurrentMember Member member) {

        authService.delete(member);

        return ApiResponse.createResponse(MessageCode.SUCCESS_DELETE_RESOURCE, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
    }
}
