package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.Exception.ForbiddenException;
import com.kihyaa.Eiplanner.Exception.NotFoundException;
import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.History;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.repository.HistoryRepository;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final MemberRepository memberRepository;
  private final HistoryRepository historyRepository;
  private final EntityManager em;

  public Long makeTask(MakeTaskRequest request, Member member) {
    //EIType이 PENDING이고, Next가 null인 (마지막) 거 가져옴
    List<Task> taskList = taskRepository.findByMemberAndEiTypeAndNext(member, EIType.PENDING, null);

    Task prev = null;

    //첫 번째가 아니라면
    if (taskList.size() != 0)
      prev = taskList.get(0);

    LocalDateTime dateTime = request.getEndAt();
    //time이 포함되지 않았다면 그냥 저장
    if (dateTime != null && !request.getIsTimeInclude())
      dateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

    log.info("@@: " + request.getIsTimeInclude());

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description((request.getDescription() != null)? request.getDescription() : null)
      .endAt((dateTime != null) ? dateTime: null)
      .isTimeInclude((dateTime != null)? request.getIsTimeInclude(): false)
      .prev(prev)
      .build();

    //이전꺼에 연결
    if (prev != null)
      prev.setNextTask(task);

    taskRepository.save(task);

    return task.getId();
  }

  @Transactional(readOnly = true)
  public DashBoardResponse getAllTask(Member member) {

    List<Task> taskList = taskRepository.findByMemberAndIsHistoryIsFalseOrderByEiType(member);

    List<Task> listPENDING = new ArrayList<>();
    List<Task> listIMPORTANT_URGENT = new ArrayList<>();
    List<Task> listIMPORTANT_NOT_URGENT = new ArrayList<>();
    List<Task> listNOT_IMPORTANT_URGENT = new ArrayList<>();
    List<Task> listNOT_IMPORTANT_NOT_URGENT = new ArrayList<>();

    int i =0;
    while(i < taskList.size()) {
      while (taskList.get(i).getEiType() != EIType.PENDING) {
        listPENDING.add(taskList.get(i));
        i++;
      }
      while (i < taskList.size() && taskList.get(i).getEiType() != EIType.IMPORTANT_URGENT) {
        listIMPORTANT_URGENT.add(taskList.get(i));
        i++;
      }

      while (i < taskList.size() && taskList.get(i).getEiType() != EIType.IMPORTANT_NOT_URGENT) {
        listIMPORTANT_NOT_URGENT.add(taskList.get(i));
        i++;
      }

      while (i < taskList.size() && taskList.get(i).getEiType() != EIType.NOT_IMPORTANT_URGENT){
        listNOT_IMPORTANT_URGENT.add(taskList.get(i));
        i++;
      }

      while (i < taskList.size() && taskList.get(i).getEiType() != EIType.NOT_IMPORTANT_NOT_URGENT) {
        listNOT_IMPORTANT_NOT_URGENT.add(taskList.get(i));
        i++;
      }
    }

    List<Task> sortedListPENDING = sortTask(listPENDING);
    List<Task> sortedListIMPORTANT_URGENT = sortTask(listIMPORTANT_URGENT);
    List<Task> sortedListIMPORTANT_NOT_URGENT = sortTask(listIMPORTANT_NOT_URGENT);
    List<Task> sortedListNOT_IMPORTANT_URGENT = sortTask(listNOT_IMPORTANT_URGENT);
    List<Task> sortedListNOT_IMPORTANT_NOT_URGENT = sortTask(listNOT_IMPORTANT_NOT_URGENT);


    return DashBoardResponse.builder()
      .pending(
        TaskListResponse.builder()
          .count(sortedListPENDING.size())
          .tasks(TaskResponse.convert(sortedListPENDING))
          .build()
      )
      .important_urgent(
        TaskListResponse.builder()
          .count(sortedListIMPORTANT_URGENT.size())
          .tasks(TaskResponse.convert(sortedListIMPORTANT_URGENT))
          .build()
      )
      .important_not_urgent(
        TaskListResponse.builder()
          .count(sortedListIMPORTANT_NOT_URGENT.size())
          .tasks(TaskResponse.convert(sortedListIMPORTANT_NOT_URGENT))
          .build()
      )
      .not_important_urgent(
        TaskListResponse.builder()
          .count(sortedListNOT_IMPORTANT_URGENT.size())
          .tasks(TaskResponse.convert(sortedListNOT_IMPORTANT_URGENT))
          .build()
      )
      .not_important_not_urgent(
        TaskListResponse.builder()
          .count(sortedListNOT_IMPORTANT_NOT_URGENT.size())
          .tasks(TaskResponse.convert(sortedListNOT_IMPORTANT_NOT_URGENT))
          .build()
      )
      .build();
  }

  private List<Task> sortTask(List<Task> list) {
    List<Task> sortedList = new ArrayList<>();
    Task current = null;

    for (Task task: list) {
      if (task.getPrev() == null) {
        sortedList.add(task);
        current = task;
        break;
      }
    }

    if (current == null)
      return new ArrayList<>();

    while (sortedList.size() != list.size()) {
      current = current.getNext();
      sortedList.add(current);
    }

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

    if (prev != null)
      prev.setNextTask(next);

    if (next != null)
      next.setPrevTask(prev);

    //목적지의 task 리스트 분석
    List<Long> tasks = taskList.getTasks();

    int idx = -1;
    for (int i =0; i<tasks.size(); i++) {
      if (tasks.get(i) == findTask.getId()) {
        idx = i;
        break;
      }
    }

    if (idx == -1)
      throw new NoSuchElementException();

    Task prev2 = null;
    Task next2 = null;
    if (idx-1 >= 0) {
      prev2 = taskRepository.findById(tasks.get(idx-1)).orElseThrow(() -> new InputMismatchException("일정의 배열이 이상합니다"));
      prev2.setNextTask(findTask);
    }

    if (idx+1 <tasks.size()) {
      next2 = taskRepository.findById(tasks.get(idx+1)).orElseThrow(() -> new InputMismatchException("일정의 배열이 이상합니다"));
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

  public void edit(Long taskId, TaskEditRequest request, Member member) {
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
      .build();

  }


  public void editCheck(Long taskId, TaskCheckDto dto) {
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new NoSuchElementException("일정을 찾을 수 없습니다"));

    task.check(dto.getIs_checked());
  }

  public void cleanCompleteTasks(Member member) {
    List<Task> taskList = taskRepository.findByMemberAndIsCompletedIsTrue(member);

    for (Task task: taskList) {
      historyRepository.save(History.makeHistory(member, task));

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
    }

  }
}
