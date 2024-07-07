package com.mygame.game_service.controller;


import com.mygame.game_service.dto.PlayerScoreDto;
import com.mygame.game_service.dto.ResponseDto;
import com.mygame.game_service.service.PlayerScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mygame.game_service.constant.GameConstants.SUCCESS;

@RestController
@RequestMapping("/game/v1")
public class GameController {

    @Autowired
    private PlayerScoreService playerScoreService;

    @PostMapping("/score")
    public ResponseEntity<Object> postGameScore(@RequestBody PlayerScoreDto playerScoreDto) {
        playerScoreService.pushEvent(playerScoreDto);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK, SUCCESS, null));
    }

    @GetMapping("/score/{gameId}")
    public ResponseEntity<Object> getTopKScore(@PathVariable String gameId, @RequestParam("k") int k) {
        return ResponseEntity.ok(playerScoreService.getTopKScores(gameId, k));
    }
}
