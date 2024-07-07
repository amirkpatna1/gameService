package com.mygame.game_service.service.impl;

import org.junit.jupiter.api.Test;

import static com.mygame.game_service.constant.GameConstants.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

import com.mygame.game_service.dto.PlayerScoreDto;
import com.mygame.game_service.dto.ResponseDto;
import com.mygame.game_service.exception.GenericException;
import com.mygame.game_service.exception.ValidationException;
import com.mygame.game_service.service.KafkaProducerService;
import com.mygame.game_service.service.feignclients.LeaderboardClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlayerScoreServiceImplTest {

    @InjectMocks
    private PlayerScoreServiceImpl playerScoreService;

    @Mock
    private LeaderboardClient leaderboardClient;

    @Mock
    private KafkaProducerService kafkaProducerService;
    private String topic;
    private int maxScore = 1000;

    @BeforeEach
    public void setUp() {
        topic = "TOPIC";
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(playerScoreService, "scoreTopic", topic);
        ReflectionTestUtils.setField(playerScoreService, "maxScore", maxScore);

    }

    @Test
    public void testPushEventSuccess() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", 100, new Date());
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any(), any(PlayerScoreDto.class));

        assertDoesNotThrow(() -> playerScoreService.pushEvent(playerScoreDto));
        verify(kafkaProducerService, times(1)).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionNullUsername() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto(null, "1", 100, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Username or score or gameId cannot be null", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionNullScore() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", null, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Username or score or gameId cannot be null", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionNullGameId() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", null, 100, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Username or score or gameId cannot be null", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionUsernameWithSpaces() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player 1", "1", 100, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Username cannot contain spaces for username player 1", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionGameIdWithSpaces() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "game 1", 100, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("gameId cannot contain spaces for username player1", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionScoreGreaterThanMax() {

        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", 1001, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals(String.format("Score cannot be greater than %d for username %s", maxScore, "player1"), exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventValidationExceptionScoreLessThanZero() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", -1, new Date());

        ValidationException exception = assertThrows(ValidationException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Score cannot be lesser than 0 for username player1", exception.getMessage());
        verify(kafkaProducerService, never()).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testPushEventGenericException() {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", 100, new Date());
        doThrow(new RuntimeException("Kafka exception")).when(kafkaProducerService).sendMessage(anyString(), any(), any(PlayerScoreDto.class));

        GenericException exception = assertThrows(GenericException.class, () -> playerScoreService.pushEvent(playerScoreDto));
        assertEquals("Some exception occurred while saving data for player1", exception.getMessage());
        verify(kafkaProducerService, times(1)).sendMessage(anyString(), any(), any(PlayerScoreDto.class));
    }

    @Test
    public void testGetTopKScores() {
        String gameId = "1";
        int k = 3;
        List<PlayerScoreDto> scores = List.of(
                new PlayerScoreDto("player1", gameId, 100, new Date()),
                new PlayerScoreDto("player2", gameId, 95, new Date())
        );
        when(leaderboardClient.getLeaderboard(anyString(), anyInt())).thenReturn(scores);

        ResponseDto responseDto = playerScoreService.getTopKScores(gameId, k);

        assertEquals(HttpStatus.OK, responseDto.getStatus());
        assertEquals(SUCCESS, responseDto.getMessage());
        assertEquals(scores, responseDto.getData());
        verify(leaderboardClient, times(1)).getLeaderboard(anyString(), anyInt());
    }

    @Test
    public void testGetTopKScoresWithNegativeK() {
        String gameId = "1";
        int k = -1;
        List<PlayerScoreDto> scores = List.of(
                new PlayerScoreDto("player1", gameId, 100, new Date()),
                new PlayerScoreDto("player2", gameId, 95, new Date())
        );
        when(leaderboardClient.getLeaderboard(anyString(), anyInt())).thenReturn(scores);

        ValidationException validationException = assertThrows(ValidationException.class, () -> playerScoreService.getTopKScores(gameId, k));
        assertEquals("Value of k should be greater than 0", validationException.getMessage());
        verify(leaderboardClient, times(0)).getLeaderboard(anyString(), anyInt());
    }
}