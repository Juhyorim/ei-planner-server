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

    @Column(name = "seq_num")
    private Long seqNum = 0L;

    private Boolean isHistory = false;

    public Task(Member member, String title, boolean isHistory, LocalDateTime completedAt) {
        this.member = member;
        this.title = title;
        this.isHistory = isHistory;
        this.completedAt = completedAt;
    }

    public Task(Member member, String title,  LocalDateTime endAt, boolean isCompleted, EIType eiType, boolean isHistory) {
        this.member = member;
        this.title = title;
        this.endAt = endAt;
        this.isCompleted = isCompleted;
        this.eiType = eiType;
        this.isHistory = isHistory;
    }

    @Builder
    public Task(Member member, String title, String description, LocalDateTime endAt, boolean isTimeInclude, Long seqNum) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.endAt = endAt;
        this.isTimeInclude = isTimeInclude;
        this.seqNum = seqNum;
    }

    public void setEiType(EIType eiType) {
        this.eiType = eiType;
    }

    public void setSeqNum(Long seqNum) {
        this.seqNum = seqNum;
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
