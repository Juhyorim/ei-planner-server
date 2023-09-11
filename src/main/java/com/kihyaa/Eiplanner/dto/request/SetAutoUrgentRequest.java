package com.kihyaa.Eiplanner.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetAutoUrgentRequest {
    @PositiveOrZero
    private int auto_urgent_day;

}

