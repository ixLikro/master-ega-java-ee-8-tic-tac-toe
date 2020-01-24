package de.salty.software.model;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.MoveDTO;

import java.util.List;

/**
 * represent a tic tac toe field
 * --------
 * 00|01|02
 * 10|11|12
 * 20|21|22
 * --------
 */
public class Field {

    private Cell[][] cells;

    public Field() {
        initCells();
    }

    public Field(List<MoveDTO> allMoves, GameDTO game){
        initCells();
        if(allMoves != null){
            allMoves.forEach(move -> {
                makeMove(move.getX(),move.getY(), move.getDidMove().equals(game.getPlayer1()));
            });
        }
    }

    public void makeMove(int x, int y, boolean wasPlayer1){
        cells[x][y].setCellState(wasPlayer1 ? CellState.PLAYER_1 : CellState.PLAYER_2);
    }

    private void initCells(){
        cells = new Cell[3][3];

        for(int x = 0; x < cells.length; x++){
            for(int y = 0; y < cells.length; y++){
                cells[x][y] = new Cell(x,y);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }
}
