package de.salty.software.ui;

import de.salty.software.entity.GameDTO;
import de.salty.software.entity.GameState;
import de.salty.software.entity.MoveDTO;
import de.salty.software.entity.PlayerDTO;
import de.salty.software.model.Cell;
import de.salty.software.model.CellState;
import de.salty.software.model.Game;
import de.salty.software.persistence.StorageJPA;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
public class UiModelGame {

    @Inject
    private StorageJPA storage;
    @Inject @Push
    private PushContext updateGame;

    private static final Logger log = Logger.getAnonymousLogger();

    public void before() {
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("game");
        if(game != null){
            updateGameFromDBInSession();

        }else {
            //not in-game?! redirect landingpage
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("landingpage.xhtml?faces-redirect=true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String updateGameFromDBInSession(){

        Game old = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("game");

        //get fresh game
        GameDTO newGame = storage.getGame(old.getId());

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("game", new Game(newGame));

        return "/secured/game.xhtml";
    }

    public boolean amIPlayer1(){
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        return game.getPlayer1().equals(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name"));
    }

    public boolean amIPlayer2(){
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        if(game.getPlayer2() != null){
            return game.getPlayer2().equals(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name"));
        }
        return false;
    }

    public boolean canIMakeAMove(){
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        if(isGameRunning()){
            //check if its my turn
            if(game.getState() == GameState.PLAYER_1_TURN && amIPlayer1()) return true;
            return game.getState() == GameState.PLAYER_2_TURN && amIPlayer2();
        }
        return false;
    }

    public boolean isGameRunning(){
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        return game.getState() == GameState.PLAYER_1_TURN || game.getState() == GameState.PLAYER_2_TURN;
    }

    public void performMove(int x, int y){
        //get fresh game
        updateGameFromDBInSession();
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");
        PlayerDTO me = (PlayerDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");

        //i'm allowed to make a move?
        if(canIMakeAMove()){
            //get the gameDTO
            GameDTO gameDTO = storage.getGame(game.getId());

            //save move
            if(gameDTO.getAllMoveDTOS() == null){
                gameDTO.setAllMoveDTOS(new ArrayList<>());
            }
            gameDTO.getAllMoveDTOS().add(new MoveDTO(me, x, y));
            //get and set the new state
            gameDTO.setStatus(getNextGameStatus(new Game(gameDTO)));

            //update game in db
            GameDTO freshGame = storage.updateGame(gameDTO);
            //update session object
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("game", new Game(freshGame));

            //update client
            updateGame.send(game.getId());
        }
    }

    public String quitGame(){
        //get fresh game
        updateGameFromDBInSession();
        Game game = (Game) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("game");

        if(!isGameRunning()){
            //get the gameDTO
            GameDTO gameDTO = storage.getGame(game.getId());

            if(gameDTO.getStatus() == GameState.WAITING_FOR_PLAYER){
                //delete empty game
                storage.deleteGame(gameDTO);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("game");
            return "/secured/landingpage.xhtml";
        }
        return null;
    }

    /**
     * @param game the current game
     * @return a GameStatus object that should the next state, assuming the player already did his move. That means this
     *         method will alternate between the to player if nobody has won jet.
     */
    private GameState getNextGameStatus(Game game){

        //win detection
        Cell[][] cells = game.getField().getCells();
        List<List<Cell>> allPossibleWinLines = new ArrayList<>();

        //check horizontal lines
        for(int i = 0; i < 3; i++) {
            List<Cell> cellsToCheck = new ArrayList<>();
            for (int x = 0; x < 3; x++) {
                cellsToCheck.add(cells[x][i]);
            }
            allPossibleWinLines.add(cellsToCheck);
        }
        //check vertical lines
        for(int i = 0; i < 3; i++){
            allPossibleWinLines.add(Arrays.asList(cells[i]));
        }
        //diagonal lines
        allPossibleWinLines.add(Arrays.asList(cells[0][0], cells[1][1], cells[2][2]));
        allPossibleWinLines.add(Arrays.asList(cells[0][2], cells[1][1], cells[2][0]));

        for(List<Cell> toCheck : allPossibleWinLines){
            CellState result = isSameState(toCheck);
            if(result != null){
                return cellStateToGameState(result);
            }
        }

        //draw check
        int placeCount = 0;
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                if(cells[x][y].getCellState() != CellState.EMPTY) placeCount++;
            }
        }
        if(placeCount == 9) return GameState.DRAW;

        //still here? that means the game is still running, so let's swap the player
        if(game.getState() == GameState.PLAYER_1_TURN){
            return GameState.PLAYER_2_TURN;
        }
        if(game.getState() == GameState.PLAYER_2_TURN){
            return GameState.PLAYER_1_TURN;
        }

        //wait you should't be here...
        //if the code runs to this point somebody abused the game or the win detection failed
        log.log(Level.WARNING, "Corrupted Game! We stopped the game with DRAW");
        return GameState.DRAW;
    }


    private CellState isSameState(List<Cell> cellsToCheck){
        int player1Count = 0, player2Count = 0;
        for (Cell cell : cellsToCheck) {
            if (cell.getCellState() == CellState.PLAYER_1) player1Count++;
            if (cell.getCellState() == CellState.PLAYER_2) player2Count++;
        }

        if(cellsToCheck.size() == player1Count) return CellState.PLAYER_1;
        if(cellsToCheck.size() == player2Count) return CellState.PLAYER_2;
        return null;
    }

    private GameState cellStateToGameState(CellState cellState){
        if(cellState == CellState.PLAYER_1) return GameState.PLAYER_1_WON;
        if(cellState == CellState.PLAYER_2) return GameState.PLAYER_2_WON;
        return null;
    }
}
