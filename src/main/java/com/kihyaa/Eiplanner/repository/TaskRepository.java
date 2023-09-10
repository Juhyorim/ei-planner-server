package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByMemberAndEiTypeAndNext(Member member, EIType eiType, Task task);
}
