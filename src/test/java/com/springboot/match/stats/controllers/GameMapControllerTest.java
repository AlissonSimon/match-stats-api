package com.springboot.match.stats.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.match.stats.dtos.map.GameMapRequestDTO;
import com.springboot.match.stats.dtos.map.GameMapResponseDTO;
import com.springboot.match.stats.dtos.map.GameMapStatusRequestDTO;
import com.springboot.match.stats.services.GameMapService;
import com.springboot.match.stats.services.exceptions.MapAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import com.springboot.match.stats.services.exceptions.StatusAlreadySetException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameMapController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameMapControllerTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final String NAME_EXISTENT = "Train";
    private static final boolean ACTIVE_MAP = true;

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GameMapService service;
    private ObjectMapper objectMapper;
    private GameMapRequestDTO requestDTO;
    private GameMapStatusRequestDTO requestStatusDTO;
    private GameMapResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        requestDTO = new GameMapRequestDTO(
                NAME_EXISTENT,
                ACTIVE_MAP
        );

        requestStatusDTO = new GameMapStatusRequestDTO(
                ACTIVE_MAP
        );

        responseDTO = new GameMapResponseDTO(
                ID_EXISTENT,
                NAME_EXISTENT,
                ACTIVE_MAP,
                null,
                null
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /maps --->> Should find all return status 200 OK and map list")
    void should_find_all_maps_and_return_ok_and_map_list() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/maps")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID_EXISTENT))
                .andExpect(jsonPath("$[0].name").value(NAME_EXISTENT))
                .andExpect(jsonPath("$[0].active").value(ACTIVE_MAP));
    }

    @Test
    @Order(1)
    @DisplayName("GET /maps/{id} --->> Should find by id and return status 200 OK and map when id exists")
    void should_find_map_by_id_and_returns_ok_and_map() throws Exception {
        Mockito.when(service.findById(ID_EXISTENT)).thenReturn(responseDTO);

        mockMvc.perform(get("/maps/{id}", ID_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT));
    }

    @Test
    @Order(1)
    @DisplayName("GET /maps/{id} --->> Should return 404 NOT FOUND when id is not found")
    void should_throw_exception_when_id_is_not_found() throws Exception {
        Mockito.when(service.findById(ID_NON_EXISTENT)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/maps/{id}", ID_NON_EXISTENT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    @DisplayName("POST /maps --->> Should insert and return status 201 CREATED and maps")
    void should_insert_map_with_valid_data_and_returns_created() throws Exception {
        Mockito.when(service.insert(any(GameMapRequestDTO.class))).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/maps")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT))
                .andExpect(jsonPath("$.name").value(NAME_EXISTENT))
                .andExpect(jsonPath("$.active").value(ACTIVE_MAP));
    }

    @Test
    @Order(2)
    @DisplayName("POST /maps --->> Should return 409 CONFLICT when name already exists")
    void should_throw_exception_when_map_name_already_exists() throws Exception {
        GameMapRequestDTO request = new GameMapRequestDTO(NAME_EXISTENT, ACTIVE_MAP);

        Mockito.doThrow(new MapAlreadyExistsException()).when(service).insert(any());

        mockMvc.perform(post("/maps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("PATCH /maps/{id} --->> Should update and return status 200 OK and the updated map")
    void should_update_with_map_status_and_returns_ok_and_updated_map() throws Exception {
        Mockito.when(service.update(eq(ID_EXISTENT), any())).thenReturn(responseDTO);

        String jsonBody = objectMapper.writeValueAsString(requestStatusDTO);

        mockMvc.perform(patch("/maps/{id}", ID_EXISTENT)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_EXISTENT))
                .andExpect(jsonPath("$.active").value(ACTIVE_MAP));
    }

    @Test
    @Order(3)
    @DisplayName("PATCH /maps/{id} --->> Should return 409 CONFLICT when map status is already set")
    void should_throw_exception_when_map_status_already_set() throws Exception {
        Mockito.when(service.update(eq(ID_EXISTENT), any())).thenThrow(new StatusAlreadySetException());

        String jsonBody = objectMapper.writeValueAsString(requestStatusDTO);

        mockMvc.perform(patch("/maps/{id}", ID_EXISTENT)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /maps/{id} --->> Should delete and return 204 NO CONTENT when delete is successful")
    void should_delete_map_by_id_and_return_no_content() throws Exception {
        Mockito.doNothing().when(service).delete(ID_EXISTENT);

        mockMvc.perform(delete("/maps/{id}", ID_EXISTENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /maps/{id} --->> Should return 404 NOT FOUND when id does not exist")
    void should_throw_exception_when_id_is_not_found_and_deleted() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException()).when(service).delete(ID_NON_EXISTENT);

        mockMvc.perform(delete("/maps/{id}", ID_NON_EXISTENT))
                .andExpect(status().isNotFound());
    }
}
