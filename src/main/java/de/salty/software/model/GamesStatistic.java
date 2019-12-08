package de.salty.software.model;

import javax.inject.Named;
import java.io.Serializable;

@Named
public class GamesStatistic implements Serializable{

    private int gameCount;
    private int winCount;

    public GamesStatistic() {
    }

    public GamesStatistic(int gameCount, int winCount) {
        this.gameCount = gameCount;
        this.winCount = winCount;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
}
