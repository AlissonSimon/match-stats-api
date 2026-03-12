package com.springboot.match.stats.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springboot.match.stats.dtos.PlayerRequestDTO;
import com.springboot.match.stats.dtos.PlayerResponseDTO;
import com.springboot.match.stats.services.exceptions.NicknameAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import com.springboot.match.stats.services.PlayerService;
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

@WebMvcTest(PlayerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerControllerTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final String NICKNAME = "Zigueira";
    private static final int DEFAULT_ELO = 1000;

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PlayerService service;
    private ObjectMapper objectMapper;
    private PlayerRequestDTO requestDTO;
    private PlayerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        requestDTO = new PlayerRequestDTO(
                NICKNAME
        );

        responseDTO = new PlayerResponseDTO(
                ID_EXISTENT,
                NICKNAME,
                DEFAULT_ELO,
                null,
                null
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /players --->> Should find all return status 200 OK and players list")
    void should_find_all_players_and_return_ok_and_player_list() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/players")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID_EXISTENT))
                .andExpect(jsonPath("$[0].nickname").value(NICKNAME))
                .andExpect(jsonPath("$[0].elo").value(DEFAULT_ELO));
    }

    @Test
    @Order(1)
    @DisplayName("GET /players/{id} --->> Should find by id and return status 200 OK and player when id exists")
    void should_find_player_by_id_and_returns_ok_and_player() throws Exception {
        Mockito.when(service.findById(ID_EXISTENT)).thenReturn(responseDTO);

        mockMvc.perform(get("/players/{id}", ID_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT));
    }

    @Test
    @Order(1)
    @DisplayName("GET /players/{id} --->> Should return 404 NOT FOUND when id is not found")
    void should_throw_exception_when_id_is_not_found() throws Exception {
        Mockito.when(service.findById(ID_NON_EXISTENT)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/players/{id}", ID_NON_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    @DisplayName("POST /players --->> Should insert and return status 201 CREATED and player")
    void should_insert_player_with_valid_data_and_returns_created() throws Exception {
        Mockito.when(service.insert(any(PlayerRequestDTO.class))).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/players")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT))
                .andExpect(jsonPath("$.nickname").value(NICKNAME))
                .andExpect(jsonPath("$.elo").value(DEFAULT_ELO));
    }

    @Test
    @Order(2)
    @DisplayName("POST /players --->> Should return 409 CONFLICT when nickname already exists")
    void should_throw_exception_when_nickname_already_exists() throws Exception {
        PlayerRequestDTO request = new PlayerRequestDTO(NICKNAME);

        Mockito.doThrow(new NicknameAlreadyExistsException()).when(service).insert(any());

        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("PUT /players/{id} --->> Should update and return status 200 OK and the updated player")
    void should_update_with_valid_data_and_returns_ok_and_updated_player() throws Exception {
        Mockito.when(service.update(eq(ID_EXISTENT), any())).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(put("/players/{id}", ID_EXISTENT)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT));
    }

    @Test
    @Order(3)
    @DisplayName("PUT /players/{id} --->> Should return 409 CONFLICT when nickname already exists")
    void should_throw_exception_when_nickname_already_exists_updating_player() throws Exception {
        PlayerRequestDTO request = new PlayerRequestDTO(NICKNAME);

        Mockito.doThrow(new NicknameAlreadyExistsException()).when(service).update(eq(ID_EXISTENT), any());

        mockMvc.perform(put("/players/{id}", ID_EXISTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /players/{id} --->> Should delete and return 204 NO CONTENT when delete is successful")
    void should_delete_player_by_id_and_return_no_content() throws Exception {
        Mockito.doNothing().when(service).delete(ID_EXISTENT);

        mockMvc.perform(delete("/players/{id}", ID_EXISTENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /players --->> Should delete all players and return 204 NO CONTENT")
    void should_delete_all_players_and_return_no_content() throws Exception {
        Mockito.doNothing().when(service).deleteAll();

        mockMvc.perform(delete("/players"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /players/{id} --->> Should return 404 NOT FOUND when id does not exist")
    void should_throw_exception_when_id_is_not_found_and_deleted() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException()).when(service).delete(ID_NON_EXISTENT);

        mockMvc.perform(delete("/players/{id}", ID_NON_EXISTENT))
                .andExpect(status().isNotFound());
    }
}
