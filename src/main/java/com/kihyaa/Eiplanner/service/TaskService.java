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

  @Transactional
  public TaskResponse makeTask(MakeTaskRequest request, Member member) {
    Long lastSeqNum = taskRepository.findLastSeqNum(member, EIType.PENDING)
      .orElseGet(() -> 0L);
    Long currSeqNum = lastSeqNum + 11; //seqnum은 11부터 시작

    //dateTime과 시간 설정
    LocalDateTime dateTime = checkAndEditDateTime(request.getEndAt(), request.getIsTimeInclude());

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description((request.getDescription() != null)? request.getDescription() : null)
      .endAt((dateTime != null) ? dateTime: null)
      .isTimeInclude((dateTime != null)? request.getIsTimeInclude(): false) //dateTime이 안들어오면 timeInclude는 false여야함
      .seqNum(currSeqNum)
      .build();

    task = taskRepository.save(task);

    return TaskResponse.convert(task);
  }

  private LocalDateTime checkAndEditDateTime(LocalDateTime dateTime, boolean isTimeInclude) {
    //timeInclude가 false인데 dateTime이 null이 아니면 -> 시간은 쓸모없는거니까 시간부분 초기화
    if (dateTime != null && !isTimeInclude)
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    return dateTime;
  }

  @Transactional(readOnly = true)
  public DashBoardResponse getAllTask(Member member) {

    //@TODO 인덱스 적용 후 type별 정렬되는지도 확인부탁
    //Member의 history가 아닌 모든 task를 가져옴 - 정렬은 EiType으로
    List<Task> taskList = taskRepository.findByMemberAndIsHistoryIsFalseOrderBySeqNumAsc(member);

    Map<EIType, List<Task>> taskMap = new HashMap<>();

    //task type별로 해시에 넣기
    for (EIType type: EIType.values())
      taskMap.put(type, new ArrayList<>());

    for (Task task: taskList) {
      taskMap
        .computeIfAbsent(task.getEiType(), k -> new ArrayList<>())
        .add(task);
    }

    return DashBoardResponse.make(taskMap);
  }

  //return: 리소스가 갱신되었으면 true, 아니면 false, 잘못되면 excpetion
  //(1)이동할 task의 id와 (2)내가 이동한 목적지의 task 리스트를 입력으로 받음
  @Transactional
  public boolean move(Long taskId, TaskMoveRequest taskList, Member member) throws InputMismatchException {
    Task findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    //<<목적지의 task 리스트 분석>>
    List<Long> inputTaskIdList = taskList.getTasks();
    Integer taskIdx = findTaskIdx(findTask, inputTaskIdList); //목적지 배열에 내가 없으면 InputMisMatchException 발생

    List<Task> typeEqualsTaskList = taskRepository.findByMemberAndEiTypeAndIsHistoryIsFalseOrderBySeqNumAsc(member, taskList.getEi_type());

    //아무 이동도 없는지 확인 + 입력받은 배열과 데이터베이스 배열이 동일한지 검증
    if (isDontMove(taskId, inputTaskIdList, typeEqualsTaskList))
      return false;

    //<<순서번호 부여>>
    Long seqNum = 0L;

    if (taskIdx == 0)
      seqNum = getSeqNumFirstPos(typeEqualsTaskList); //맨 앞 삽입
    else if (taskIdx == inputTaskIdList.size()-1)
      seqNum = getSeqNumLastPos(typeEqualsTaskList); //맨 뒤 삽입
    else
      seqNum = getSeqNumCenterPos(inputTaskIdList, taskIdx, typeEqualsTaskList); //중간 삽입

    findTask.setSeqNum(seqNum);
    findTask.setEiType(taskList.getEi_type());

    return true;
  }

  private static boolean isDontMove(Long taskId, List<Long> inputTaskIdList, List<Task> typeEqualsTaskList) {
    boolean equalsMove = false; //이동안하는 경우(같은 곳으로 이동한 경우)인지 확인
    int j = 0;

    for (int i = 0; i< inputTaskIdList.size(); i++) {
      Long inputTaskId = inputTaskIdList.get(i);
      Long matchTaskId = null;
      if (j < typeEqualsTaskList.size())
        matchTaskId = typeEqualsTaskList.get(j).getId();

      if (inputTaskId == taskId) {
        //(1)이동 안하는 경우인지 확인
        if (matchTaskId != null && matchTaskId == inputTaskId) {
          equalsMove = true;
          j++;
        }

        continue;
      }

      //(2)같은 type으로 이동한 경우 -> taskId와 같은 거 스킵
      if (matchTaskId == taskId) {
        j++;
        matchTaskId = typeEqualsTaskList.get(j).getId();
      }

      if (matchTaskId != inputTaskId)
        throw new InputMismatchException("입력받은 배열이 이상합니다");

      j++;
    }

    return equalsMove;
  }

  private Long getSeqNumCenterPos(List<Long> inputTaskIdList, Integer taskIdx, List<Task> typeEqualsTaskList) {
    Long mySeqNum;
    Task prev = taskRepository.findById(inputTaskIdList.get(taskIdx -1))
      .orElseThrow(() -> new InternalServerErrorException("오류"));

    Task next = taskRepository.findById(inputTaskIdList.get(taskIdx +1))
      .orElseThrow(() -> new InternalServerErrorException("오류"));

    Long prevSeqNum = prev.getSeqNum();
    Long nextSeqNum = next.getSeqNum();

    if (nextSeqNum-prevSeqNum == 1) {
      pushOneSeqNum(taskIdx, typeEqualsTaskList);
      mySeqNum = nextSeqNum;
    } else {
      mySeqNum = (prevSeqNum + 1 + nextSeqNum)/2;
    }

    return mySeqNum;
  }

  private static Long getSeqNumLastPos(List<Task> typeEqualsTaskList) {
    Long lastSeqNum = typeEqualsTaskList.get(typeEqualsTaskList.size() - 1).getSeqNum();
    return lastSeqNum+11;
  }

  private Long getSeqNumFirstPos(List<Task> typeEqualsTaskList) {
    Long mySeqNum;
    //(1)빈 배열 첫 삽입일 경우
    if (typeEqualsTaskList.size() == 0) {
      mySeqNum = 11L;
    }
    else {
      Long seqNum = typeEqualsTaskList.get(0).getSeqNum();
      if (seqNum == 0) { //seqnum 값이 0인 경우가 있을 경우
        pushOneSeqNum(0, typeEqualsTaskList); //뒤로 한 칸씩 땡김
        mySeqNum = 0L;
      } else {
        mySeqNum = seqNum/2; //중간값으로 삽입
      }
    }

    return mySeqNum;
  }

  //인덱스 idx 값부터 한 칸씩 뒤로 미는 함수
  private void pushOneSeqNum(int idx, List<Task> taskListTypeEquals) {
    Task task = taskListTypeEquals.get(idx);

    task.setSeqNum(task.getSeqNum()+1);
    long prevSeqNum = task.getSeqNum();
    idx++;

   for (int i = idx; i<taskListTypeEquals.size(); i++) {
      task = taskListTypeEquals.get(i);
      if (task.getSeqNum() != prevSeqNum)
        break;

      task.setSeqNum(prevSeqNum + 1);
      prevSeqNum++;
    }
  }

  private static Integer findTaskIdx(Task findTask, List<Long> tasks) {
    //나 찾기
    for (int i = 0; i< tasks.size(); i++) {
      if (tasks.get(i).equals(findTask.getId()))
        return i;
    }

    //목적지 배열에 내가 없으면 - InputMismatchException
    throw new InputMismatchException("입력받은 일정 배열이 이상합니다1");
  }

  @Transactional
  public void delete(Long taskId, Member member) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
    
    if (!member.getId().equals(task.getMember().getId()))
      throw new ForbiddenException("일정에 대한 삭제 권한이 없습니다");

    taskRepository.delete(task);
  }

  @Transactional
  public TaskResponse edit(Long taskId, TaskEditRequest request) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    LocalDateTime dateTime = request.getEnd_at();
    //time이 포함되지 않았다면 그냥 저장
    if (dateTime != null && !request.is_time_include())
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    task.edit(request.getTitle(), request.getDescription(), dateTime, (dateTime != null)? request.is_time_include(): false);

    return TaskResponse.builder()
      .id(task.getId())
      .title(task.getTitle())
      .description(task.getDescription())
      .endAt(task.getEndAt())
      .isTimeInclude(task.getIsTimeInclude())
      .isCompleted(task.getIsCompleted())
      .eiType(task.getEiType())
      .build();
  }

  @Transactional(readOnly = true)
  public TaskResponse getInfo(Long taskId, Member member) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    if (!task.getMember().getId().equals(member.getId()))
      throw new ForbiddenException("일정에 대한 조회 권한이 없습니다");

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
  public CompleteTaskList cleanCompleteTasks(Member member) {
    //완료됐으면서 + hisotry가 아닌애들 다 보내기
    List<Task> taskList = taskRepository.findByMemberAndIsCompletedIsTrueAndIsHistoryIsFalse(member);

    for (Task task: taskList)
      historyRepository.save(History.makeHistory(member, task));

    return CompleteTaskList.convert(taskList);
  }

//  @Transactional
//  public void scheduleTaskTypeRotation(LocalDateTime now){
//
//    List<Task> taskNotUrgencyTasks = taskRepository.findNotUrgencyTask(now);
//    for(Task task : taskNotUrgencyTasks){
//      fetchAndMoveTask(task);
//      editToUrgentEiType(task);
//    }
//  }

//  @Transactional
//  public void editToUrgentEiType(Task task) {
//    Map<EIType, EIType> urgencyRotationMap = new HashMap<>();
//    urgencyRotationMap.put(EIType.IMPORTANT_NOT_URGENT, EIType.IMPORTANT_URGENT);
//    urgencyRotationMap.put(EIType.NOT_IMPORTANT_NOT_URGENT, EIType.NOT_IMPORTANT_URGENT);
//
//    EIType newEiType = urgencyRotationMap.get(task.getEiType());
//    if (newEiType != null) {
//      task.setEiType(newEiType);
//    }
//  }
//
//  public void fetchAndMoveTask(Task task){
//
//    Map<EIType, EIType> urgencyRotationMap = new HashMap<>();
//    urgencyRotationMap.put(EIType.IMPORTANT_NOT_URGENT, EIType.IMPORTANT_URGENT);
//    urgencyRotationMap.put(EIType.NOT_IMPORTANT_NOT_URGENT, EIType.NOT_IMPORTANT_URGENT);
//    List<Task> taskList = new ArrayList<>();
//
//    Optional<Task> t = taskRepository.findByMemberAndEiTypeAndPrevIsNullAndIsHistoryIsFalseAndIsCompletedIsFalse(task.getMember(), urgencyRotationMap.get(task.getEiType()));
//
//    if(t.isPresent()){
//      taskList = getAllLinkedList(t.get());
//    }
//    taskList.add(task);
//
//    List<Long> taskIds = taskList.stream().map(Task::getId).toList();
//
//    TaskMoveRequest request = new TaskMoveRequest(urgencyRotationMap.get(task.getEiType()), taskIds);
//
//    move(task.getId(), request, task.getMember());
//
//  }
//
//  private List<Task> getAllLinkedList(Task task){
//    List<Task> taskList = new ArrayList<>();
//
//    while(task.getNext() != null){
//      taskList.add(task);
//      task = task.getNext();
//    }
//    taskList.add(task);
//
//    return taskList;
//  }

}
