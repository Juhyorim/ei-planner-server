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
public class DashBoardResponse {

  private TaskListResponse pending;
  private TaskListResponse important_urgent;
  private TaskListResponse important_not_urgent;
  private TaskListResponse not_important_urgent;
  private TaskListResponse not_important_not_urgent;


}
