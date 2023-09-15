package com.kihyaa.Eiplanner.service.auth.client;

import com.kihyaa.Eiplanner.service.auth.profile.AccessToken;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractOAuth2Client implements OAuth2Client {
    private final RestTemplate restTemplate;

    @Override
    public CommonProfile oauthLogin(String code) {
        log.info("authcode = {}", code);
        String accessToken = requestAccessToken(code);
        CommonProfile commonProfile = getUserResource(accessToken);
        return new CommonProfile(commonProfile.getId(), commonProfile.getName(), commonProfile.getEmail(), commonProfile.getProfileImage());
    }

    // 액세스 토큰 요청 로직
    protected String requestAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);

        Map<String, String> additionalParams = getAdditionalParams();
        for(Map.Entry<String, String> entry : additionalParams.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        try {
            return Objects.requireNonNull(restTemplate.postForObject(getTokenUri(), entity, AccessToken.class)).getAccessToken();
        } catch (RestClientException e) {
            throw new AuthClientException("인증서버로 액세스 토큰 요청 중 예외가 발생했습니다\n" + "예외 메시지: " + e.getMessage());
        }
    }

    // 사용자 정보 요청 로직
    protected CommonProfile getUserResource(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<? extends CommonProfile> response = restTemplate.exchange(getResourceUri(), HttpMethod.GET, entity, getProfileClass());
            log.info("getProfileClass = {}", getProfileClass().toString());
            log.info("response.getbody = {}", response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new AuthClientException("사용자 정보 요청 중 예외가 발생했습니다.\n예외 메시지: " + e.getMessage());
        }
    }

    // 각 구현체에서 설정해야 하는 부분
    protected abstract Map<String, String> getAdditionalParams();
    protected abstract String getTokenUri();
    protected abstract String getResourceUri();
    protected abstract Class<? extends CommonProfile> getProfileClass();
}
