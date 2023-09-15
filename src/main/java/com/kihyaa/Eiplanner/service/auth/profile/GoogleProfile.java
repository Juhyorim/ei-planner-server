package com.kihyaa.Eiplanner.service.auth.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GoogleProfile extends CommonProfile {
    @JsonProperty("id")
    private String id;
    private String name;
    private String email;
    @JsonProperty("picture")
    private String profileImage;
}
