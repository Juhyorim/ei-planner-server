package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.*;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.exception.MessageCode;
import com.kihyaa.Eiplanner.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {

  private final TaskService taskService;

  //일정 만들기
  @PostMapping
  public ResponseEntity makeTask(@RequestBody @Valid MakeTaskRequest request, @CurrentMember Member member) {

    return ResponseEntity.ok(taskService.makeTask(request, member));
  }

//  //일정 타입, 위치 옮기기
//  @PutMapping("/{task_id}/move")
//  public ResponseEntity moveTask(@PathVariable("task_id")Long taskId, @RequestBody TaskMoveRequest taskList, @CurrentMember Member member) {
//    boolean move = taskService.move(taskId, taskList, member);
//
//    if (move)
//      return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
//    else
//      return ApiResponse.createResponse(MessageCode.SUCCESS_BUT_NOTHING_HAPPENDED, HttpStatus.OK);
//  }
//
//  //일정 삭제
//  @DeleteMapping("/{task_id}")
//  public ResponseEntity deleteTask(@PathVariable("task_id") Long taskId, @CurrentMember Member member) {
//    taskService.delete(taskId, member);
//
//    return ApiResponse.createResponse(MessageCode.SUCCESS_DELETE_RESOURCE, HttpStatus.OK);
//  }
//
//  //일정 전체조회
//  @GetMapping
//  public ResponseEntity<DashBoardResponse> getAllTasks(@CurrentMember Member member) {
//    DashBoardResponse response = taskService.getAllTask(member);
//    return ResponseEntity.ok(response);
//  }
//
//  //일정 내용 수정
//  @PutMapping("/{task_id}")
//  public ResponseEntity editTask(@PathVariable("task_id")Long taskId, @RequestBody @Valid TaskEditRequest request) {
//    return ResponseEntity.ok(taskService.edit(taskId, request));
//  }
//
//  //일정 상세보기
//  @GetMapping("/{task_id}")
//  public ResponseEntity<TaskResponse> getTaskInfo(@PathVariable("task_id")Long taskId, @CurrentMember Member member) {
//    TaskResponse info = taskService.getInfo(taskId, member);
//
//    return ResponseEntity.ok(info);
//  }
//
//  //일정 완료 체크, 체크취소
//  @PutMapping("/{task_id}/checked")
//  public ResponseEntity<ApiResponse> completeCheck(@PathVariable("task_id")Long taskId, @RequestBody TaskCheckDto dto) {
//    taskService.editCheck(taskId, dto);
//
//    return ApiResponse.createResponse(MessageCode.SUCCESS_UPDATE_RESOURCE, HttpStatus.OK);
//  }
//
//  //대시보드 완료 일정 정리
//  @PutMapping("/tasks/clean")
//  public ResponseEntity cleanCompleteTasks(@CurrentMember Member member) {
//
//    return ResponseEntity.ok(taskService.cleanCompleteTasks(member));
//  }
}
