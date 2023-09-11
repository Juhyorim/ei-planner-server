package com.kihyaa.Eiplanner.dto;

import com.kihyaa.Eiplanner.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryTaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime end_at;
    private Boolean isTimeInclude;
    private LocalDateTime completed_at;

    public static HistoryTaskDto of(Task task) {
        return new HistoryTaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getEndAt(),
                task.getIsTimeInclude(),
                task.getCompletedAt()
        );
    }
}