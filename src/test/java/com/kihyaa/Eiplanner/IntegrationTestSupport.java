package com.kihyaa.Eiplanner;

import com.kihyaa.Eiplanner.repository.HistoryRepository;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import com.kihyaa.Eiplanner.service.HistoryService;
import com.kihyaa.Eiplanner.service.SettingService;
import com.kihyaa.Eiplanner.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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



}
