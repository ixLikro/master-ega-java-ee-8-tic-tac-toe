package de.salty.software.persistence;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.PlayerDTO;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class StorageJPA {

    @PersistenceContext
    private EntityManager manager;

    private static final Logger log = Logger.getAnonymousLogger();

    public StorageJPA() {
    }

    private PlayerDTO getPlayerByName(String name){
        TypedQuery q = manager.createQuery("SELECT p FROM PlayerDTO p where p.name='" + name+"'", PlayerDTO.class);

        List results = q.getResultList();

        if(results.isEmpty()){
            return null;
        }else {
            return (PlayerDTO) results.get(0);
        }
    }

    public List<GameDTO> getAllGames(){
        TypedQuery<GameDTO> q = manager.createQuery("SELECT p FROM GameDTO p", GameDTO.class);
        return q.getResultList();
    }

    public List<GameDTO> getAllGamesFromPlayer(PlayerDTO player){
        TypedQuery<GameDTO> q = manager.createQuery("SELECT g FROM GameDTO g WHERE g.player1.id="+player.getId()+" or g.player2.id="+player.getId(), GameDTO.class);
        return q.getResultList();
    }

    public List<GameDTO> getAllWaitingGames(){
        TypedQuery<GameDTO> q = manager.createQuery("SELECT g FROM GameDTO g WHERE g.status='"+ GameState.WAITING_FOR_PLAYER.ordinal()+"'", GameDTO.class);
        return q.getResultList();
    }

    public GameDTO getGame(long id){
        TypedQuery q = manager.createQuery("SELECT g FROM GameDTO g where g.id=" + id, GameDTO.class);

        List results = q.getResultList();

        if(results.isEmpty()){
            return null;
        }else {
            return (GameDTO) results.get(0);
        }
    }

    public void deleteGame(GameDTO gameDTO){
        if(!manager.contains(gameDTO)) gameDTO = manager.merge(gameDTO);
        manager.remove(gameDTO);

        log.log(Level.INFO, "{0}\n\twas successfully deleted!", gameDTO.toString());
    }

    public GameDTO persistGame(GameDTO gameDTO){
        if(!manager.contains(gameDTO)) gameDTO = manager.merge(gameDTO);
        manager.persist(gameDTO);

        log.log(Level.INFO, "{0}\n\twas successfully saved!", gameDTO.toString());
        return gameDTO;
    }

    public GameDTO updateGame(GameDTO gameToUpdate){
        return manager.merge(gameToUpdate);
    }


    public PlayerDTO persistPLayer(PlayerDTO player){
        manager.persist(player);

        log.log(Level.INFO, "{0}\n\twas successfully saved!", player.toString());
        return player;
    }

    public PlayerDTO createPLayer(String name){
        PlayerDTO newPLayer = new PlayerDTO(name);
        return persistPLayer(newPLayer);
    }

    public PlayerDTO getOrCreatePlayer(String name){
        PlayerDTO foundPlayer = getPlayerByName(name);

        if(foundPlayer != null){
            return foundPlayer;
        }else {
            return createPLayer(name);
        }
    }

}
