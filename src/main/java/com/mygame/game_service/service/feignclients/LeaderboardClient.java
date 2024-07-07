package com.mygame.game_service.service.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "leaderboard-service", url = "${leaderboardBaseUrl}")
public interface LeaderboardClient {
    @GetMapping("/leaderboard/v1/score/{gameId}")
    Object getLeaderboard(@PathVariable String gameId, @RequestParam("k") int k);
}
