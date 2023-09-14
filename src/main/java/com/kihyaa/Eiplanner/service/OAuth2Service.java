package com.kihyaa.Eiplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kihyaa.Eiplanner.config.properties.OAuth2Properties;
import com.kihyaa.Eiplanner.domain.LoginType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.auth.LoginRequest;
import com.kihyaa.Eiplanner.dto.auth.RegisterRequest;
import com.kihyaa.Eiplanner.exception.exceptions.AuthClientException;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2Service {
    private final OAuth2Properties oAuth2Properties;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public String oauthLogin(String code) {
        String accessToken = requestAccessToken(code);
        JsonNode userResourceNode = getUserResource(accessToken);
        String email = userResourceNode.get("email").asText();
        String name = userResourceNode.get("name").asText();

        Optional<Member> optionalMember = memberRepository.findByEmailAndLoginType(email, LoginType.GOOGLE);

        if (optionalMember.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(" ");
            RegisterRequest registerRequest = RegisterRequest.of(name, email, encodedPassword);
            return authService.register(registerRequest, LoginType.GOOGLE);
        }

        return authService.login(LoginRequest.of(email));
    }

    public String requestAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", oAuth2Properties.getClientId());
        body.add("client_secret", oAuth2Properties.getClientSecret());
        body.add("redirect_uri", oAuth2Properties.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<JsonNode> responseNode = restTemplate.exchange(oAuth2Properties.getTokenUri(), HttpMethod.POST, entity, JsonNode.class);
            JsonNode accessTokenNode = responseNode.getBody();

            assert accessTokenNode != null;

            return accessTokenNode.get("access_token").asText();
        } catch (RestClientException e) {
            throw new AuthClientException("인증서버로 액세스 토큰 요청 중 예외가 발생했습니다\n" + "예외 메시지: " + e.getMessage());
        }
    }

    private JsonNode getUserResource(String accessToken) {
        String resourceUri = oAuth2Properties.getResourceUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }
}
