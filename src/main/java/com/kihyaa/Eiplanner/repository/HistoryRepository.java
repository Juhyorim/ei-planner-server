package com.kihyaa.Eiplanner.repository;

import com.kihyaa.Eiplanner.domain.History;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface HistoryRepository extends JpaRepository<History, Long> {

    /**
     * 주어진 ID를 가진 회원의 완료된 작업과 관련된 히스토리들을 가져옴
     *
     * @param memberId  히스토리를 가져올 멤버의 ID.
     * @param pageable  페이지네이션 정보.
     * @return 주어진 회원의 완료된 작업들의 페이지네이트된 목록.
     */
    @Query("SELECT h " +
            "FROM History h " +
            "WHERE h.member.id = :memberId " +
            "AND h.task.isCompleted = true " +
            "ORDER BY h.task.completedAt DESC")
    Page<History> findCompletedTasksByMemberId(Long memberId, Pageable pageable);



    void deleteByTaskId(Long taskId);

    void deleteAllByMemberId(Long memberId);
}
