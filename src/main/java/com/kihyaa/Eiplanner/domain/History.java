package com.kihyaa.Eiplanner.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    public History(Member member, Task task) {
        this.member = member;
        this.task = task;

        task.sendHistory();
    }
    public static History makeHistory(Member member, Task task) {
        History history = new History(null, member, task);
        task.sendHistory();

        return history;
    }

}

