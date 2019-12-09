package de.salty.software.entity;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;

@Entity
public class MoveDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PlayerDTO didMove;

    private int x;
    private int y;

    public MoveDTO() {
    }

    public MoveDTO(PlayerDTO didMove, int x, int y) {
        this.didMove = didMove;
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public PlayerDTO getDidMove() {
        return didMove;
    }

    public void setDidMove(PlayerDTO didMove) {
        this.didMove = didMove;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
