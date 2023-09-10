package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.dto.response.GetHistoryResponse;
import com.kihyaa.Eiplanner.dto.HistoryTaskDto;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;


    public GetHistoryResponse getHistory(Long memberId, Pageable pageable) {
        Page<History> historyPage = historyRepository.findCompletedTasksByMemberId(memberId, pageable);

        List<HistoryTaskDto> historyTasks = historyPage.getContent().stream()
                .map(history -> HistoryTaskDto.of(history.getTask())).toList();

        return new GetHistoryResponse(historyTasks.size(), historyTasks);
    }
}