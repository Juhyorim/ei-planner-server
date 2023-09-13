package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2/code")
public class OAuth2Controller {
    @Value("${frontendUrl}")
    private String frontendUrl;
    private final OAuth2Service oAuth2Service;

    @GetMapping("/google")
    public void googleLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        String token = oAuth2Service.oauthLogin(code);
        response.sendRedirect(frontendUrl+"?"+token);
    }
}
