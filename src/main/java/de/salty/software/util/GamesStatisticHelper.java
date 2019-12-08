package de.salty.software.util;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.PlayerDTO;
import de.salty.software.model.GamesStatistic;
import de.salty.software.persistence.StorageJPA;

import java.util.List;

public class GamesStatisticHelper {


    public static GamesStatistic getStatisticsFromPlayer(StorageJPA storage, PlayerDTO fromPLayer){
        List<GameDTO> games = storage.getAllGamesFromPlayer(fromPLayer);
        int gameCount = 0;
        int gameCountWin = 0;

        for (GameDTO game : games){
            if(game.getStatus() == GameState.PLAYER_1_WON || game.getStatus() == GameState.PLAYER_2_WON || game.getStatus() == GameState.DRAW){
                gameCount++;
                boolean isPlayer1 = game.getPlayer1().equals(fromPLayer);

                if(game.getStatus() == GameState.PLAYER_1_WON && isPlayer1){
                    gameCountWin++;
                }
                if(game.getStatus() == GameState.PLAYER_2_WON && !isPlayer1){
                    gameCountWin++;
                }
            }
        }

        return new GamesStatistic(gameCount, gameCountWin);
    }
}
