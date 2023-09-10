package com.kihyaa.Eiplanner.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    private String nickname;
    private String email;
    private String password;
    private String profileImgUrl;
}

