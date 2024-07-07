package com.mygame.game_service.service;

import com.mygame.game_service.dto.PlayerScoreDto;
import com.mygame.game_service.dto.ResponseDto;

public interface PlayerScoreService {
    void pushEvent(PlayerScoreDto playerScoreDto);
    ResponseDto getTopKScores(String gameId, int k);
}
