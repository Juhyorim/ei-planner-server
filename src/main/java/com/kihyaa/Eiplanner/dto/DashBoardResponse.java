package com.kihyaa.Eiplanner.dto;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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


  public static DashBoardResponse make(Map<EIType, List<Task>> sortedTaskMap, boolean isViewDateTime) {
    return DashBoardResponse.builder()
      .pending(new TaskListResponse(TaskResponse.convert(sortedTaskMap.get(EIType.PENDING), isViewDateTime)))
      .important_urgent(new TaskListResponse(TaskResponse.convert(sortedTaskMap.get(EIType.IMPORTANT_URGENT), isViewDateTime)))
      .important_not_urgent(new TaskListResponse(TaskResponse.convert(sortedTaskMap.get(EIType.IMPORTANT_NOT_URGENT), isViewDateTime)))
      .not_important_urgent(new TaskListResponse(TaskResponse.convert(sortedTaskMap.get(EIType.NOT_IMPORTANT_URGENT), isViewDateTime)))
      .not_important_not_urgent(new TaskListResponse(TaskResponse.convert(sortedTaskMap.get(EIType.NOT_IMPORTANT_NOT_URGENT), isViewDateTime)))
      .build();
  }
}
