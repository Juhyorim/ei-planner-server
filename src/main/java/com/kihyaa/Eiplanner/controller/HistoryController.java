package com.kihyaa.Eiplanner.controller;

import com.kihyaa.Eiplanner.dto.request.GetHistoryRequest;
import com.kihyaa.Eiplanner.dto.response.GetHistoryResponse;
import com.kihyaa.Eiplanner.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<GetHistoryResponse> getHistory(@RequestBody @Valid GetHistoryRequest request,
                                                        @PageableDefault(page=0, size=5) Pageable pageable){

        GetHistoryResponse historyList = historyService.getHistory(request.getUser_pk(), pageable);

        return ResponseEntity.ok(historyList);
    }
}
