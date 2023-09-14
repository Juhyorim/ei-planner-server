package com.kihyaa.Eiplanner.security.utils;

import com.kihyaa.Eiplanner.config.properties.GoogleOAuth2Properties;
import com.kihyaa.Eiplanner.dto.auth.AccessToken;
import com.kihyaa.Eiplanner.dto.auth.GoogleProfile;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuth2GoogleClient {
    private final GoogleOAuth2Properties googleOAuth2Properties;
    private final RestTemplate restTemplate;

    // OAuth 로그인
    public GoogleProfile oauthLogin(String code) {
        String accessToken = requestAccessToken(code);
        return getUserResource(accessToken);
    }

    private String requestAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", googleOAuth2Properties.getClientId());
        body.add("client_secret", googleOAuth2Properties.getClientSecret());
        body.add("redirect_uri", googleOAuth2Properties.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        try {
            return Objects.requireNonNull(restTemplate.postForObject(googleOAuth2Properties.getTokenUri(), entity, AccessToken.class)).getAccessToken();

        } catch (RestClientException e) {
            throw new AuthClientException("인증서버로 액세스 토큰 요청 중 예외가 발생했습니다\n" + "예외 메시지: " + e.getMessage());
        }
    }

    private GoogleProfile getUserResource(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleProfile> response = restTemplate.exchange(googleOAuth2Properties.getResourceUri(), HttpMethod.GET, entity, GoogleProfile.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new AuthClientException("사용자 정보 요청 중 예외가 발생했습니다.\n예외 메시지: " + e.getMessage());
        }
    }
}
