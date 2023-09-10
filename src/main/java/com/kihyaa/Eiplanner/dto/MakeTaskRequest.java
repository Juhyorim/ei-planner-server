package com.kihyaa.Eiplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MakeTaskRequest {
  @NotNull
  private Long user_pk;

  @Schema(example = "일정제목")
  @NotNull
  private String title;

  private String description;
  private LocalDate end_date;

  @Schema(example = "05:12")
  private String end_time;
}
