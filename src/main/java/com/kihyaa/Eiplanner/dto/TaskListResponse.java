package com.kihyaa.Eiplanner.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class TaskListResponse {
  private int count;
  private List<TaskResponse> tasks;

  public TaskListResponse(List<TaskResponse> tasks) {
    this.tasks = tasks;
    this.count = tasks.size();
  }
}