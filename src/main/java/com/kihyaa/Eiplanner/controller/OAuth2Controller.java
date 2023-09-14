package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.dto.auth.GithubProfile;
import com.kihyaa.Eiplanner.dto.auth.GoogleProfile;
import com.kihyaa.Eiplanner.dto.auth.UserProfile;
import com.kihyaa.Eiplanner.security.utils.OAuth2GithubClient;
import com.kihyaa.Eiplanner.security.utils.OAuth2GoogleClient;
import com.kihyaa.Eiplanner.service.auth.OAuth2Service;
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
    private final OAuth2GoogleClient oAuth2GoogleClient;
    private final OAuth2GithubClient oAuth2GithubClient;
    private final OAuth2Service oAuth2Service;

    @GetMapping("/google")
    public void googleLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        GoogleProfile googleProfile = oAuth2GoogleClient.oauthLogin(code);
        String token = oAuth2Service.login(googleProfile, LoginType.GOOGLE);
        response.sendRedirect(frontendUrl + "?token=" + token);
    }

    @GetMapping("/github")
    public void githubLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        GithubProfile githubProfile = oAuth2GithubClient.oauthLogin(code);
        String token = oAuth2Service.login(githubProfile.toCommon(), LoginType.GITHUB);
        log.info("OAuth2 token = {}", token);
        response.sendRedirect(frontendUrl + "?token=" + token);
    }
}
