package com.springboot.match.stats.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.match.stats.dtos.stats.PlayerMatchStatsRequestDTO;
import com.springboot.match.stats.dtos.stats.PlayerMatchStatsResponseDTO;
import com.springboot.match.stats.models.enums.ResultType;
import com.springboot.match.stats.services.PlayerMatchStatsService;
import com.springboot.match.stats.services.exceptions.HeadshotsExceedKillsException;
import com.springboot.match.stats.services.exceptions.PlayerAlreadyHasStatsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerMatchStatsController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class PlayerMatchStatsControllerTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PlayerMatchStatsService service;
    private ObjectMapper objectMapper;
    private PlayerMatchStatsRequestDTO requestDTO;
    private PlayerMatchStatsResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        requestDTO = new PlayerMatchStatsRequestDTO(
                ID_EXISTENT,
                ID_EXISTENT,
                ResultType.VICTORY,
                25, 10, 15, 18
        );

        responseDTO = new PlayerMatchStatsResponseDTO(
                ID_EXISTENT,
                ID_EXISTENT,
                ID_EXISTENT,
                ResultType.VICTORY,
                25, 10, 15, 18,
                2.5,
                72.0
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /stats --->> Should find all and return 200 OK and stats list")
    void should_find_all_stats_and_return_ok_and_stats_list() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/stats")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID_EXISTENT))
                .andExpect(jsonPath("$[0].playerId").value(ID_EXISTENT))
                .andExpect(jsonPath("$[0].matchId").value(ID_EXISTENT));
    }

    @Test
    @Order(1)
    @DisplayName("GET /stats/{id} --->> Should find by id and return 200 OK and stats when id exists")
    void should_find_stats_by_id_and_returns_ok_and_stats() throws Exception {
        Mockito.when(service.findById(ID_EXISTENT)).thenReturn(responseDTO);

        mockMvc.perform(get("/stats/{id}", ID_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT));
    }

    @Test
    @Order(1)
    @DisplayName("GET /stats/{id} --->> Should return 404 NOT FOUND when id is not found")
    void should_throw_exception_when_id_is_not_found() throws Exception {
        Mockito.when(service.findById(ID_EXISTENT)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/stats/{id}", ID_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    @DisplayName("POST /stats --->> Should insert and return 201 CREATED and stats")
    void should_insert_stats_with_valid_data_and_returns_created() throws Exception {
        Mockito.when(service.insert(any(PlayerMatchStatsRequestDTO.class))).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/stats")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT))
                .andExpect(jsonPath("$.playerId").value(ID_EXISTENT))
                .andExpect(jsonPath("$.matchId").value(ID_EXISTENT));
    }

    @Test
    @Order(2)
    @DisplayName("POST /stats --->> Should return 404 NOT FOUND when player id and match id is not found")
    void should_throw_exception_when_player_and_match_dont_exist() throws Exception {
        Mockito.when(service.insert(any(PlayerMatchStatsRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException());

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/stats")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(service, Mockito.times(1)).insert(any(PlayerMatchStatsRequestDTO.class));
    }

    @Test
    @Order(2)
    @DisplayName("POST /stats --->> Should return 409 CONFLICT when player already has stats")
    void should_throw_exception_when_player_already_has_stats_in_match() throws Exception {
        Mockito.when(service.insert(any(PlayerMatchStatsRequestDTO.class)))
                .thenThrow(new PlayerAlreadyHasStatsException());

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/stats")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        Mockito.verify(service, Mockito.times(1)).insert(any(PlayerMatchStatsRequestDTO.class));
    }

    @Test
    @Order(2)
    @DisplayName("POST /stats --->> Should return 422 UNPROCESSABLE CONTENT when headshots exceeds kills number")
    void should_throw_exception_when_headshots_exceeds_kills_number() throws Exception {
        Mockito.when(service.insert(any(PlayerMatchStatsRequestDTO.class)))
                .thenThrow(new HeadshotsExceedKillsException());

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/stats")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableContent());

        Mockito.verify(service, Mockito.times(1)).insert(any(PlayerMatchStatsRequestDTO.class));
    }

    @Test
    @Order(3)
    @DisplayName("DELETE /stats/{id} --->> Should delete and return 204 NO CONTENT when delete is successful")
    void should_delete_stats_by_id_and_return_no_content() throws Exception {
        Mockito.doNothing().when(service).delete(ID_EXISTENT);

        mockMvc.perform(delete("/stats/{id}", ID_EXISTENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(3)
    @DisplayName("DELETE /stats/{id} --->> Should return 404 NOT FOUND when id does not exist")
    void should_throw_exception_when_id_is_not_found_and_deleted() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException()).when(service).delete(ID_NON_EXISTENT);

        mockMvc.perform(delete("/stats/{id}", ID_NON_EXISTENT))
                .andExpect(status().isNotFound());
    }
}
