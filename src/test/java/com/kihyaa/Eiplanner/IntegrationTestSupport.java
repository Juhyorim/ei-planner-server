package com.kihyaa.Eiplanner;

import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import com.kihyaa.Eiplanner.service.HistoryService;
import com.kihyaa.Eiplanner.service.SettingService;
import com.kihyaa.Eiplanner.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
public abstract class IntegrationTestSupport {

    @Autowired
    protected SettingService settingservice;

    @Autowired
    protected SettingRepository settingRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected HistoryRepository historyRepository;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected TaskRepository taskRepository;

    protected Setting createSetting() {
        return Setting.builder()
                .isViewDateTime(true)
                .autoEmergencySwitch(3)
                .build();
    }

    protected Member createMember(String testEmail) {
        return Member.builder()
                .email(testEmail)
                .build();
    }
    protected Member createMember(String testEmail, Setting setting) {
        return Member.builder()
                .email(testEmail)
                .setting(setting)
                .build();
    }

    protected List<Task> createTaskList(Member member) {
        return List.of(
                createTask(member, "t1", LocalDateTime.of(2023, 9, 13, 1, 0)),
                createTask(member, "t2", LocalDateTime.of(2023, 9, 13, 3, 0)),
                createTask(member, "t3", LocalDateTime.of(2023, 9, 13, 2, 0))
        );
    }

    protected Task createTask(Member member, String title, LocalDateTime endTime){
        return new Task(member, title, true, endTime);

    }

    protected List<History> createHistoryList(Member member, List<Task> taskList) {
        List<History> historyList = new ArrayList<>();
        for (Task task : taskList) {
            History history = History.makeHistory(member, task);
            historyList.add(history);
        }
        return historyList;
    }


}
