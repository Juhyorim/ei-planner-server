package com.kihyaa.Eiplanner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSettingResponse {
    private int auto_urgent;
    private  Boolean datetime_display;

    public static GetSettingResponse of(int auto_urgent, Boolean datetime_display) {
        return new GetSettingResponse(auto_urgent, datetime_display);
    }
}
