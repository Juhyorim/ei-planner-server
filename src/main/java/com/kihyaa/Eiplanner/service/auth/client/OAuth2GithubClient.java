package com.kihyaa.Eiplanner.service.auth.client;

import com.kihyaa.Eiplanner.config.properties.GithubOAuth2Properties;
import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;
import com.kihyaa.Eiplanner.service.auth.profile.GithubProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("github")
public class OAuth2GithubClient extends AbstractOAuth2Client{
    private final GithubOAuth2Properties githubOAuth2Properties;

    @Autowired
    public OAuth2GithubClient(RestTemplate restTemplate, GithubOAuth2Properties githubOAuth2Properties) {
        super(restTemplate);
        this.githubOAuth2Properties = githubOAuth2Properties;
    }

    @Override
    protected Map<String, String> getAdditionalParams() {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", githubOAuth2Properties.getClientId());
        params.put("client_secret", githubOAuth2Properties.getClientSecret());
        return params;
    }

    @Override
    protected String getTokenUri() {
        return githubOAuth2Properties.getTokenUri();
    }

    @Override
    protected String getResourceUri() {
        return githubOAuth2Properties.getResourceUri();
    }

    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return GithubProfile.class;
    }

    @Override
    protected CommonProfile getUserResource(String accessToken) {
        GithubProfile githubProfile = (GithubProfile) super.getUserResource(accessToken);

        log.info("githubProfile = {}", githubProfile.toString());
        log.info("githubProfile = {}, {}", githubProfile.getGithubLink(), githubProfile.getId());

        if (githubProfile.getEmail() == null || githubProfile.getEmail().isEmpty()) {
            String fullLink = githubProfile.getGithubLink();
            String simplifiedLink = fullLink.replaceFirst("https://", "");
            githubProfile.setEmail(simplifiedLink);
        }

        return githubProfile;
    }
}