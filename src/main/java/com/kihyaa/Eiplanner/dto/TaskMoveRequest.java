package com.kihyaa.Eiplanner.dto;


import com.kihyaa.Eiplanner.domain.EIType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskMoveRequest {
  private EIType ei_type;
  private List<Long> tasks;

}
