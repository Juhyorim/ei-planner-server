//package com.kihyaa.Eiplanner.service;
//
//import com.kihyaa.Eiplanner.IntegrationTestSupport;
//
//import com.kihyaa.Eiplanner.domain.EIType;
//import com.kihyaa.Eiplanner.domain.Member;
//import com.kihyaa.Eiplanner.domain.Setting;
//import com.kihyaa.Eiplanner.domain.Task;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Transactional
//class SchedulerTest extends IntegrationTestSupport {
//
//    private final String testEmail = "testUser@gmail.com";
//
//    @DisplayName("*_NOT_URGENT Type을 *_URGENT Type으로 변경할 수 있다.")
//    @Test
//    void editToUrgentEiType(){
//        //given
//        Setting setting = createSetting();
//        Member member = createMember(testEmail, setting);
//        settingRepository.save(setting);
//        memberRepository.save(member);
//
//        List<Task> taskList = createScheduleTaskList(member);
//        taskRepository.saveAll(taskList);
//
//        //when
//        for(Task task : taskList){
//            taskService.editToUrgentEiType(task);
//        }
//
//        List<Task> level1Task = taskRepository.findByMemberAndEiType(member, EIType.IMPORTANT_URGENT); // 3
//        List<Task> level3Task = taskRepository.findByMemberAndEiType(member, EIType.NOT_IMPORTANT_URGENT); // 3
//
//        //then
//        assertThat(level1Task).hasSize(3)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t1", "t2", "t3");
//        assertThat(level3Task).hasSize(3)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t4", "t5", "t6");
//    }
//
//    @DisplayName("*_NOT_URGENCY Type의 Task를 *_URGENCY Type의 연결리스트 tail로 이동시킬 수 있다. 1")
//    @Test
//    void fetchAndMoveTask(){
//        //given
//        Setting setting = createSetting();
//        Member member = createMember(testEmail, setting);
//        settingRepository.save(setting);
//        memberRepository.save(member);
//
//        List<Task> taskList = createScheduleTaskList(member);
//        taskRepository.saveAll(taskList);
//
//        Task targetTask = taskList.get(4);
//
//        //when
//        taskService.fetchAndMoveTask(targetTask);
//        List<Task> level3Task = taskRepository.findByMemberAndEiType(member, EIType.NOT_IMPORTANT_URGENT); // 2
//        List<Task> level4Task = taskRepository.findByMemberAndEiType(member, EIType.NOT_IMPORTANT_NOT_URGENT); // 1
//
//        //then
//        assertThat(level3Task).hasSize(2)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t6", "t5");
//        assertThat(level4Task).hasSize(1)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t4");
//    }
//
//    @DisplayName("*_NOT_URGENCY Type의 Task를 *_URGENCY Type의 연결리스트 tail로 이동시킬 수 있다. 2")
//    @Test
//    void fetchAndMoveTask2(){
//        //given
//        Setting setting = createSetting();
//        Member member = createMember(testEmail, setting);
//        settingRepository.save(setting);
//        memberRepository.save(member);
//
//        List<Task> taskList = createScheduleTaskList(member);
//        taskRepository.saveAll(taskList);
//
//        Task targetTask = taskList.get(1);
//
//        //when
//        taskService.fetchAndMoveTask(targetTask);
//        List<Task> level1Task = taskRepository.findByMemberAndEiType(member, EIType.IMPORTANT_URGENT);
//        List<Task> level2Task = taskRepository.findByMemberAndEiType(member, EIType.IMPORTANT_NOT_URGENT);
//
//        //then
//        assertThat(level1Task).hasSize(1)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t2");
//        assertThat(level2Task).hasSize(2)
//                .extracting("title")
//                .containsExactlyInAnyOrder("t1", "t3");
//    }
//
//    @DisplayName("NOT_URGENCY 타입의 Task들의 완료 시간이 / Setting의 자동 긴급 전환 시간보다 작을 경우 / 긴급으로 타입 수정 + 연결리스트 tail 자리에 붙일 수 있다.")
//    @Test
//    void scheduleTaskTypeRotation() {
//        //given
//        Setting setting = createSetting();
//        Member member = createMember(testEmail, setting);
//        settingRepository.save(setting);
//        memberRepository.save(member);
//
//        List<Task> taskList = createScheduleTaskList(member);
//        taskRepository.saveAll(taskList);
//
//        List<Task> tasks = taskRepository.findByMember(member);
//        assertThat(tasks).hasSize(6);
//
//        //when
//        taskService.scheduleTaskTypeRotation(LocalDateTime.of(2023, 9, 13, 1, 0));
//
//        List<Task> level1Task = taskRepository.findByMemberAndEiType(member, EIType.IMPORTANT_URGENT); // 1
//        List<Task> level2Task = taskRepository.findByMemberAndEiType(member, EIType.IMPORTANT_NOT_URGENT); // 2
//        List<Task> level3Task = taskRepository.findByMemberAndEiType(member, EIType.NOT_IMPORTANT_URGENT); // 2
//        List<Task> level4Task = taskRepository.findByMemberAndEiType(member, EIType.NOT_IMPORTANT_NOT_URGENT); // 1
//
//        //then
//        assertThat(level1Task).hasSize(1);
//        assertThat(level2Task).hasSize(2);
//        assertThat(level3Task).hasSize(2);
//        assertThat(level4Task).hasSize(1);
//
//    }
//
//}