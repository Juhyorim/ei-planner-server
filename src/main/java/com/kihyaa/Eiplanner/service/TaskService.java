package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import com.kihyaa.Eiplanner.dto.MakeTaskRequest;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public void makeTask(MakeTaskRequest request) {
    Member member = memberRepository.findById(request.getUser_pk())
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
  }

  public void getAllTask() {




  }

}
