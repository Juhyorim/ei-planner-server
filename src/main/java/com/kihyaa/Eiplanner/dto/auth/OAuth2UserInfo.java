package com.kihyaa.Eiplanner.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2UserInfo {
    private  String name;
    private String email;
}
