package de.salty.software.ui;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.PlayerDTO;
import de.salty.software.model.Game;
import de.salty.software.persistence.StorageJPA;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
public class UiModelHistory {

    @Inject
    private StorageJPA storage;

    public List<Game> getMyGames(){
        PlayerDTO player = (PlayerDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("name");
        List<GameDTO> gameDTOs = storage.getAllGamesFromPlayer(player);

        List<Game> ret = new ArrayList<>();
        gameDTOs.forEach(gameDTO -> ret.add(new Game(gameDTO)));
        return ret;
    }

    public boolean amIPlayer1(Game game){
        return game.getPlayer1().equals(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name"));
    }

    public boolean amIPlayer2(Game game){
        if(game.getPlayer2() != null){
            return game.getPlayer2().equals(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name"));
        }
        return false;
    }
}
