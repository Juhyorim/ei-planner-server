package com.kihyaa.Eiplanner.dto;

import com.kihyaa.Eiplanner.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TaskResponse {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime end_at;
  private boolean isTimeInclude;

  public static List<TaskResponse> convert(List<Task> taskList) {
    List<TaskResponse> responses = new ArrayList<>();
    for (Task task: taskList) {
      responses.add(
        TaskResponse.builder()
          .id(task.getId())
          .title(task.getTitle())
          .description(task.getDescription())
          .end_at(task.getEndAt())
          .isTimeInclude(task.getIsTimeInclude())
          .build()
      );
    }

    return responses;
  }
}
