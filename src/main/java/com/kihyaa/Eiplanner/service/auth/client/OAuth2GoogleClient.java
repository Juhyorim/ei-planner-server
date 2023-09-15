package com.kihyaa.Eiplanner.service.auth.client;

import com.kihyaa.Eiplanner.config.properties.GoogleOAuth2Properties;
import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;
import com.kihyaa.Eiplanner.service.auth.profile.GoogleProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("google")
public class OAuth2GoogleClient extends AbstractOAuth2Client {
    private final GoogleOAuth2Properties googleOAuth2Properties;

    @Autowired
    public OAuth2GoogleClient(RestTemplate restTemplate, GoogleOAuth2Properties googleOAuth2Properties) {
        super(restTemplate);
        this.googleOAuth2Properties = googleOAuth2Properties;
    }

    // 구현체에서 필요한 파라미터 설정
    @Override
    protected Map<String, String> getAdditionalParams() {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", googleOAuth2Properties.getClientId());
        params.put("client_secret", googleOAuth2Properties.getClientSecret());
        params.put("redirect_uri", googleOAuth2Properties.getRedirectUri());
        params.put("grant_type", "authorization_code");
        return params;
    }

    // 액세스 토큰을 얻기 위한 URI
    @Override
    protected String getTokenUri() {
        return googleOAuth2Properties.getTokenUri();
    }

    // 사용자 정보를 얻기 위한 URI
    @Override
    protected String getResourceUri() {
        return googleOAuth2Properties.getResourceUri();
    }

    // 사용자 프로필 클래스 타입 반환
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return GoogleProfile.class;
    }
}
