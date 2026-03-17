package com.springboot.match.stats.controllers;

import com.springboot.match.stats.dtos.stats.PlayerMatchStatsRequestDTO;
import com.springboot.match.stats.dtos.stats.PlayerMatchStatsResponseDTO;
import com.springboot.match.stats.services.PlayerMatchStatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/stats")
@RequiredArgsConstructor
public class PlayerMatchStatsController {
    private final PlayerMatchStatsService service;

    @GetMapping
    public ResponseEntity<List<PlayerMatchStatsResponseDTO>> findAll() {
        List<PlayerMatchStatsResponseDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerMatchStatsResponseDTO> findById(@PathVariable Long id) {
        PlayerMatchStatsResponseDTO obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<PlayerMatchStatsResponseDTO> insert(@Valid @RequestBody PlayerMatchStatsRequestDTO objDto) {
        PlayerMatchStatsResponseDTO response = service.insert(objDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
