package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.dto.DashBoardResponse;
import com.kihyaa.Eiplanner.dto.MakeTaskRequest;
import com.kihyaa.Eiplanner.dto.TaskMoveRequest;
import com.kihyaa.Eiplanner.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {

  private final TaskService taskService;

  //일정 만들기
  @PostMapping("/") 
  public ResponseEntity makeTask(@RequestBody @Valid MakeTaskRequest request) {

    //시간이 있으면 날짜는 필수
    if (request.getEnd_date() == null && request.getEnd_time() != null)
      return ResponseEntity.badRequest().build();

    taskService.makeTask(request);

    return ResponseEntity.ok().build();
  }

  //일정 타입, 위치 옮기기
  @PutMapping("/{task_id}/move")
  public ResponseEntity moveTask(@PathVariable("task_id")Long taskId, @RequestBody TaskMoveRequest taskList) {
    taskService.move(taskId, taskList);

    return ResponseEntity.ok().build();
  }


  //일정 삭제
  @DeleteMapping("/{task_id}")
  public ResponseEntity deleteTask(@PathVariable("task_id") Long taskId) {
    taskService.delete(taskId);

    return ResponseEntity.ok().build();
  }

  //일정 전체조회
  @GetMapping("/")
  public ResponseEntity getAllTasks() {
    DashBoardResponse response = taskService.getAllTask();
    return ResponseEntity.ok(response);
  }
  //일정 글 수정
}
