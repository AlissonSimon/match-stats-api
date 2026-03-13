package com.springboot.match.stats.controllers;

import com.springboot.match.stats.dtos.player.PlayerRequestDTO;
import com.springboot.match.stats.dtos.player.PlayerResponseDTO;
import com.springboot.match.stats.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService service;

    @GetMapping
    public ResponseEntity<List<PlayerResponseDTO>> findAll() {
        List<PlayerResponseDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerResponseDTO> findById(@PathVariable Long id) {
        PlayerResponseDTO obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<PlayerResponseDTO> insert(@RequestBody PlayerRequestDTO objDto) {
        PlayerResponseDTO response = service.insert(objDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PlayerResponseDTO> update(@PathVariable Long id, @RequestBody PlayerRequestDTO objDto) {
        PlayerResponseDTO response = service.update(id, objDto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
