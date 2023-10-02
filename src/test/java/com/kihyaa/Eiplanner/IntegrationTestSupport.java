//package com.kihyaa.Eiplanner;
//
//import com.kihyaa.Eiplanner.domain.*;
//import com.kihyaa.Eiplanner.repository.HistoryRepository;
//import com.kihyaa.Eiplanner.repository.MemberRepository;
//import com.kihyaa.Eiplanner.repository.SettingRepository;
//import com.kihyaa.Eiplanner.repository.TaskRepository;
//import com.kihyaa.Eiplanner.service.HistoryService;
//import com.kihyaa.Eiplanner.service.SettingService;
//import com.kihyaa.Eiplanner.service.TaskService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@SpringBootTest
//public abstract class IntegrationTestSupport {
//
//    @Autowired
//    protected SettingService settingservice;
//
//    @Autowired
//    protected SettingRepository settingRepository;
//
//    @Autowired
//    protected MemberRepository memberRepository;
//
//    @Autowired
//    protected HistoryService historyService;
//
//    @Autowired
//    protected HistoryRepository historyRepository;
//
//    @Autowired
//    protected TaskService taskService;
//
//    @Autowired
//    protected TaskRepository taskRepository;
//
//    protected Setting createSetting() {
//        return Setting.builder()
//                .isViewDateTime(true)
//                .autoEmergencySwitch(4)
//                .build();
//    }
//
//    protected Member createMember(String testEmail) {
//        return Member.builder()
//                .email(testEmail)
//                .build();
//    }
//    protected Member createMember(String testEmail, Setting setting) {
//        return Member.builder()
//                .email(testEmail)
//                .setting(setting)
//                .build();
//    }
//
//    protected List<Task>
//    createTaskList(Member member) {
//        return List.of(
//                createTaskForHistoryTest(member, "t1", LocalDateTime.of(2023, 9, 13, 1, 0)),
//                createTaskForHistoryTest(member, "t2", LocalDateTime.of(2023, 9, 13, 3, 0)),
//                createTaskForHistoryTest(member, "t3", LocalDateTime.of(2023, 9, 13, 2, 0))
//        );
//    }
//
//    protected List<Task> createScheduleTaskList(Member member) {
//        Task t1 = createScehduleTask(member, "t1", LocalDateTime.of(2023, 9, 13, 5, 0), false, EIType.IMPORTANT_NOT_URGENT, false);
//        Task t2 = createScehduleTask(member, "t2", LocalDateTime.of(2023, 9, 13, 3, 0), false, EIType.IMPORTANT_NOT_URGENT, false);
//        Task t3 = createScehduleTask(member, "t3", LocalDateTime.of(2023, 9, 13, 5, 0), false, EIType.IMPORTANT_NOT_URGENT, false);
//        Task t4 = createScehduleTask(member, "t4", LocalDateTime.of(2023, 9, 13, 5, 0), false, EIType.NOT_IMPORTANT_NOT_URGENT, false);
//        Task t5 = createScehduleTask(member, "t5", LocalDateTime.of(2023, 9, 13, 3, 0), false, EIType.NOT_IMPORTANT_NOT_URGENT, false);
//        Task t6 = createScehduleTask(member, "t6", LocalDateTime.of(2023, 9, 13, 1, 0), false, EIType.NOT_IMPORTANT_URGENT, false);
//
//        t1.setNextTask(t2);
//        t2.setPrevTask(t1);
//        t2.setNextTask(t3);
//        t3.setPrevTask(t2);
//
//        t4.setNextTask(t5);
//        t5.setPrevTask(t4);
//
//        return List.of(
//                t1, t2, t3, t4, t5, t6
//        );
//    }
//
//
//    protected Task createTaskForHistoryTest(Member member, String title, LocalDateTime endAt){
//        return new Task(member, title, true, endAt);
//
//    }
//
//    protected Task createScehduleTask(Member member, String title, LocalDateTime endAt, boolean isCompleted, EIType eiType, boolean isHistory){
//        return new Task(member, title,  endAt, isCompleted, eiType, isHistory);
//
//    }
//
//
//    protected List<History> createHistoryList(Member member, List<Task> taskList) {
//        List<History> historyList = new ArrayList<>();
//        for (Task task : taskList) {
//            History history = History.makeHistory(member, task);
//            historyList.add(history);
//        }
//        return historyList;
//    }
//
//
//}
