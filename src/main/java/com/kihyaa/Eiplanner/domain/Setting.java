package com.kihyaa.Eiplanner.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Setting {

    @Id
    @Column(name = "setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean isViewDateTime = true;
    private Integer autoEmergencySwitch;
}
