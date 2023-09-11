package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.annotation.CurrentMember;
import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.dto.response.ApiResponse;
import com.kihyaa.Eiplanner.dto.response.GetHistoryResponse;
import com.kihyaa.Eiplanner.service.HistoryService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<GetHistoryResponse> getHistory(@CurrentMember Member member,
                                                        @PageableDefault (page=0, size=5) @Parameter(hidden = true) Pageable pageable){

        return ResponseEntity.ok(historyService.getHistory(member.getId(), pageable));
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<ApiResponse> deleteOneHistory(@PathVariable(value = "task_id") Long taskId,
                                                        @CurrentMember Member member){

        historyService.deleteOneHistory(taskId, member.getId());

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), LocalDateTime.now(), "히스토리 내 일정 삭제 성공"));
    }

    @DeleteMapping("/clean")
    public ResponseEntity<ApiResponse> deleteAllHistory(@CurrentMember Member member){

        historyService.deleteAllHistory(member.getId());

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), LocalDateTime.now(), "히스토리 비우기 성공"));
    }



}
