package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.IntegrationTestSupport;
import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class TaskRepositoryTest extends IntegrationTestSupport {

    private final String testEmail = "testUser@gmail.com";

    @DisplayName("히스토리에 존재하지 않고 완료되지 않은 상태에서 멤버별, EiType별로 연결리스트 head를 조회할 수 있다.")
    @Test
    void findByMemberAndEiTypeAndPrevIsNullAndIsHistoryIsFalseAndIsCompletedIsFalse() {

        // given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);
        settingRepository.save(setting);
        memberRepository.save(member);

        List<Task> taskList = createScheduleTaskList(member);
        taskRepository.saveAll(taskList);

        // when
        Task findTask = taskRepository.findByMemberAndEiTypeAndPrevIsNullAndIsHistoryIsFalseAndIsCompletedIsFalse(member, EIType.IMPORTANT_NOT_URGENT).get();

        //then
        assertThat(findTask.getTitle()).isEqualTo("t1");

    }

    @DisplayName("모든 멤버의 두 시간의 차이가 자동 긴급 설정 시간보다 작고 EIType이 *_NOT_URGENCY인 완료되지 않은 Task를 조회할 수 있다.")
    @Test
    void findNotUrgencyTask() {
        // given
        Setting setting = createSetting();
        Member member = createMember(testEmail, setting);
        settingRepository.save(setting);
        memberRepository.save(member);

        List<Task> taskList = createScheduleTaskList(member);
        taskRepository.saveAll(taskList);

        // test
        List<Task> findTasks = taskRepository.findNotUrgencyTask(LocalDateTime.of(2023, 9, 13, 0, 0));

        // then
        assertThat(findTasks).hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder("t2", "t5");

    }
}