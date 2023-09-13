package com.kihyaa.Eiplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kihyaa.Eiplanner.domain.EIType;
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
  @JsonProperty("end_at")
  private LocalDateTime endAt;
  @JsonProperty("is_time_include")
  private Boolean isTimeInclude;
  @JsonProperty("is_completed")
  private Boolean isCompleted;

  @JsonProperty("ei_type")
  private EIType eiType;

  public static List<TaskResponse> convert(List<Task> taskList, boolean isViewDateTime) {
    List<TaskResponse> responses = new ArrayList<>();
    for (Task task: taskList) {
      responses.add(
        TaskResponse.builder()
          .id(task.getId())
          .title(task.getTitle())
          .description(task.getDescription())
          .endAt(isViewDateTime? task.getEndAt() : null)
          .isTimeInclude(task.getIsTimeInclude())
          .isCompleted(task.getIsCompleted())
          .eiType(task.getEiType())
          .build()
      );
    }

    return responses;
  }
}
