package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.service.TaskService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {

  private final TaskService taskService;

  //일정 만들기
  @PostMapping
  public ResponseEntity makeTask(@RequestBody @Valid MakeTaskRequest request, @CurrentMember Member member) {

    //시간이 있으면 날짜는 필수
    if (request.getEnd_date() == null && request.getEnd_time() != null)
      return ResponseEntity.badRequest().build();

    taskService.makeTask(request, member);

    return ResponseEntity.ok().build();
  }

  //일정 타입, 위치 옮기기
  @PutMapping("/{task_id}/move")
  public ResponseEntity moveTask(@PathVariable("task_id")Long taskId, @RequestBody TaskMoveRequest taskList, @CurrentMember Member member) {
    taskService.move(taskId, taskList, member);

    return ResponseEntity.ok().build();
  }


  //일정 삭제
  @DeleteMapping("/{task_id}")
  public ResponseEntity deleteTask(@PathVariable("task_id") Long taskId, @CurrentMember Member member) {
    taskService.delete(taskId, member);

    return ResponseEntity.ok().build();
  }

  //일정 전체조회
  @GetMapping
  public ResponseEntity getAllTasks(@CurrentMember Member member) {
    DashBoardResponse response = taskService.getAllTask(member);
    return ResponseEntity.ok(response);
  }

  //일정 내용 수정
  @PutMapping("/{task_id}")
  public ResponseEntity editTask(@PathVariable("task_id")Long taskId, @RequestBody TaskEditRequest request, @CurrentMember Member member) {
    taskService.edit(taskId, request, member);

    ApiResponse response = ApiResponse.builder()
      .code(200)
      .message("일정이 성공적으로 수정되었습니다")
      .time_stamp(LocalDateTime.now())
      .build();

    return ResponseEntity.ok(response);
  }

  //일정 상세보기
  @GetMapping("/{task_id}")
  public ResponseEntity getTaskInfo(@PathVariable("task_id")Long taskId, @CurrentMember Member member) {
    TaskResponse info = taskService.getInfo(taskId, member);

    return ResponseEntity.ok(info);
  }

  //일정 완료 체크, 체크취소
  @PutMapping("/{task_id}/checked")
  public ResponseEntity completeCheck(@PathVariable("task_id")Long taskId, TaskCheckDto dto) {
    taskService.editCheck(taskId, dto);

    return ResponseEntity.ok().build();
  }
}
