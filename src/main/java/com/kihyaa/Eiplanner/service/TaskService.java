package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.MakeTaskRequest;
import com.kihyaa.Eiplanner.dto.TaskMoveRequest;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
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

  public void getAllTask() {

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
