package de.salty.software.ui;

import de.salty.software.entity.PlayerDTO;
import de.salty.software.persistence.StorageJPA;
import de.salty.software.util.GamesStatisticHelper;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named()
@SessionScoped
public class UiModelIndex implements Serializable {

    @Inject
    private StorageJPA storage;


    public String continueWithName(String name){
        PlayerDTO player = storage.getOrCreatePlayer(name);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("name", player);
        return "/secured/landingpage.xhtml?faces-redirect=true";
    }
}
