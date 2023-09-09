package com.kihyaa.Eiplanner.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Lob
    private String description;

    @Column(columnDefinition = "DATE")
    private LocalDate endDate;

    @Column(columnDefinition = "TIME")
    private LocalTime endTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private EIType eiType;

    private Boolean isCompleted;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime completedAt;
}
