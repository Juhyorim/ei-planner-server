package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.MakeTaskRequest;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskServiceTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private TaskService taskService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private SettingRepository settingRepository;

  private Member member;

  //member.getId(), "제목", "설명", null, null);
  private String title = "제목1";
  private String description = "설명2";

  @BeforeAll
  void setUp() {
    Setting setting = new Setting();

    settingRepository.save(setting);

    member = Member.builder()
      .email("asdf")
      .nickname("닉네임")
      .password("1234")
      .setting(setting)
      .build();

    memberRepository.save(member);
  }

//  @DisplayName("일정 등록")
//  @Transactional
//  @Order(1)
//  @Test
//  void makeTask() {
//    MakeTaskRequest makeTaskRequest = new MakeTaskRequest(title, description, null, null);
//    MakeTaskRequest makeTaskRequest2 = new MakeTaskRequest(title, description, null, null);
//    MakeTaskRequest makeTaskRequest3 = new MakeTaskRequest(title, description, null, null);
//    Long taskId = taskService.makeTask(makeTaskRequest, member);
//    Long taskId2 = taskService.makeTask(makeTaskRequest2, member);
//    Long taskId3 = taskService.makeTask(makeTaskRequest3, member);
//
//    Task task = taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException());
//
//    assertEquals(title, task.getTitle());
//    assertEquals(member.getId(), task.getMember().getId());
//    assertEquals(description, task.getDescription());
//    assertEquals(null, task.getEndDate());
//    assertEquals(null, task.getEndTime());
//    assertEquals(EIType.PENDING, task.getEiType());
//    assertEquals(null, task.getCompletedAt());
//    assertEquals(false, task.getIsCompleted());
//
//    Task task2 = taskRepository.findById(taskId2).orElseThrow(() -> new NoSuchElementException());
//    //연결고리 확인
//    assertEquals(taskId2, task.getNext().getId());
//    assertEquals(null, task.getPrev());
//    assertEquals(taskId, task2.getPrev().getId());
//    assertEquals(taskId3, task2.getNext().getId());
//  }

  @DisplayName("일정 같은 타입으로 이동: 가장 상위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskFirstPosition() {

  }

  //일정 같은 타입으로 이동: 가장 하위
  @DisplayName("일정 같은 타입으로 이동: 가장 하위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskLastPosition() {

  }

  //일정 같은 타입으로 이동: 중간으로

  //일정 다른 타입으로 이동: 가장 상위

  //일정 다른 타입으로 이동: 가장 하위

  //일정 다른 타입으로 이동: 중간으로



}
