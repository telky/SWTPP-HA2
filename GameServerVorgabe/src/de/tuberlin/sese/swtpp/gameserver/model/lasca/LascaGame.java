package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class LascaGame extends Game implements Serializable{

	private static final long serialVersionUID = 8461983069685628324L;
	
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	LascaBoard board;
	String state;
	
	/************************
	 * constructors
	 ***********************/
	
	public LascaGame() {
		super();
		this.board = new LascaBoard("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w");
		// initialize internal game model (state/ board here)
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blackGaveUp()) gameInfo = "black gave up";
			else if(whiteGaveUp()) gameInfo = "white gave up";
			else if(didWhiteDraw() && !didBlackDraw()) gameInfo = "white called draw";
			else if(!didWhiteDraw() && didBlackDraw()) gameInfo = "black called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = blackPlayer.isWinner()? "black won" : "white won";
		}
			
		return gameInfo;
	}	

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) { 
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// this method is public only for test coverage
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	@Override
	public void setState(String state) {
		board = new LascaBoard(state);
	}
	
	@Override
	public String getState() {
		return board.toFenString();
	}
	
	private boolean trySoldierMove(LascaMove move) {
		boolean diagonal = (move.origin.x == move.destination.x + 2) || (move.origin.x == move.destination.x - 2);
		boolean forward = move.origin.y + 2 == move.destination.y; 
		
		// todo add handling for strike
		
		return diagonal && forward;
	}
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		LascaMove move = new LascaMove(moveString);
		
		LascaField origin = board.getField(CoordinatesHelper.fenStringForCoordinate(move.origin));
		LascaField destination = board.getField(CoordinatesHelper.fenStringForCoordinate(move.destination));
		
		if (origin.isEmpty()) {
			return false;
		}
		
		FigureType selectedFigure = origin.topFigure();
		
		switch (selectedFigure) {
			case WHITE_SOLDIER:
				return trySoldierMove(move);
			case WHITE_OFFICER:
				// TODO validate move
				break;
			case BLACK_SOLDIER: 
				return trySoldierMove(move);
			case BLACK_OFFICER: 
				break;
			default: return false;
		}
		
		return false;
	}



}
