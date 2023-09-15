package com.kihyaa.Eiplanner.security.utils;

import com.kihyaa.Eiplanner.config.properties.GithubOAuth2Properties;
import com.kihyaa.Eiplanner.dto.auth.AccessToken;
import com.kihyaa.Eiplanner.dto.auth.GithubProfile;
import com.kihyaa.Eiplanner.dto.auth.GoogleProfile;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2GithubClient {
    private final GithubOAuth2Properties githubOAuth2Properties;
    private final RestTemplate restTemplate;

    public GithubProfile oauthLogin(String code) {
        String accessToken = requestAccessToken(code);
        return getUserResource(accessToken);
    }

    private String requestAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", githubOAuth2Properties.getClientId());
        body.add("client_secret", githubOAuth2Properties.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        try {
            return Objects.requireNonNull(restTemplate.postForObject(githubOAuth2Properties.getTokenUri(), entity, AccessToken.class)).getAccessToken();

        } catch (RestClientException e) {
            throw new AuthClientException("인증서버로 액세스 토큰 요청 중 예외가 발생했습니다\n" + "예외 메시지: " + e.getMessage());
        }
    }

    private GithubProfile getUserResource(String accessToken) {
        log.info("token = {}", accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<GithubProfile> response = restTemplate.exchange(githubOAuth2Properties.getResourceUri(), HttpMethod.GET, entity, GithubProfile.class);

        GithubProfile githubProfile = response.getBody();

        assert githubProfile != null;
        setDefaultEmailIfNull(githubProfile);

        return githubProfile;
    }

    private void setDefaultEmailIfNull(GithubProfile githubProfile) {
        if (githubProfile.getEmail() == null) {
            githubProfile.setEmail(githubProfile.getId());
        }
    }

}