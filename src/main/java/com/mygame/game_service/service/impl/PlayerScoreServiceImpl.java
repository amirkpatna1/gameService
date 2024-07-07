package com.mygame.game_service.service.impl;

import com.mygame.game_service.dto.PlayerScoreDto;
import com.mygame.game_service.dto.ResponseDto;
import com.mygame.game_service.exception.GenericException;
import com.mygame.game_service.exception.ValidationException;
import com.mygame.game_service.service.KafkaProducerService;
import com.mygame.game_service.service.PlayerScoreService;
import com.mygame.game_service.service.feignclients.LeaderboardClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.mygame.game_service.constant.GameConstants.KAFKA_PRODUCER_SERVICE_3;
import static com.mygame.game_service.constant.GameConstants.SPACE;
import static com.mygame.game_service.constant.GameConstants.SUCCESS;

@Service
@Slf4j
public class PlayerScoreServiceImpl implements PlayerScoreService {

    @Value("${kafka.score-topic}")
    private String scoreTopic;

    @Value("${limit.max-score}")
    private int maxScore;

    @Autowired
    private LeaderboardClient leaderboardClient;

    @Autowired
    @Qualifier(KAFKA_PRODUCER_SERVICE_3)
    private KafkaProducerService kafkaProducerService;
    @Override
    public void pushEvent(PlayerScoreDto playerScoreDto) {
        log.info("pushEvent: started processing request for pushEvent in topic {} and data {}", scoreTopic, playerScoreDto);
        validateDto(playerScoreDto);
        try {
            playerScoreDto.setCreatedAt(new Date());
            kafkaProducerService.sendMessage(scoreTopic, null, playerScoreDto);
        } catch (Exception exception) {
            throw new GenericException(String.format("Some exception occurred while saving data for %s", playerScoreDto.getUserName()));
        }
    }

    @Override
    public ResponseDto getTopKScores(String gameId, int k) {
        log.info("getTopKScores: started processing request for getting top {} scores for gameId {}", k, gameId);
        validateK(k);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(HttpStatus.OK);
        responseDto.setMessage(SUCCESS);
        responseDto.setData(leaderboardClient.getLeaderboard(gameId, k));
        return responseDto;
    }

    private void validateDto(PlayerScoreDto playerScoreDto) {
        if(Objects.isNull(playerScoreDto.getUserName()) || Objects.isNull(playerScoreDto.getScore()) || Objects.isNull(playerScoreDto.getGameId())) {
            throw new ValidationException("Username or score or gameId cannot be null");
        }
        if(playerScoreDto.getUserName().contains(SPACE)) {
            throw new ValidationException(String.format("Username cannot contain spaces for username %s", playerScoreDto.getUserName()));
        }
        if(playerScoreDto.getGameId().contains(SPACE)) {
            throw new ValidationException(String.format("gameId cannot contain spaces for username %s", playerScoreDto.getUserName()));
        }
        if(playerScoreDto.getScore() > maxScore) {
            throw new ValidationException(String.format("Score cannot be greater than %d for username %s", maxScore, playerScoreDto.getUserName()));
        }
        if(playerScoreDto.getScore() < 0) {
            throw new ValidationException(String.format("Score cannot be lesser than 0 for username %s", playerScoreDto.getUserName()));
        }
    }

    public void validateK(int k) {
        if(k <= 0) {
            throw new ValidationException("Value of k should be greater than 0");
        }
    }
}
