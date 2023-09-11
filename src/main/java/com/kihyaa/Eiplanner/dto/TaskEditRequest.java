package com.kihyaa.Eiplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskEditRequest {
  private String title;
  private String description;
  private LocalDate end_date;
  @Schema(example = "05:12")
  private String end_time;
}
