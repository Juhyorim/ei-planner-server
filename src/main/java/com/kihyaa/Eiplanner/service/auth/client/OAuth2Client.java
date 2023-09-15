package com.kihyaa.Eiplanner.service.auth.client;

import com.kihyaa.Eiplanner.service.auth.profile.CommonProfile;

public interface OAuth2Client {
    CommonProfile oauthLogin(String code);
}
