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

    List<Task> taskList = taskRepository.findByMemberAndEiType(member, EIType.PENDING);

    boolean isFirstPosition;
    Task last = null;

    if (taskList.size() == 0) {
      isFirstPosition = true;
    } else {
      isFirstPosition = false;

      //next가 null인 애 찾기
      for (Task task: taskList) {
        if (task.getNext() == null) {
          last = task;
          break;
        }
      }
    }

    Task task = Task.builder()
      .member(member)
      .title(request.getTitle())
      .description(request.getDescription())
      .endDate(request.getEnd_date())
      .endTime(LocalTime.parse(request.getEnd_time()))
      .isFirstPosition(isFirstPosition)
      .build();

    //이전꺼에 연결
    if (last != null)
      last.setNextTask(task);

    taskRepository.save(task);
  }

  public void getAllTask() {




  }

}
