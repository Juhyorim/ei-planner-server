package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final MemberRepository memberRepository;
  private final EntityManager em;

  @Transactional
  public Long makeTask(MakeTaskRequest request) {
    Member member = memberRepository.findById(request.getMember_id())
        .orElseThrow(() -> new NoSuchElementException("해당하는 멤버가 없습니다"));

    //EIType이 PENDING이고, Next가 null인 (마지막) 거 가져옴
    List<Task> taskList = taskRepository.findByMemberAndEiTypeAndNext(member, EIType.PENDING, null);

    Task prev = null;

    //첫 번째가 아니라면
    if (taskList.size() != 0)
      prev = taskList.get(0);

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description(request.getDescription())
      .endDate((request.getEnd_date() != null) ? request.getEnd_date(): null)
      .endTime((request.getEnd_time() != null) ? LocalTime.parse(request.getEnd_time()): null)
      .prev(prev)
      .build();

    //이전꺼에 연결
    if (prev != null)
      prev.setNextTask(task);

    taskRepository.save(task);

    return task.getId();
  }

  public DashBoardResponse getAllTask() {
    Member member = memberRepository.findById(1L)
      .orElseThrow(() -> new NoSuchElementException());

    List<Task> taskList = taskRepository.findByMemberOrderByEiType(member);

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

  @Transactional
  public void move(Long taskId, TaskMoveRequest taskList) {
    Task findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 task가 없습니다"));

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
      prev2 = taskRepository.findById(tasks.get(idx-1)).orElseThrow(() -> new NoSuchElementException());
      prev2.setNextTask(findTask);
    }

    if (idx+1 <tasks.size()) {
      next2 = taskRepository.findById(tasks.get(idx+1)).orElseThrow(() -> new NoSuchElementException());
      next2.setPrevTask(findTask);
    }

    findTask.setEiType(taskList.getEi_type());

    em.flush();
    em.clear();

    findTask = taskRepository.findById(taskId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 task가 없습니다"));

    findTask.setPrevTask(prev2);
    findTask.setNextTask(next2);
  }
}
