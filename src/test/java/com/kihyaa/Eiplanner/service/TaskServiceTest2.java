package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskServiceTest2 {

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


  private Long taskId1;
  private Long taskId2;
  private Long taskId3;
  private Long taskId4;

  @BeforeAll
  void setUp() {
    Setting setting = new Setting();

    member = Member.builder()
      .email("asdf")
      .nickname("닉네임")
      .password("1234")
      .setting(setting)
      .build();

    member = memberRepository.save(member);
  }

  @DisplayName("일정 등록")
  @Transactional
  @Order(1)
  @Test
  void makeTask() {
    make4Task();

    Task task = taskRepository.findById(taskId1).orElseThrow(() -> new NoSuchElementException());

    assertEquals(title, task.getTitle());
    assertEquals(member.getId(), task.getMember().getId());
    assertEquals(description, task.getDescription());
    assertEquals(null, task.getEndAt());
    assertEquals(false, task.getIsTimeInclude());
    assertEquals(EIType.PENDING, task.getEiType());
    assertEquals(null, task.getCompletedAt());
    assertEquals(false, task.getIsCompleted());

    Task task2 = taskRepository.findById(taskId2).orElseThrow(() -> new NoSuchElementException());
    //연결고리 확인
    assertEquals(taskId2, task.getNext().getId());
    assertEquals(null, task.getPrev());
    assertEquals(taskId1, task2.getPrev().getId());
    assertEquals(taskId3, task2.getNext().getId());
  }

  private void make4Task() {
    MakeTaskRequest makeTaskRequest = new MakeTaskRequest(title, description, null, false);

    taskId1 = taskService.makeTask(makeTaskRequest, member);
    taskId2 = taskService.makeTask(makeTaskRequest, member);
    taskId3 = taskService.makeTask(makeTaskRequest, member);
    taskId4 = taskService.makeTask(makeTaskRequest, member);
  }

  @DisplayName("일정 같은 타입으로 이동: 가장 상위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskFirstPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId4, taskId1, taskId2, taskId3});

    taskService.move(taskId4, new TaskMoveRequest(EIType.PENDING, lst), member);
    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();

    assertEquals(taskId4, tasks.get(0).getId());
    assertEquals(taskId1, tasks.get(1).getId());
    assertEquals(taskId2, tasks.get(2).getId());
    assertEquals(taskId3, tasks.get(3).getId());
  }

  private List<Long> makeDestinationOrderList(Long[] taskIdList) {
    List<Long> lst = new ArrayList<>();
    for (Long id: taskIdList) {
      lst.add(id);
    }
    return lst;
  }

  //일정 같은 타입으로 이동: 가장 하위
  @DisplayName("일정 같은 타입으로 이동: 가장 하위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskLastPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId2, taskId3, taskId4, taskId1});

    taskService.move(taskId1, new TaskMoveRequest(EIType.PENDING, lst), member);
    TaskListResponse pending = taskService.getAllTask(member).getPending();

    assertEquals(taskId2, pending.getTasks().get(0).getId());
    assertEquals(taskId3, pending.getTasks().get(1).getId());
    assertEquals(taskId4, pending.getTasks().get(2).getId());
    assertEquals(taskId1, pending.getTasks().get(3).getId());
  }

  //일정 같은 타입으로 이동: 중간으로
  @DisplayName("일정 같은 타입으로 이동: 가장 하위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskCenterPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId2, taskId1, taskId3, taskId4});

    taskService.move(taskId1, new TaskMoveRequest(EIType.PENDING, lst), member);
    TaskListResponse pending = taskService.getAllTask(member).getPending();

    assertEquals(taskId2, pending.getTasks().get(0).getId());
    assertEquals(taskId1, pending.getTasks().get(1).getId());
    assertEquals(taskId3, pending.getTasks().get(2).getId());
    assertEquals(taskId4, pending.getTasks().get(3).getId());
  }

  @DisplayName("일정 다른 타입으로 이동: 가장 상위")
  @Transactional
  @Order(3)
  @Test
  void moveTaskFirstPositionDiffType() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId3, taskId1, taskId2});
    taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    TaskListResponse pending = taskService.getAllTask(member).getPending();
    assertEquals(taskId4, pending.getTasks().get(0).getId());

    TaskListResponse imUr = taskService.getAllTask(member).getImportant_urgent();
    assertEquals(taskId3, imUr.getTasks().get(0).getId());
    assertEquals(taskId1, imUr.getTasks().get(1).getId());
    assertEquals(taskId2, imUr.getTasks().get(2).getId());
  }

  @DisplayName("일정 다른 타입으로 이동: 가장 하위")
  @Transactional
  @Order(3)
  @Test
  void moveTaskLastPositionDiffType() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2, taskId3});
    taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    TaskListResponse pending = taskService.getAllTask(member).getPending();
    assertEquals(taskId4, pending.getTasks().get(0).getId());

    TaskListResponse imUr = taskService.getAllTask(member).getImportant_urgent();
    assertEquals(taskId1, imUr.getTasks().get(0).getId());
    assertEquals(taskId2, imUr.getTasks().get(1).getId());
    assertEquals(taskId3, imUr.getTasks().get(2).getId());
  }

  //일정 다른 타입으로 이동: 중간으로
  @DisplayName("일정 다른 타입으로 이동: 가장 하위")
  @Transactional
  @Order(3)
  @Test
  void moveTaskCenterPositionDiffType() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId3, taskId2});
    taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    TaskListResponse pending = taskService.getAllTask(member).getPending();
    assertEquals(taskId4, pending.getTasks().get(0).getId());

    TaskListResponse imUr = taskService.getAllTask(member).getImportant_urgent();
    assertEquals(taskId1, imUr.getTasks().get(0).getId());
    assertEquals(taskId3, imUr.getTasks().get(1).getId());
    assertEquals(taskId2, imUr.getTasks().get(2).getId());
  }


}