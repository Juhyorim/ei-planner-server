package com.kihyaa.Eiplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MakeTaskRequest {

  @Schema(example = "일정제목")
  @NotNull
  private String title;
  private String description;

  @JsonProperty("end_at")
  private LocalDateTime endAt;

  @JsonProperty("is_time_include")
  private Boolean isTimeInclude;
}
