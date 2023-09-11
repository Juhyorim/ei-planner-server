package com.kihyaa.Eiplanner.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetViewDateTimeRequest {
    @NotNull
    private Long user_pk;

    @NotNull
    private Boolean display_date_time;

}
