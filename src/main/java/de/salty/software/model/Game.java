package de.salty.software.model;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.PlayerDTO;

import java.io.Serializable;

public class Game implements Serializable {

    private Long id;
    private PlayerDTO player1;
    private PlayerDTO player2;

    private Field field;
    private GameState state;

    public Game(GameDTO gameDTO) {
        player1 = gameDTO.getPlayer1();
        player2 = gameDTO.getPlayer2();
        id = gameDTO.getId();
        state = gameDTO.getStatus();

        field = new Field(gameDTO.getAllMoveDTOS(),gameDTO);
    }

    public PlayerDTO getPlayer1() {
        return player1;
    }

    public PlayerDTO getPlayer2() {
        return player2;
    }

    public Field getField() {
        return field;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }
}
