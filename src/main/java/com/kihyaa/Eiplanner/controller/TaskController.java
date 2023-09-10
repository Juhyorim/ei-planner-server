package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.dto.MakeTaskRequest;
import com.kihyaa.Eiplanner.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
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
  
  //일정 전체조회

}
