package com.springboot.match.stats.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "tb_maps")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
