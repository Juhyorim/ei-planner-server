package com.kihyaa.Eiplanner.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetAutoUrgentRequest {
    @NotNull
    private Long user_pk;

    @PositiveOrZero
    private int auto_urgent_day;

}

