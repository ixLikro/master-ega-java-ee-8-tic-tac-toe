package de.salty.software.entity;

import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Entity
public class GameDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private GameState status;

    @ManyToOne
    private PlayerDTO player1;

    @ManyToOne
    private PlayerDTO player2;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)

    private List<MoveDTO> allMoveDTOS;


    public GameDTO() {
    }

    public GameDTO(GameState status, PlayerDTO player1, PlayerDTO player2, List<MoveDTO> allMoveDTOS) {
        this.status = status;
        this.player1 = player1;
        this.player2 = player2;
        this.allMoveDTOS = allMoveDTOS;
    }

    public Long getId() {
        return id;
    }

    public PlayerDTO getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerDTO player1) {
        this.player1 = player1;
    }

    public PlayerDTO getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerDTO player2) {
        this.player2 = player2;
    }

    public List<MoveDTO> getAllMoveDTOS() {
        return allMoveDTOS;
    }

    public void setAllMoveDTOS(List<MoveDTO> allMoveDTOS) {
        this.allMoveDTOS = allMoveDTOS;
    }

    public GameState getStatus() {
        return status;
    }

    public void setStatus(GameState status) {
        this.status = status;
    }
}
