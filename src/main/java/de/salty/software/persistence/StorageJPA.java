package de.salty.software.persistence;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.PlayerDTO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named
public class StorageJPA {

    private EntityManagerFactory factory;
    private EntityManager manager;

    private static final Logger log = Logger.getAnonymousLogger();

    public StorageJPA() {
        factory = Persistence.createEntityManagerFactory("storage1");
        manager = factory.createEntityManager();
    }

    private PlayerDTO getPlayerByName(String name){
        Query q = manager.createQuery("SELECT p FROM PlayerDTO p where p.name='" + name+"'");

        List results = q.getResultList();

        if(results.isEmpty()){
            return null;
        }else {
            return (PlayerDTO) results.get(0);
        }
    }

    public List<GameDTO> getAllGames(){
        Query q = manager.createQuery("SELECT p FROM GameDTO p");
        return q.getResultList();
    }

    public List<GameDTO> getAllGamesFromPlayer(PlayerDTO player){
        Query q = manager.createQuery("SELECT g FROM GameDTO g WHERE g.player1.id="+player.getId()+" or g.player2.id="+player.getId());
        return q.getResultList();
    }

    public List<GameDTO> getAllWaitingGames(){
        Query q = manager.createQuery("SELECT g FROM GameDTO g WHERE g.status='"+ GameState.WAITING_FOR_PLAYER.ordinal()+"'");
        return q.getResultList();
    }

    public GameDTO getGame(long id){
        Query q = manager.createQuery("SELECT g FROM GameDTO g where g.id=" + id);

        List results = q.getResultList();

        if(results.isEmpty()){
            return null;
        }else {
            return (GameDTO) results.get(0);
        }
    }

    public void deleteGame(GameDTO gameDTO){
        manager.getTransaction().begin();
        manager.remove(gameDTO);
        manager.getTransaction().commit();

        log.log(Level.INFO, "{0}\n\twas successfully deleted!", gameDTO.toString());
    }

    public GameDTO persistGame(GameDTO gameDTO){
        manager.getTransaction().begin();
        manager.persist(gameDTO);
        manager.getTransaction().commit();

        log.log(Level.INFO, "{0}\n\twas successfully saved!", gameDTO.toString());
        return gameDTO;
    }

    public GameDTO updateGame(GameDTO gameToUpdate){
        manager.getTransaction().begin();
        GameDTO gameDTO = manager.merge(gameToUpdate);
        manager.getTransaction().commit();

        return gameDTO;
    }


    public PlayerDTO persistPLayer(PlayerDTO player){
        manager.getTransaction().begin();
        manager.persist(player);
        manager.getTransaction().commit();

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
