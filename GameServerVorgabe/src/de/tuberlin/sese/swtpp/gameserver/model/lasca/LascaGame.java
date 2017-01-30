package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.ColorType;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance
 * that allows to play Lasca (http://www.lasca.org/).
 *
 */
public class LascaGame extends Game implements Serializable {

	private static final long serialVersionUID = 8461983069685628324L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black
	// player
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
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (whiteGaveUp())
				gameInfo = "white gave up";
			else if (didWhiteDraw() && !didBlackDraw())
				gameInfo = "white called draw";
			else if (!didWhiteDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "white won";
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
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(p -> p.requestedDraw())) {
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

	// only call for valid moves!
	private boolean tryStrikeSoldier(LascaMove move, LascaField origin, LascaField destination) {
		boolean movingRight = move.origin.x + 1 == move.destination.x;

		LascaField destinationField = board.getField(move.destination);
		if (!destinationField.isEmpty()) {
			LascaFigure strikedFigure = destinationField.topFigure();
			if ((strikedFigure.color == ColorType.WHITE && move.getPlayer() == blackPlayer)
					|| (strikedFigure.color == ColorType.BLACK && move.getPlayer() == whitePlayer)) {

				boolean forward = move.getPlayer() == whitePlayer; 
				
				Point2D newDestinationPoint = new Point2D(move.destination.x + (movingRight ? 1 : - 1) , move.destination.y + (forward ? 1 : -1)); 
				LascaField newDestination = board.getField(newDestinationPoint);
				
				if (newDestination.isEmpty()) {
					board.strike(origin, destination, movingRight, forward);
					return true;					
				}
			}
		}
		return false;
	}

	private boolean trySoldierMove(LascaMove move, LascaField origin, LascaField destination) {
		boolean diagonal = (move.origin.x + 1 == move.destination.x) || (move.origin.x - 1 == move.destination.x);
		boolean forward = move.getPlayer() == whitePlayer ? move.origin.y + 1 == move.destination.y : move.origin.y - 1 == move.destination.y;

		if (diagonal && forward) {
			if (!tryStrikeSoldier(move, origin, destination)) {
				if (destination.isEmpty()) {
					board.moveFigure(origin, destination);	
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		LascaMove move = new LascaMove(moveString, player);

		LascaField origin = board.getField(move.origin);
		LascaField destination = board.getField(move.destination);

		boolean validMove = false;

		if (origin.isEmpty()) {
			return false;
		} else if ((origin.topFigure().color == ColorType.BLACK && player != blackPlayer) || ((origin.topFigure().color == ColorType.WHITE && player != whitePlayer))) {
			return false;
		}

		LascaFigure selectedFigure = origin.topFigure();
		switch (selectedFigure.type) {
		case SOLDIER:
			validMove = trySoldierMove(move, origin, destination);
			break;
		case OFFICER:
			// TODO validate move
			break;
		default:
			validMove = false;
			break;
		}

		if (validMove) {
			setNextPlayer(isWhiteNext() ? blackPlayer : whitePlayer);
			history.add(move);
		}

		return validMove;
	}

}
