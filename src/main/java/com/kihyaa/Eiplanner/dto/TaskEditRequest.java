package com.kihyaa.Eiplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskEditRequest {
  @NotNull
  private String title;
  private String description;
  private LocalDateTime end_at;
  @JsonProperty("is_time_include")
  private boolean is_time_include;
}
