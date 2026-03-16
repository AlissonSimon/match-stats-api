package com.springboot.match.stats.controllers;

import com.springboot.match.stats.dtos.map.GameMapRequestDTO;
import com.springboot.match.stats.dtos.map.GameMapResponseDTO;
import com.springboot.match.stats.dtos.map.GameMapStatusRequestDTO;
import com.springboot.match.stats.services.GameMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/maps")
@RequiredArgsConstructor
public class GameMapController {
    private final GameMapService service;

    @GetMapping
    public ResponseEntity<List<GameMapResponseDTO>> findAll() {
        List<GameMapResponseDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GameMapResponseDTO> findById(@PathVariable Long id) {
        GameMapResponseDTO obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<GameMapResponseDTO> insert(@Valid @RequestBody GameMapRequestDTO objDto) {
        GameMapResponseDTO response = service.insert(objDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<GameMapResponseDTO> update(@PathVariable Long id, @Valid @RequestBody GameMapStatusRequestDTO objDto) {
        GameMapResponseDTO response = service.update(id, objDto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
