package com.kihyaa.Eiplanner.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EIType eiType = EIType.PENDING;

    private Boolean isCompleted = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime completedAt = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="next_task_id") //@TODO BuilderDefault 적요앙ㄴ해도 되는지 확인
    private Task next = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prev_task_id") //@TODO BuilderDefault 적요앙ㄴ해도 되는지 확인
    private Task prev = null; //@TODO 삭제할 때 꼭 고려

    @Builder
    public Task(Member member, String title, String description, LocalDate endDate, LocalTime endTime, Task prev) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.endTime = endTime;
        this.prev = prev;
    }

    public void setNextTask(Task task) {
        this.next = task;
    }

    public void setPrevTask(Task task) {
        this.prev = task;
    }

    public void setEiType(EIType eiType) {
        this.eiType = eiType;
    }
}
