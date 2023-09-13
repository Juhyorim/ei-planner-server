package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.IntegrationTestSupport;
import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.response.GetHistoryResponse;
import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class HistoryServiceTest extends IntegrationTestSupport {

    private final String testEmail = "testUser@gmail.com";

    @DisplayName("히스토리를 일정이 완료된 시간 순으로 조회할 수 있다.")
    @Test
    void getHistory() {
        //given
        Member member = createMember(testEmail);
        memberRepository.save(member);

        List<Task> taskList = createTaskList(member);
        taskRepository.saveAll(taskList);

        List<History> historyList = createHistoryList(member, taskList);
        historyRepository.saveAll(historyList);

        Pageable pageable = PageRequest.of(0,2);

        //when
        GetHistoryResponse response = historyService.getHistory(member.getId(), pageable);


        //then
        assertThat(response.getCount()).isEqualTo(2);
        assertThat(response.getTasks()).hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "t2","t3"
        );

    }

    @DisplayName("히스토리를 단건 삭제를 할 수 있다.")
    @Test
    void deleteOneHistory() {
        // given
        Member member = createMember(testEmail);
        memberRepository.save(member);

        List<Task> taskList = createTaskList(member);
        taskRepository.saveAll(taskList);

        List<History> historyList = createHistoryList(member, taskList);
        historyRepository.saveAll(historyList);

        Long targetId = historyList.get(0).getTask().getId();

        //when
        Optional<History> findHistoryBeforeDelete = historyRepository.findByTaskId(targetId);
        historyService.deleteOneHistory(targetId, member.getId());
        Optional<History> findHistoryAfterDelete = historyRepository.findByTaskId(targetId);

        //then
        assertThat(findHistoryBeforeDelete).isNotEmpty();
        assertThat(findHistoryAfterDelete).isEmpty();

    }

    @DisplayName("요청한 히스토리가 존재하지 않으면 단건 삭제를 할 수 없다.")
    @Test
    void deleteOneHistoryNotExist() {
        // given
        Member member = createMember(testEmail);
        memberRepository.save(member);

        List<Task> taskList = createTaskList(member);
        taskRepository.saveAll(taskList);

        List<History> historyList = createHistoryList(member, taskList);
        historyRepository.saveAll(historyList);

        Long targetId = -1L;

        //when
        assertThatThrownBy(() -> historyService.deleteOneHistory(targetId, member.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("요청한 히스토리가 존재하지 않습니다.");
    }

    @DisplayName("모든 히스토리를 삭제를 할 수 있다.")
    @Test
    void deleteAllHistory() {
        // given
        Member member = createMember(testEmail);
        memberRepository.save(member);

        List<Task> taskList = createTaskList(member);
        taskRepository.saveAll(taskList);

        List<History> historyList = createHistoryList(member, taskList);
        historyRepository.saveAll(historyList);

        //when
        historyService.deleteAllHistory(member.getId());
        List<History> findHistoryList = historyRepository.findByMember(member);

        assertThat(findHistoryList).isEmpty();
    }


}