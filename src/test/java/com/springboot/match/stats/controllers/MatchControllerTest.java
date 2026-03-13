package com.springboot.match.stats.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.match.stats.dtos.MatchRequestDTO;
import com.springboot.match.stats.dtos.MatchResponseDTO;
import com.springboot.match.stats.services.MatchService;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchControllerTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final String MAP_NAME = "Train";

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    private MatchService service;
    private ObjectMapper objectMapper;
    private MatchRequestDTO requestDTO;
    private MatchResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        requestDTO = new MatchRequestDTO(
                MAP_NAME
        );

        responseDTO = new MatchResponseDTO(
                ID_EXISTENT,
                MAP_NAME,
                null
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /matches --->> Should find all and return status 200 OK and matches list")
    void should_find_all_matches_and_return_ok_and_match_list() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/matches")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID_EXISTENT))
                .andExpect(jsonPath("$[0].mapName").value(MAP_NAME));
    }

    @Test
    @Order(1)
    @DisplayName("GET /matches/{id} --->> Should find by id and return status 200 OK and match when id exists")
    void should_find_matches_by_id_and_returns_ok_and_match() throws Exception {
        Mockito.when(service.findById(ID_EXISTENT)).thenReturn(responseDTO);

        mockMvc.perform(get("/matches/{id}", ID_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT));
    }

    @Test
    @Order(1)
    @DisplayName("GET /matches/{id} --->> Should return 404 NOT FOUND when id is not found")
    void should_throw_exception_when_id_is_not_found() throws Exception {
        Mockito.when(service.findById(ID_NON_EXISTENT)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/matches/{id}", ID_NON_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    @DisplayName("POST /matches --->> Should insert and return status 201 CREATED and match")
    void should_insert_match_with_valid_data_and_returns_created() throws Exception {
        Mockito.when(service.insert(any(MatchRequestDTO.class))).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/matches")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT))
                .andExpect(jsonPath("$.mapName").value(MAP_NAME));
    }
}
