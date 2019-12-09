package de.salty.software.ui;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.PlayerDTO;
import de.salty.software.model.Game;
import de.salty.software.persistence.StorageJPA;
import de.salty.software.util.GamesStatisticHelper;
import de.salty.software.util.RandomHelper;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
public class UiModelLandingPage implements Serializable {

    @Inject
    private StorageJPA storage;

    @Inject @Push
    private PushContext updateGame;

    @Inject @Push
    private PushContext waitingGame;

    public void before() {
        //if in-game redirect to the game
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("game") != null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("game.xhtml?faces-redirect=true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //update statistics
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("statistic", GamesStatisticHelper.getStatisticsFromPlayer(storage, (PlayerDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                        .get("name")));
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/app/index?faces-redirect=true";
    }

    public List<GameDTO> getWaitingGames() {
        return storage.getAllWaitingGames();
    }


    public String createNewGame() {

        GameDTO gameDTO = new GameDTO();
        gameDTO.setPlayer1((PlayerDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("name"));
        gameDTO.setStatus(GameState.WAITING_FOR_PLAYER);
        gameDTO = storage.persistGame(gameDTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("game", new Game(gameDTO));

        waitingGame.send("updateNow");

        return "game?faces-redirect=true";
    }

    public String joinGame(GameDTO gameDTO) {

        //get fresh game
        GameDTO game = storage.getGame(gameDTO.getId());
        //set player 2 as this player
        game.setPlayer2((PlayerDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("name"));
        //chose random start player
        if (RandomHelper.hitPercentChance(50)) {
            game.setStatus(GameState.PLAYER_1_TURN);
        } else {
            game.setStatus(GameState.PLAYER_2_TURN);
        }
        //update game and set it in the session
        game = storage.updateGame(game);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("game", new Game(game));

        //push an update
        updateGame.send(game.getId());

        waitingGame.send("updateNow");

        return "game?faces-redirect=true";
    }
}
