package com.kihyaa.Eiplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TaskListResponse {
  private int count;
  private List<TaskResponse> tasks;
}