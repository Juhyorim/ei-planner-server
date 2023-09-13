package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Setting;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
  private HistoryService historyService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private EntityManager em;

  @Autowired
  private TaskRepository taskRepository;

  private Member member;

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

    Task task1 = taskRepository.findById(taskId1).orElseThrow(() -> new NoSuchElementException());

    //title빼고 다 null일 때
    assertEquals(title, task1.getTitle());
    assertEquals(member.getId(), task1.getMember().getId());
    assertEquals(null, task1.getDescription());
    assertEquals(null, task1.getEndAt());
    assertEquals(false, task1.getIsTimeInclude());
    assertEquals(EIType.PENDING, task1.getEiType());
    assertEquals(null, task1.getCompletedAt());
    assertEquals(false, task1.getIsCompleted());

    //description이 null이 아닐 때
    Task task2 = taskRepository.findById(taskId2).orElseThrow(() -> new NoSuchElementException());
    assertEquals(description, task2.getDescription());
    assertEquals(false, task2.getIsTimeInclude());

    //isTimeInclude가 false일 때
    Task task3 = taskRepository.findById(taskId3).orElseThrow(() -> new NoSuchElementException());
    assertEquals(0, task3.getEndAt().getHour());
    assertEquals(0, task3.getEndAt().getMinute());
    assertEquals(0, task3.getEndAt().getSecond());
    assertEquals(0, task3.getEndAt().getNano());
    assertEquals(false, task3.getIsTimeInclude());

    Task task4 = taskRepository.findById(taskId4).orElseThrow(() -> new NoSuchElementException());
    assertEquals(true, task4.getIsTimeInclude());

    //연결고리 확인
    assertEquals(taskId2, task1.getNext().getId());
    assertEquals(null, task1.getPrev());
    assertEquals(taskId1, task2.getPrev().getId());
    assertEquals(taskId3, task2.getNext().getId());
  }

  private void make4Task() {
    MakeTaskRequest makeTaskRequest1 = new MakeTaskRequest(title, null, null, false);
    MakeTaskRequest makeTaskRequest2 = new MakeTaskRequest(title, description, null, true);
    MakeTaskRequest makeTaskRequest3 = new MakeTaskRequest(title, description, LocalDateTime.now(), false);
    MakeTaskRequest makeTaskRequest4 = new MakeTaskRequest(title, description, LocalDateTime.now(), true);

    taskId1 = taskService.makeTask(makeTaskRequest1, member);
    taskId2 = taskService.makeTask(makeTaskRequest2, member);
    taskId3 = taskService.makeTask(makeTaskRequest3, member);
    taskId4 = taskService.makeTask(makeTaskRequest4, member);
  }

  @DisplayName("일정 사분면 이동 테스트")
  @Transactional
  @Order(2)
  @Test
  void moveTask() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});

    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);
    TaskResponse info = taskService.getInfo(taskId1, member);
    assertEquals(EIType.IMPORTANT_URGENT, info.getEiType());

    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_NOT_URGENT, lst), member);
    info = taskService.getInfo(taskId1, member);
    assertEquals(EIType.IMPORTANT_NOT_URGENT, info.getEiType());

    taskService.move(taskId1, new TaskMoveRequest(EIType.NOT_IMPORTANT_URGENT, lst), member);
    info = taskService.getInfo(taskId1, member);
    assertEquals(EIType.NOT_IMPORTANT_URGENT, info.getEiType());

    taskService.move(taskId1, new TaskMoveRequest(EIType.NOT_IMPORTANT_NOT_URGENT, lst), member);
    info = taskService.getInfo(taskId1, member);
    assertEquals(EIType.NOT_IMPORTANT_NOT_URGENT, info.getEiType());

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2, taskId3, taskId4});
    taskService.move(taskId1, new TaskMoveRequest(EIType.PENDING, lst), member);
    info = taskService.getInfo(taskId1, member);
    assertEquals(EIType.PENDING, info.getEiType());
  }

  @DisplayName("일정 같은 타입으로 이동: 가장 상위")
  @Transactional
  @Order(2)
  @Test
  void moveTaskFirstPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId4, taskId1, taskId2, taskId3});

    taskService.move(taskId4, new TaskMoveRequest(EIType.PENDING, lst), member);
    em.flush(); em.clear();

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

  @DisplayName("일정 같은 타입으로 이동: 가장 하위")
  @Transactional
  @Order(3)
  @Test
  void moveTaskLastPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId2, taskId3, taskId4, taskId1});

    taskService.move(taskId1, new TaskMoveRequest(EIType.PENDING, lst), member);
    em.flush(); em.clear();

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();

    assertEquals(taskId2, tasks.get(0).getId());
    assertEquals(taskId3, tasks.get(1).getId());
    assertEquals(taskId4, tasks.get(2).getId());
    assertEquals(taskId1, tasks.get(3).getId());

  }

  @DisplayName("일정 같은 타입으로 이동: 중간으로")
  @Transactional
  @Order(4)
  @Test
  void moveTaskCenterPosition() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId2, taskId1, taskId3, taskId4});


    taskService.move(taskId1, new TaskMoveRequest(EIType.PENDING, lst), member);
    em.flush(); em.clear();

    TaskListResponse pending = taskService.getAllTask(member).getPending();

    assertEquals(taskId2, pending.getTasks().get(0).getId());
    assertEquals(taskId1, pending.getTasks().get(1).getId());
    assertEquals(taskId3, pending.getTasks().get(2).getId());
    assertEquals(taskId4, pending.getTasks().get(3).getId());
  }

  @DisplayName("일정 다른 타입으로 이동: 가장 상위")
  @Transactional
  @Order(5)
  @Test
  void moveTaskFirstPositionDiffType() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId3, taskId1, taskId2});
    taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    em.flush(); em.clear();

    TaskListResponse pending = taskService.getAllTask(member).getPending();
    assertEquals(taskId4, pending.getTasks().get(0).getId());

    TaskListResponse imUr = taskService.getAllTask(member).getImportant_urgent();
    assertEquals(taskId3, imUr.getTasks().get(0).getId());
    assertEquals(taskId1, imUr.getTasks().get(1).getId());
    assertEquals(taskId2, imUr.getTasks().get(2).getId());
  }

  @DisplayName("일정 다른 타입으로 이동: 가장 하위")
  @Transactional
  @Order(6)
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

  @DisplayName("일정 다른 타입으로 이동: 중간으로")
  @Transactional
  @Order(7)
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

  @DisplayName("일정 이동 시 목적지 배열이 정확하지 않은 경우") //솔직히 prev, 나, next만 줘도 동작할듯
  @Transactional
  @Order(7)
  @Test
  void moveTaskWithBadRequest() {
    make4Task();

    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    //IM_UR: [1,2]    PENDING: [3,4]

    //자기 자신을 포함하지 않은 경우
    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});

    List<Long> finalLst = lst;
    assertThrows(InputMismatchException.class, () -> {
      taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, finalLst), member);
    });

    //앞에 이상한 애 붙어있는 경우
    lst = makeDestinationOrderList(new Long[]{taskId4, taskId2});

    List<Long> finalLst2 = lst;
    assertThrows(InputMismatchException.class, () -> {
      taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, finalLst2), member);
    });

    //뒤에 이상한 애 붙어있는 경우
    lst = makeDestinationOrderList(new Long[]{taskId1, taskId4});

    List<Long> finalLst3 = lst;
    assertThrows(InputMismatchException.class, () -> {
      taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, finalLst3), member);
    });

    //배열 길이가 이상할 경우 - 오류 x
    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2, taskId3});
    taskService.move(taskId3, new TaskMoveRequest(EIType.IMPORTANT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId2, taskId4, taskId3});
    List<Long> finalLst4 = lst;
    assertDoesNotThrow(() -> {
      taskService.move(taskId4, new TaskMoveRequest(EIType.IMPORTANT_URGENT, finalLst4), member);
    });

  }

  @DisplayName("일정 조회 테스트")
  @Transactional
  @Order(8)
  @Test
  void getAllTask() {
    make4Task();
    //@TODO 시간 있을 때 없을 때 조회
    List<Long> lst = makeDestinationOrderList(new Long[]{taskId1});
    taskService.move(taskId1, new TaskMoveRequest(EIType.NOT_IMPORTANT_NOT_URGENT, lst), member);

    lst = makeDestinationOrderList(new Long[]{taskId1, taskId2});
    taskService.move(taskId2, new TaskMoveRequest(EIType.NOT_IMPORTANT_NOT_URGENT, lst), member);

    DashBoardResponse allTask = taskService.getAllTask(member);

    //PENDING에 2개, NOT_IMPORTANT_NOT_URGENT에 2개 확인
    TaskListResponse pending = allTask.getPending();
    TaskListResponse notImportantNotUrgent = allTask.getNot_important_not_urgent();

    assertEquals(taskId3, pending.getTasks().get(0).getId());
    assertEquals(taskId4, pending.getTasks().get(1).getId());

    assertEquals(taskId1, notImportantNotUrgent.getTasks().get(0).getId());
    assertEquals(taskId2, notImportantNotUrgent.getTasks().get(1).getId());
  }

  //일정 정리하기 테스트
  @DisplayName("일정 완료 테스트")
  @Transactional
  @Order(9)
  @Test
  void completeTask() {
    make4Task();

    taskService.editCheck(taskId1, new TaskCheckDto(true));
    taskService.editCheck(taskId2, new TaskCheckDto(true));

    //체크 취소 테스트
    taskService.editCheck(taskId3, new TaskCheckDto(true));
    taskService.editCheck(taskId3, new TaskCheckDto(false));

    TaskResponse info1 = taskService.getInfo(taskId1, member);
//    TaskResponse info2 = taskService.getInfo(taskId2, member);
    TaskResponse info3 = taskService.getInfo(taskId3, member);

    //완료 체크 확인, 완료일시 notnull확인, isHistory false확인
    Task task1 = taskRepository.findById(taskId1).get();
    assertEquals(true, info1.getIsCompleted());
    assertNotNull(task1.getCompletedAt());
    assertEquals(false, task1.getIsHistory());

    Task task3 = taskRepository.findById(taskId3).get();
    assertEquals(false, info3.getIsCompleted());
    assertNull(task3.getCompletedAt());
    assertEquals(false, task3.getIsHistory());
  }

  //일정 정리하기 테스트
  @DisplayName("일정 정리하기 테스트")
  @Transactional
  @Order(10)
  @Test
  void clearCompleteTask() throws InterruptedException {
    make4Task();

    taskService.editCheck(taskId2, new TaskCheckDto(true));
    Thread.sleep(1000);
    taskService.editCheck(taskId3, new TaskCheckDto(true));

    taskService.cleanCompleteTasks(member);

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();
    for (TaskResponse response: tasks) {
      assertNotEquals(taskId2, response.getId());
      assertNotEquals(taskId3, response.getId());
    }

    //히스토리 잘 들어갔는지 확인
    List<HistoryTaskDto> historys = historyService.getHistory(member.getId(), PageRequest.of(0, 2)).getTasks();

    //체크 완료된 순 DESC 정렬!
    assertEquals(taskId2, historys.get(1).getId());
    assertEquals(taskId3, historys.get(0).getId());
  }

  //일정 삭제 테스트-첫 번째 위치
  @DisplayName("일정 삭제 테스트: 첫 번째 위치")
  @Transactional
  @Order(11)
  @Test
  void deleteFirstPos() {
    make4Task();

    taskService.delete(taskId1, member);

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(3, tasks.size());
    for (TaskResponse response: tasks)
      assertNotEquals(taskId1, response.getId());

    taskService.delete(taskId2, member);
    taskService.delete(taskId3, member);
    taskService.delete(taskId4, member);

    tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(0, tasks.size());
  }

  @DisplayName("일정 삭제 테스트: 마지막 위치")
  @Transactional
  @Order(11)
  @Test
  void deleteLastPos() {
    make4Task();

    taskService.delete(taskId4, member);

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(3, tasks.size());
    for (TaskResponse response: tasks)
      assertNotEquals(taskId4, response.getId());

    taskService.delete(taskId3, member);
    taskService.delete(taskId2, member);
    taskService.delete(taskId1, member);

    tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(0, tasks.size());
  }

  //일정 삭제 테스트-중간 위치
  @DisplayName("일정 삭제 테스트: 중간 위치")
  @Transactional
  @Order(11)
  @Test
  void deleteCenterPos() {
    make4Task();

    taskService.delete(taskId2, member);

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(3, tasks.size());
    for (TaskResponse response: tasks)
      assertNotEquals(taskId2, response.getId());

    taskService.delete(taskId3, member);
    tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(2, tasks.size());
    for (TaskResponse response: tasks)
      assertNotEquals(taskId3, response.getId());

    taskService.delete(taskId4, member);
    taskService.delete(taskId1, member);

    tasks = taskService.getAllTask(member).getPending().getTasks();
    assertEquals(0, tasks.size());
  }

  //일정 내용 수정 테스트
  @DisplayName("일정 내용 수정 테스트")
  @Transactional
  @Order(12)
  @Test
  void editTaskDetail() throws InterruptedException {
    make4Task();

    Thread.sleep(1000);

    LocalDateTime endTime = LocalDateTime.now();

    taskService.edit(taskId1, new TaskEditRequest("바꾼제목", null, null, false));
    taskService.edit(taskId2, new TaskEditRequest("바꾼제목", "바꾼설명", null, true));
    taskService.edit(taskId3, new TaskEditRequest("바꾼제목", "바꾼설명", endTime, false));
    taskService.edit(taskId4, new TaskEditRequest("바꾼제목", "바꾼설명", endTime, true));

    List<TaskResponse> tasks = taskService.getAllTask(member).getPending().getTasks();

    for (TaskResponse response: tasks) {

      if (response.getId() == taskId1) {
        assertEquals("바꾼제목", response.getTitle());
        assertNull(response.getDescription());
        assertNull(response.getEndAt());
        assertEquals(false, response.getIsTimeInclude());
      } else if (response.getId() == taskId2) {
        assertEquals("바꾼제목", response.getTitle());
        assertEquals("바꾼설명", response.getDescription());
        assertNull(response.getEndAt());
        assertEquals(false, response.getIsTimeInclude());
      } else if (response.getId() == taskId2) {
        assertEquals("바꾼제목", response.getTitle());
        assertEquals("바꾼설명", response.getDescription());
        assertEquals(endTime, response.getEndAt().withHour(0).withMinute(0).withSecond(0).withNano(0));
        assertEquals(false, response.getIsTimeInclude());
      } else if (response.getId() == taskId2) {
        assertEquals("바꾼제목", response.getTitle());
        assertEquals("바꾼설명", response.getDescription());
        assertEquals(endTime, response.getEndAt());
        assertEquals(true, response.getIsTimeInclude());
      }
    }
  }


}