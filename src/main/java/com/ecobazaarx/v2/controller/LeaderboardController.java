package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.LeaderboardEntryDto;
import com.ecobazaarx.v2.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntryDto>> getGlobalLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboardByLevel(
            @PathVariable int level
    ) {
        return ResponseEntity.ok(leaderboardService.getLeaderboardByLevel(level));
    }
}
