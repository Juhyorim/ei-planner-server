package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;
import com.kihyaa.Eiplanner.service.auth.client.OAuth2Client;
import com.kihyaa.Eiplanner.service.auth.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2/code")
public class OAuth2Controller {
    @Value("${frontendUrl}")
    private String frontendUrl;
    private final Map<String, OAuth2Client> oAuth2Clients;
    private final OAuth2Service oAuth2Service;


    @GetMapping("/{provider}")
    public void oauthLogin(@PathVariable LoginType provider, @RequestParam String code, HttpServletResponse response) throws IOException {
        OAuth2Client oAuth2Client = oAuth2Clients.get(provider.name());

        CommonProfile profile = oAuth2Client.oauthLogin(code);
        String token = oAuth2Service.login(profile, provider);

        response.sendRedirect(frontendUrl + "?token=" + token);
    }
}
