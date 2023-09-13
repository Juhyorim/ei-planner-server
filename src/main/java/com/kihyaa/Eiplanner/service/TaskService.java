package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.exception.exceptions.ForbiddenException;
import com.kihyaa.Eiplanner.exception.exceptions.InternalServerErrorException;
import com.kihyaa.Eiplanner.exception.exceptions.NotFoundException;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final HistoryRepository historyRepository;
  private final EntityManager em;

  public Long makeTask(MakeTaskRequest request, Member member) {
    Task prev = getFirstTaskPending(member);

    LocalDateTime dateTime = validAndEditDateTime(request.getEndAt(), request.getIsTimeInclude());

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description((request.getDescription() != null)? request.getDescription() : null)
      .endAt((dateTime != null) ? dateTime: null)
      .isTimeInclude((dateTime != null)? request.getIsTimeInclude(): false) //dateTime이 안들어오면 timeInclude는 false여야함
      .prev(prev)
      .build();

    //이전꺼에 연결
    if (prev != null)
      prev.setNextTask(task);

    taskRepository.save(task);

    return task.getId();
  }

  private Task getFirstTaskPending(Member member) {
    //EIType이 PENDING이고, Next가 null인 (마지막) 거 가져옴
    List<Task> taskList = taskRepository.findByMemberAndEiTypeAndNextIsNullAndIsHistoryIsFalse(member, EIType.PENDING);

    if (taskList.size() == 0)
      return null;
    else if (taskList.size() == 1)  //PENDING 상태인 일정이 하나면
      return taskList.get(0);
    else //1개 이상
      throw new InternalServerErrorException("일정 배열의 값이 이상합니다. 데이터베이스 이상.. 관리자에게 문의하세요");
  }

  public LocalDateTime validAndEditDateTime(LocalDateTime dateTime, boolean isTimeInclude) {
    //timeInclude가 false인데 dateTime이 null이 아니면 -> 시간은 쓸모없는거니까 시간부분 초기화
    if (dateTime != null && !isTimeInclude)
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    return dateTime;
  }

  @Transactional(readOnly = true)
  public DashBoardResponse getAllTask(Member member) {

    //Member의 history가 아닌 모든 task를 가져옴 - 정렬은 EiType으로
    List<Task> taskList = taskRepository.findByMemberAndIsHistoryIsFalseOrderByEiType(member);

    boolean isViewDateTime = member.getSetting().getIsViewDateTime();

    List<Task> listPENDING = new ArrayList<>();
    List<Task> listIMPORTANT_URGENT = new ArrayList<>();
    List<Task> listIMPORTANT_NOT_URGENT = new ArrayList<>();
    List<Task> listNOT_IMPORTANT_URGENT = new ArrayList<>();
    List<Task> listNOT_IMPORTANT_NOT_URGENT = new ArrayList<>();

    int i =0;
    while(i < taskList.size()) {
      while (i < taskList.size() && taskList.get(i).getEiType() == EIType.PENDING) {
        listPENDING.add(taskList.get(i)); i++;
      }
      while (i < taskList.size() && taskList.get(i).getEiType() == EIType.IMPORTANT_URGENT) {
        listIMPORTANT_URGENT.add(taskList.get(i)); i++;
      }
      while (i < taskList.size() && taskList.get(i).getEiType() == EIType.IMPORTANT_NOT_URGENT) {
        listIMPORTANT_NOT_URGENT.add(taskList.get(i)); i++;
      }
      while (i < taskList.size() && taskList.get(i).getEiType() == EIType.NOT_IMPORTANT_URGENT){
        listNOT_IMPORTANT_URGENT.add(taskList.get(i)); i++;
      }
      while (i < taskList.size() && taskList.get(i).getEiType() == EIType.NOT_IMPORTANT_NOT_URGENT) {
        listNOT_IMPORTANT_NOT_URGENT.add(taskList.get(i)); i++;
      }
    }

    List<Task> sortedListPENDING = sortTask(listPENDING);
    List<Task> sortedListIMPORTANT_URGENT = sortTask(listIMPORTANT_URGENT);
    List<Task> sortedListIMPORTANT_NOT_URGENT = sortTask(listIMPORTANT_NOT_URGENT);
    List<Task> sortedListNOT_IMPORTANT_URGENT = sortTask(listNOT_IMPORTANT_URGENT);
    List<Task> sortedListNOT_IMPORTANT_NOT_URGENT = sortTask(listNOT_IMPORTANT_NOT_URGENT);

    return DashBoardResponse.builder()
      .pending(new TaskListResponse(TaskResponse.convert(sortedListPENDING, isViewDateTime)))
      .important_urgent(new TaskListResponse(TaskResponse.convert(sortedListIMPORTANT_URGENT, isViewDateTime)))
      .important_not_urgent(new TaskListResponse(TaskResponse.convert(sortedListIMPORTANT_NOT_URGENT, isViewDateTime)))
      .not_important_urgent(new TaskListResponse(TaskResponse.convert(sortedListNOT_IMPORTANT_URGENT, isViewDateTime)))
      .not_important_not_urgent(new TaskListResponse(TaskResponse.convert(sortedListNOT_IMPORTANT_NOT_URGENT, isViewDateTime)))
      .build();
  }

  //prev, next 순서에 따라 재배열하는 메서드
  private List<Task> sortTask(List<Task> list) {
    List<Task> sortedList = new ArrayList<>();
    Task current = null;

    //첫 번째 값을 찾음(prev가 null인 경우)
    for (Task task: list) {
      if (task.getPrev() == null) {
        sortedList.add(task);
        current = task;
        break;
      }
    }

    //값이 하나도 없을 때
    if (current == null)
      return new ArrayList<>();

    //next next 연결
    while (sortedList.size() != list.size()) {
      current = current.getNext();

      if (current == null)
        break;

      sortedList.add(current);
    }

    //@TODO 오류처리 0 list.size != sortedList.sie

    return sortedList;
  }

  public void move(Long taskId, TaskMoveRequest taskList, Member member) {
    Task findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    //출발지 리스트 연결
    Task prev = findTask.getPrev();
    Task next = findTask.getNext();

    findTask.setPrevTask(null);
    findTask.setNextTask(null);

    em.flush();
    em.clear();

    findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
    if (prev != null)
      prev= taskRepository.findById(prev.getId())
        .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
    if (next != null)
      next = taskRepository.findById(next.getId())
        .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    if (prev != null)
      prev.setNextTask(next);

    if (next != null)
      next.setPrevTask(prev);

    //목적지의 task 리스트 분석
    List<Long> tasks = taskList.getTasks();

    int idx = -1;
    //나 찾기
    for (int i =0; i<tasks.size(); i++) {
      if (tasks.get(i) == findTask.getId()) {
        idx = i;
        break;
      }
    }

    //내가 없으면
    if (idx == -1)
      throw new InputMismatchException("입력받은 일정 배열이 이상합니다");

    Task prev2 = null;
    Task next2 = null;
    if (idx-1 >= 0) {
      prev2 = taskRepository.findById(tasks.get(idx-1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));
      prev2.setNextTask(findTask);
    }

    if (idx+1 <tasks.size()) {
      next2 = taskRepository.findById(tasks.get(idx+1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));
      next2.setPrevTask(findTask);
    }

    findTask.setEiType(taskList.getEi_type());

    em.flush();
    em.clear();

    findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    findTask.setPrevTask(prev2);
    findTask.setNextTask(next2);
  }

  public void delete(Long taskId, Member member) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    Task prev = task.getPrev();
    Task next = task.getNext();

    task.setPrevTask(null);
    task.setNextTask(null);

    em.flush();
    em.clear();

    if (prev != null)
      prev = taskRepository.findById(prev.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
    if (next != null)
      next = taskRepository.findById(next.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    if (prev != null)
      prev.setNextTask(next);
    if (next != null)
      next.setPrevTask(prev);

    taskRepository.delete(task);
  }

  public void edit(Long taskId, TaskEditRequest request) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    LocalDateTime dateTime = request.getEnd_at();
    //time이 포함되지 않았다면 그냥 저장
    if (dateTime != null && !request.is_time_include())
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    task.edit(request.getTitle(), request.getDescription(), dateTime, (dateTime != null)? request.is_time_include(): null);
  }

  @Transactional(readOnly = true)
  public TaskResponse getInfo(Long taskId, Member member) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    if (task.getMember().getId() != member.getId()) {
      throw new ForbiddenException("일정에 대한 조회 권한이 없습니다");
    }

    return TaskResponse.builder()
      .id(taskId)
      .title(task.getTitle())
      .description(task.getDescription())
      .endAt(task.getEndAt())
      .isTimeInclude(task.getIsTimeInclude())
      .isCompleted(task.getIsCompleted())
      .eiType(task.getEiType())
      .build();
  }

  public void editCheck(Long taskId, TaskCheckDto dto) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NoSuchElementException("일정을 찾을 수 없습니다"));

    task.check(dto.getIs_checked());
  }

  public void cleanCompleteTasks(Member member) {
    //완료됐으면서 + hisotry가 아닌애들 다 보내기
    List<Task> taskList = taskRepository.findByMemberAndIsCompletedIsTrueAndIsHistoryIsFalse(member);

    for (Task task: taskList) {
      task = taskRepository.findById(task.getId()).orElseThrow(() -> new InternalServerErrorException("asdf"));
      historyRepository.save(History.makeHistory(member, task));

      Task prev = task.getPrev();
      Task next = task.getNext();

      task.setPrevTask(null);
      task.setNextTask(null);

      //영속성 컨텍스트 비우가
      em.flush();
      em.clear();

      if (prev != null)
        prev = taskRepository.findById(prev.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
      if (next != null)
        next = taskRepository.findById(next.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

      if (prev != null)
        prev.setNextTask(next);
      if (next != null)
        next.setPrevTask(prev);
    }
  }

  @Transactional
  public void scheduleTaskTypeRotation(){

    List<Task> taskNotUrgencyTasks = taskRepository.findNotUrgencyTask(LocalDateTime.now());

    for(Task task : taskNotUrgencyTasks){
      editToUrgentEiType(task);
    }
  }

  private void editToUrgentEiType(Task task) {
    Map<EIType, EIType> urgencyRotationMap = new HashMap<>();
    urgencyRotationMap.put(EIType.IMPORTANT_NOT_URGENT, EIType.IMPORTANT_URGENT);
    urgencyRotationMap.put(EIType.NOT_IMPORTANT_NOT_URGENT, EIType.NOT_IMPORTANT_URGENT);

    EIType newEiType = urgencyRotationMap.get(task.getEiType());
    if (newEiType != null) {
      task.setEiType(newEiType);
    }
  }
}
