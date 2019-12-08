package de.salty.software.ui;

import de.salty.software.model.CellState;
import de.salty.software.model.Game;
import de.salty.software.persistence.StorageJPA;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class UiModelCell {

    @Inject
    private UiModelGame uiModelGame;
    @Inject
    private StorageJPA storageJPA;

    public CellState getCellState(int x, int y){
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        return game.getField().getCells()[x][y].getCellState();
    }
}
