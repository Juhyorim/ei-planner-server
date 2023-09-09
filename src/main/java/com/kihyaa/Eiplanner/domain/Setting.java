package com.kihyaa.Eiplanner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Setting {

    @Id
    @Column(name = "setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isViewDateTime;
    private Integer autoEmergencySwitch;
}
