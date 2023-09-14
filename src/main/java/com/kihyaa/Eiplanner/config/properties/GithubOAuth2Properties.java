package com.kihyaa.Eiplanner.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.github")
public class GithubOAuth2Properties {
    private String clientId;
    private String clientSecret;
    private String tokenUri;
    private String resourceUri;
}
