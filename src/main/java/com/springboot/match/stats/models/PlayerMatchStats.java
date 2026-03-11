package com.springboot.match.stats.models;

import com.springboot.match.stats.models.enums.ResultType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_player_match_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerMatchStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player playerId;
    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match matchId;
    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private ResultType resultType;
    @Column(name = "kills")
    private Integer kills;
    @Column(name = "deaths")
    private Integer deaths;
    @Column(name = "assists")
    private Integer assists;
    @Column(name = "headshots")
    private Integer headshots;
}
