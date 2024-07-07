package com.mygame.game_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygame.game_service.dto.PlayerScoreDto;
import com.mygame.game_service.dto.ResponseDto;
import com.mygame.game_service.exception.ValidationException;
import com.mygame.game_service.service.PlayerScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static com.mygame.game_service.constant.GameConstants.SUCCESS;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerScoreService playerScoreService;

    @InjectMocks
    private GameController gameController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testPostGameScore() throws Exception {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto("player1", "1", 100, new Date());

        doNothing().when(playerScoreService).pushEvent(any(PlayerScoreDto.class));

        String json = objectMapper.writeValueAsString(playerScoreDto);

        mockMvc.perform(post("/game/v1/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is(SUCCESS)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void testPostGameScoreWithNullUsername() throws Exception {
        PlayerScoreDto playerScoreDto = new PlayerScoreDto(null, "1", 100, new Date());

        doThrow(new ValidationException("Username or score or gameId cannot be null")).when(playerScoreService).pushEvent(any(PlayerScoreDto.class));

        String json = objectMapper.writeValueAsString(playerScoreDto);

        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/game/v1/score")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                    .andExpect(jsonPath("$.message", is("Username or score or gameId cannot be null")))
                    .andReturn();
        });
    }

    @Test
    public void testGetTopKScore() throws Exception {
        List<PlayerScoreDto> topKScores = List.of(
                new PlayerScoreDto("player1", "1", 100, new Date()),
                new PlayerScoreDto("player2", "1", 95, new Date())
        );

        ResponseDto responseDto = new ResponseDto(HttpStatus.OK, SUCCESS, topKScores);

        when(playerScoreService.getTopKScores(anyString(), anyInt())).thenReturn(responseDto);

        mockMvc.perform(get("/game/v1/score/game123")
                        .param("k", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(SUCCESS)))
                .andExpect(jsonPath("$.data[0].userName", is("player1")))
                .andExpect(jsonPath("$.data[0].score", is(100)))
                .andExpect(jsonPath("$.data[1].userName", is("player2")))
                .andExpect(jsonPath("$.data[1].score", is(95)));
    }
}