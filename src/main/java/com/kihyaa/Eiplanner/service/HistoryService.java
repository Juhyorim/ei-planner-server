package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.dto.response.GetHistoryResponse;
import com.kihyaa.Eiplanner.dto.HistoryTaskDto;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;

    public GetHistoryResponse getHistory(Long memberId, Pageable pageable) {
        validateMemberExists(memberId);

        Page<History> historyPage = historyRepository.findCompletedTasksByMemberId(memberId, pageable);

        List<HistoryTaskDto> historyTasks = historyPage.getContent().stream()
                .map(history -> HistoryTaskDto.of(history.getTask())).toList();

        return new GetHistoryResponse(historyTasks.size(), historyTasks);
    }

    private void validateMemberExists(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("해당 멤버가 존재하지 않습니다."));
    }

}