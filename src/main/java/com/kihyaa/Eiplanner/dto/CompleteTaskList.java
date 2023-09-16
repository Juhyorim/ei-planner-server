package com.kihyaa.Eiplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kihyaa.Eiplanner.domain.Task;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class CompleteTaskList {
  @JsonProperty("complete_task")
  private List<Long> completeTask;

  public static CompleteTaskList convert(List<Task> taskList) {
    List<Long> completeTask = new ArrayList<>();

    for (Task task: taskList)
      completeTask.add(task.getId());

    return new CompleteTaskList(completeTask);
  }
}
