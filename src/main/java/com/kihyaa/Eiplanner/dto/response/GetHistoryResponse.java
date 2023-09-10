package com.kihyaa.Eiplanner.dto.response;

import com.kihyaa.Eiplanner.dto.HistoryTaskDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetHistoryResponse {
    private int count;
    private List<HistoryTaskDto> tasks;
}