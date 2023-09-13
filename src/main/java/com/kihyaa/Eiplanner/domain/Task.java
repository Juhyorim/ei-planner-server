package com.kihyaa.Eiplanner.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endAt;

    //시간 포함 여부
    private Boolean isTimeInclude;

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

    private Boolean isHistory = false;

    @Builder
    public Task(Member member, String title, String description, LocalDateTime endAt, boolean isTimeInclude, Task prev) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.endAt = endAt;
        this.isTimeInclude = isTimeInclude;
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

    public void edit(String title, String description, LocalDateTime endAt, boolean isTimeInclude) {
        this.title = title;
        this.description = description;
        this.endAt = endAt;
        this.isTimeInclude = isTimeInclude;
    }

    public void check(boolean checked) {
        this.isCompleted = checked;

        if (checked)
            this.completedAt = LocalDateTime.now();
        else
            this.completedAt = null;
    }

    public void sendHistory() {
        this.isHistory = true;
    }
}
