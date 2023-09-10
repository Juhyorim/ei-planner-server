package com.kihyaa.Eiplanner.dto;

import com.kihyaa.Eiplanner.domain.Task;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryTaskDto {
    @NotNull
    private Long id;
    private String title;
    private String description;
    private LocalDate end_date;
    private LocalTime end_time;
    private LocalDateTime completed_at;

    public static HistoryTaskDto of(Task task) {
        return new HistoryTaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getEndDate(),
                task.getEndTime(),
                task.getCompletedAt()
        );
    }
}