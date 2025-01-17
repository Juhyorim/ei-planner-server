package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.EIType;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

//  List<Task> findByMemberAndEiTypeAndNextIsNullAndIsHistoryIsFalse(Member member, EIType eiType);
  List<Task> findByMemberAndIsHistoryIsFalseOrderBySeqNumAsc(Member member);

  //select t from Task t where t.member=:member and t.eiType=:eiType and t.isHistory=false order by t.seqNum asc
  List<Task> findByMemberAndEiTypeAndIsHistoryIsFalseOrderBySeqNumAsc(Member member, EIType eiType);

  List<Task> findByMemberAndEiTypeAndIsHistoryIsFalse(Member member, EIType eiType);

  @Query("select max(t.seqNum) from Task t where t.member = ?1 and t.eiType = ?2 and t.isHistory = false")
  Optional<Long> findLastSeqNum(Member member, EIType eiType);

  List<Task> findByMemberAndIsCompletedIsTrueAndIsHistoryIsFalse(Member member);


//  List<Task> findByMemberAndIsHistoryIsTrueOrderByCompletedAt(Member member, Pageable pageable);

  @Query(value = "SELECT t.* " +
          "FROM task t " +
          "JOIN member m ON t.member_id = m.member_id " +
          "JOIN setting s ON m.setting_id = s.setting_id " +
          "WHERE s.auto_emergency_switch > " +
          "TIMESTAMPDIFF(HOUR, :localDateTime, t.end_at) " +
          "AND (t.ei_type = 'IMPORTANT_NOT_URGENT' OR t.ei_type = 'NOT_IMPORTANT_NOT_URGENT') " +
          "AND t.is_completed = false ",
          nativeQuery = true)
  List<Task> findNotUrgencyTask(@Param("localDateTime") LocalDateTime localDateTime);

//  Optional<Task> findByMemberAndEiTypeAndPrevIsNullAndIsHistoryIsFalseAndIsCompletedIsFalse(Member member, EIType eiType);

  List<Task> findByMemberAndEiType(Member member, EIType eiType);

  List<Task> findByMember(Member member);
}
