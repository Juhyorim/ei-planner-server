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
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final HistoryRepository historyRepository;
  private final EntityManager em;

  @Transactional
  public Long makeTask(MakeTaskRequest request, Member member) {
    Task prev = getFirstTaskPending(member);

    LocalDateTime dateTime = checkAndEditDateTime(request.getEndAt(), request.getIsTimeInclude());

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description((request.getDescription() != null)? request.getDescription() : null)
      .endAt((dateTime != null) ? dateTime: null)
      .isTimeInclude((dateTime != null)? request.getIsTimeInclude(): false) //dateTime이 안들어오면 timeInclude는 false여야함
      .build();

    //prev와 연결
    connectTask(prev, task);

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

  private LocalDateTime checkAndEditDateTime(LocalDateTime dateTime, boolean isTimeInclude) {
    //timeInclude가 false인데 dateTime이 null이 아니면 -> 시간은 쓸모없는거니까 시간부분 초기화
    if (dateTime != null && !isTimeInclude)
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    return dateTime;
  }

  @Transactional(readOnly = true)
  public DashBoardResponse getAllTask(Member member) {

    //Member의 history가 아닌 모든 task를 가져옴 - 정렬은 EiType으로
    List<Task> taskList = taskRepository.findByMemberAndIsHistoryIsFalseOrderByEiType(member);

    Map<EIType, List<Task>> taskMap = new HashMap<>();

    //task type별로 해시에 넣기
    for (EIType type: EIType.values())
      taskMap.put(type, new ArrayList<>());

    for (Task task: taskList) {
      taskMap
        .computeIfAbsent(task.getEiType(), k -> new ArrayList<>())
        .add(task);
    }

    //next next 따라 정렬
    Map<EIType, List<Task>> sortedTaskMap = new HashMap<>();

    for (EIType eiType: taskMap.keySet())
      sortedTaskMap.put(eiType, sortTask(taskMap.get(eiType)));

    return DashBoardResponse.make(sortedTaskMap, member.getSetting().getIsViewDateTime());
  }

  //prev, next 순서에 따라 재배열하는 메서드
  private List<Task> sortTask(List<Task> list) {
    List<Task> sortedList = new ArrayList<>();
    Task current = null;

    //첫 번째 값을 찾음(prev가 null인 거 찾기)
    for (Task task: list) {
      if (task.getPrev() == null) {
        sortedList.add(task);
        current = task;
        break;
      }
    }

    //값이 하나라도 있을 때
    if (current != null) {
      //next next 연결
      while (sortedList.size() != list.size()) {
        current = current.getNext();

        if (current == null)
          break;

        sortedList.add(current);
      }
    }

    return sortedList;
  }

  @Transactional
  public void move(Long taskId, TaskMoveRequest taskList, Member member) {
    Task findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    //목적지의 task 리스트 분석
    List<Long> tasks = taskList.getTasks();

    int idx = findTaskIdx(findTask, tasks);

    //내가 없으면 - InputMismatchException
    if (idx == -1)
      throw new InputMismatchException("입력받은 일정 배열이 이상합니다1");

    Task futurePrev = null;
    Task futureNext = null;

    if (idx-1 >= 0) 
      futurePrev = taskRepository.findById(tasks.get(idx-1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));

    if (idx+1 <tasks.size()) 
      futureNext = taskRepository.findById(tasks.get(idx+1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));

    //입력받은 순서배열 일치여부 확인 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    //(1)prev와 next가 없는 경우 진짜없나
    if (tasks.size() == 1) {
      //타입에 맞는 모든 task 가져옴
      List<Task> typeList = taskRepository.findByMemberAndEiTypeAndIsHistoryIsFalse(member, taskList.getEi_type());
      if (typeList.size() != 0)
        throw new InputMismatchException("입력받은 일정 배열이 이상합니다2");
    }

    //(2)prev가 없는 경우 next의 prev가 진짜 없나
    if (idx == 0 && tasks.size() > 1) {
      if (futureNext.getPrev() != null)
        throw new InputMismatchException("입력받은 일정 배열이 이상합니다3");
    }

    //(3)next가 없는 경우 prev의 next가 진짜 없나
    if (idx == tasks.size()-1 && tasks.size() > 1) {
      if (futurePrev.getNext() != null)
        throw new InputMismatchException("입력받은 일정 배열이 이상합니다4");
    }

    //(4)다 있는 경우 둘이 연결되어있나
    if (idx > 0 && idx < tasks.size()-1) {
      if (futurePrev.getId()!= futureNext.getPrev().getId())
        throw new InputMismatchException("입력받은 일정 배열이 이상합니다5");
    }

    //검증완료, 연결리스트 연결작업 수행 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    Task pastPrev = findTask.getPrev();
    Task pastNext = findTask.getNext();

    //일단 연결고리 다 끊기
    findTask.setPrevTask(null);
    findTask.setNextTask(null);

    if (futurePrev != null)
      futurePrev.setNextTask(null);
    if (futureNext != null)
      futureNext.setPrevTask(null);

    if (pastPrev != null)
      pastPrev.setNextTask(null);
    if (pastNext != null)
      pastNext.setPrevTask(null);

    em.flush();
    em.clear();

    findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    if(pastPrev != null)
      pastPrev = taskRepository.findById(pastPrev.getId()).orElseThrow(() -> new NotFoundException("asdf"));
    if(pastNext != null)
      pastNext = taskRepository.findById(pastNext.getId()).orElseThrow(() -> new NotFoundException("asdf"));

    //이전꺼끼리 연결
    connectTask(pastPrev, pastNext);

    //future랑 findTask 연결
    if (futurePrev != null) {
      futurePrev = taskRepository.findById(tasks.get(idx-1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));
      futurePrev.setNextTask(findTask);
    }

    if (futureNext != null) {
      futureNext = taskRepository.findById(tasks.get(idx+1)).orElseThrow(() -> new InputMismatchException("배열에 포함된 일정을 찾을 수 없습니다"));
      futureNext.setPrevTask(findTask);
    }

    //findTask 연결, 타입수정
    findTask.setEiType(taskList.getEi_type());

    findTask.setPrevTask(futurePrev);
    findTask.setNextTask(futureNext);
  }

  private static int findTaskIdx(Task findTask, List<Long> tasks) {
    //나 찾기
    for (int i = 0; i< tasks.size(); i++) {
      if (tasks.get(i).equals(findTask.getId()))
        return i;
    }
    return -1;
  }

  @Transactional
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

    connectTask(prev, next);

    taskRepository.delete(task);
  }

  private void connectTask(Task prev, Task next) {
    if (prev != null)
      prev.setNextTask(next);
    if (next != null)
      next.setPrevTask(prev);
  }

  @Transactional
  public void edit(Long taskId, TaskEditRequest request) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    LocalDateTime dateTime = request.getEnd_at();
    //time이 포함되지 않았다면 그냥 저장
    if (dateTime != null && !request.is_time_include())
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    task.edit(request.getTitle(), request.getDescription(), dateTime, (dateTime != null)? request.is_time_include(): false);
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

  @Transactional
  public void editCheck(Long taskId, TaskCheckDto dto) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NoSuchElementException("일정을 찾을 수 없습니다"));

    task.check(dto.getIs_checked());
  }

  @Transactional
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

      connectTask(prev, next);
    }
  }

  @Transactional
  public void scheduleTaskTypeRotation(LocalDateTime now){

    List<Task> taskNotUrgencyTasks = taskRepository.findNotUrgencyTask(now);
    for(Task task : taskNotUrgencyTasks){
      fetchAndMoveTask(task);
      editToUrgentEiType(task);
    }
  }

  @Transactional
  public void editToUrgentEiType(Task task) {
    Map<EIType, EIType> urgencyRotationMap = new HashMap<>();
    urgencyRotationMap.put(EIType.IMPORTANT_NOT_URGENT, EIType.IMPORTANT_URGENT);
    urgencyRotationMap.put(EIType.NOT_IMPORTANT_NOT_URGENT, EIType.NOT_IMPORTANT_URGENT);

    EIType newEiType = urgencyRotationMap.get(task.getEiType());
    if (newEiType != null) {
      task.setEiType(newEiType);
    }
  }

  public void fetchAndMoveTask(Task task){

    Map<EIType, EIType> urgencyRotationMap = new HashMap<>();
    urgencyRotationMap.put(EIType.IMPORTANT_NOT_URGENT, EIType.IMPORTANT_URGENT);
    urgencyRotationMap.put(EIType.NOT_IMPORTANT_NOT_URGENT, EIType.NOT_IMPORTANT_URGENT);
    List<Task> taskList = new ArrayList<>();

    Optional<Task> t = taskRepository.findByMemberAndEiTypeAndPrevIsNullAndIsHistoryIsFalseAndIsCompletedIsFalse(task.getMember(), urgencyRotationMap.get(task.getEiType()));

    if(t.isPresent()){
      taskList = getAllLinkedList(t.get());
    }
    taskList.add(task);

    List<Long> taskIds = taskList.stream().map(Task::getId).toList();

    TaskMoveRequest request = new TaskMoveRequest(urgencyRotationMap.get(task.getEiType()), taskIds);

    move(task.getId(), request, task.getMember());

  }

  private List<Task> getAllLinkedList(Task task){
    List<Task> taskList = new ArrayList<>();

    while(task.getNext() != null){
      taskList.add(task);
      task = task.getNext();
    }
    taskList.add(task);

    return taskList;
  }

}
