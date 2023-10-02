package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
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
  public TaskResponse makeTask(MakeTaskRequest request, Member member) {
    Long lastSeqNum = taskRepository.findLastSeqNum(member, EIType.PENDING)
      .orElseGet(() -> 0L);
    Long currSeqNum = lastSeqNum + 11;

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

    //next next 따라 정렬
    Map<EIType, List<Task>> sortedTaskMap = new HashMap<>();

    for (EIType eiType: taskMap.keySet())
      sortedTaskMap.put(eiType, taskMap.get(eiType));

    return DashBoardResponse.make(sortedTaskMap);
  }

  //return: 리소스가 갱신되었으면 true, 아니면 false, 잘못되면 excpetion
  //(1)이동할 task의 id와 (2)내가 이동한 목적지의 task 리스트를 입력으로 받음
  @Transactional
  public boolean move(Long taskId, TaskMoveRequest taskList, Member member) {
    Task findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));

    //목적지의 task 리스트 분석 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    List<Long> inputTaskIdList = taskList.getTasks();
    int idx = findTaskIdx(findTask, inputTaskIdList);

    //내가 없으면 - InputMismatchException
    if (idx == -1)
      throw new InputMismatchException("입력받은 일정 배열이 이상합니다1");

    //실제 값과 비교 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    List<Task> typeEqualsTaskList = taskRepository.findByMemberAndEiTypeAndIsHistoryIsFalseOrderBySeqNumAsc(member, taskList.getEi_type());
    
    boolean equalsMove = false; //이동안하는 경우(같은 곳으로 이동한 경우)인지 확인
    int j = 0;

    //입력받은 배열과 실제 디비 일치하는지 확인
    for (int i =0; i<inputTaskIdList.size(); i++) {
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

    if (equalsMove)
      return false;

    //검증완료, 중간 삽입 작업 수행 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    //맨 앞 삽입일 경우
    if (idx == 0) {
      //빈 배열 첫 삽입
      if (typeEqualsTaskList.size() == 0) {
        findTask.setSeqNum(11L);
      } else {
        Long seqNum = typeEqualsTaskList.get(0).getSeqNum();
        if (seqNum == 0) { //seqnum 값이 0인 경우가 있을 경우
          plusOneSeqNum(0, typeEqualsTaskList); //뒤로 한 칸씩 땡김
          findTask.setSeqNum(0L);
        } else {
          findTask.setSeqNum(seqNum/2); //중간값으로 삽입
        }
      }
    }
    else if (idx == inputTaskIdList.size()-1) { //맨 뒤 삽입일 경우
      Long lastSeqNum = typeEqualsTaskList.get(typeEqualsTaskList.size() - 1).getSeqNum();
      findTask.setSeqNum(lastSeqNum+11);
    }
    else { //중간 삽입
//      Long prevSeqNum = typeEqualsTaskList.get(idx-1).getSeqNum();
//      Long nextSeqNum = typeEqualsTaskList.get(idx).getSeqNum();

      Task prev = taskRepository.findById(inputTaskIdList.get(idx-1))
        .orElseThrow(() -> new InternalServerErrorException("오류"));

      Task next = taskRepository.findById(inputTaskIdList.get(idx+1))
        .orElseThrow(() -> new InternalServerErrorException("오류"));

      Long prevSeqNum = prev.getSeqNum();
      Long nextSeqNum = next.getSeqNum();

      if (nextSeqNum-prevSeqNum == 1) {
        plusOneSeqNum(idx, typeEqualsTaskList);
        findTask.setSeqNum(nextSeqNum);
      } else {
        findTask.setSeqNum((prevSeqNum + 1 + nextSeqNum)/2);
      }
    }

    //타입수정
    findTask.setEiType(taskList.getEi_type());

    return true;
  }

  //인덱스 idx 값부터 한 칸씩 뒤로 미는 함수
  private void plusOneSeqNum(int idx, List<Task> taskListTypeEquals) {
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

    if (!task.getMember().getId().equals(member.getId())) {
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

//  @Transactional
//  public CompleteTaskList cleanCompleteTasks(Member member) {
//    //완료됐으면서 + hisotry가 아닌애들 다 보내기
//    List<Task> taskList = taskRepository.findByMemberAndIsCompletedIsTrueAndIsHistoryIsFalse(member);
//
//    for (Task task: taskList) {
//      task = taskRepository.findById(task.getId()).orElseThrow(() -> new InternalServerErrorException("asdf"));
//      historyRepository.save(History.makeHistory(member, task));
//
//      Task prev = task.getPrev();
//      Task next = task.getNext();
//
//      task.setPrevTask(null);
//      task.setNextTask(null);
//
//      //영속성 컨텍스트 비우가
//      em.flush();
//      em.clear();
//
//      if (prev != null)
//        prev = taskRepository.findById(prev.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
//      if (next != null)
//        next = taskRepository.findById(next.getId()).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다"));
//
//      connectTask(prev, next);
//    }
//
//    return CompleteTaskList.convert(taskList);
//  }
//
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
