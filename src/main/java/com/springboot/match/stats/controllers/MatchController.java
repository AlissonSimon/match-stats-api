package com.springboot.match.stats.controllers;

import com.springboot.match.stats.dtos.MatchRequestDTO;
import com.springboot.match.stats.dtos.MatchResponseDTO;
import com.springboot.match.stats.services.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService service;

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAll() {
        List<MatchResponseDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MatchResponseDTO> findById(@PathVariable Long id) {
        MatchResponseDTO obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<MatchResponseDTO> insert(@RequestBody MatchRequestDTO objDto) {
        MatchResponseDTO response = service.insert(objDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
