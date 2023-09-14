package com.kihyaa.Eiplanner.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "setting_id")
    private Setting setting;

    private String nickname;
    private String email;
    private String password;
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void changeProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void deleteProfileImg() {
        this.profileImgUrl = "";
    }
}

