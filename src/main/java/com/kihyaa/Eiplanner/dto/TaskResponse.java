package com.kihyaa.Eiplanner.dto;

import com.kihyaa.Eiplanner.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
  private LocalDate end_date;
  private String end_time;

  public static List<TaskResponse> convert(List<Task> taskList) {
    List<TaskResponse> responses = new ArrayList<>();
    for (Task task: taskList) {
      responses.add(
        TaskResponse.builder()
          .id(task.getId())
          .title(task.getTitle())
          .description(task.getDescription())
          .end_date(task.getEndDate())
          .end_time(task.getEndTime().toString())
          .build()
      );
    }

    return responses;
  }
}
