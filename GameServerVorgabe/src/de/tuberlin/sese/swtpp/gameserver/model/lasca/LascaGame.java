package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

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
		this.board = new LascaBoard("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w");
		// initialize internal game model (state/ board here)
		setCurrentPlayer(board.currentPlayer);
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

	private void setCurrentPlayer(Character charPlayer) {
		if (charPlayer == 'w') {
			this.nextPlayer = this.whitePlayer;
		} else {
			this.nextPlayer = this.blackPlayer;
		}
	}

	@Override
	public void setState(String state) {
		board = new LascaBoard(state);
	}

	@Override
	public String getState() {
		String fenStringState = board.toFenString();
		String playerString = "";
		if (this.nextPlayer == blackPlayer) {
			playerString = " b";
		} else {
			playerString = " w";
		}
		return fenStringState = fenStringState + playerString;
	}

	// only call for valid moves!
	private boolean tryStrikeSoldier(LascaMove move, LascaField origin, LascaField destination) {
		if (!move.isStrikeLength()) {
			return false;
		} else if (!destination.isEmpty()) {
			return false;
		}

		LascaField opponentField = board.getFieldBetween(origin, destination);
		if (opponentField != null && !opponentField.isEmpty()) {
			LascaFigure figureToStrike = opponentField.topFigure();
			if (figureIsStrikable(figureToStrike.color, move.getPlayer())) {
				board.strike(origin, opponentField, move.origin.x < move.destination.x,
						move.origin.y < move.destination.y);
				checkUpgrade(move, destination);
				return true;
			} else {
				System.out.println("Tried to strike figure of own team, invalid move"); // TODO
				return false;
			}
		}
		System.out.println("Critical Error: Tried to get an invalid field in tryStrike"); // TODO
		return false;
	}

	private boolean figureIsStrikable(ColorType figureColor, Player movePlayer) {
		switch (figureColor) {
		case WHITE:
			return movePlayer == blackPlayer;
		case BLACK:
			return movePlayer == whitePlayer;
		}
		return false;

	}

	private boolean trySoldierMove(LascaMove move, LascaField origin, LascaField destination) {
		boolean diagonal = move.isDiagonal();
		boolean forward = checkSoldierDirectionForward(move);

		if (diagonal && forward) {
			// check: strike possible?
			if (tryStrikeSoldier(move, origin, destination)) {
				return true;
			} else if (destination.isEmpty() && move.isSimpleMove()) {
				// simple move without strike 
				board.moveFigure(origin, destination);
				checkUpgrade(move, destination);
				return true;
			}
		}
		return false;
	}

	// check if the move direction matches forward direction of figure at
	private boolean checkSoldierDirectionForward(LascaMove move) {
		if (move.getPlayer() == whitePlayer) {
			return move.origin.y < move.destination.y;
		} else if (move.getPlayer() == blackPlayer) {
			return move.origin.y > move.destination.y;
		}
		return false;
	}

	private void checkUpgrade(LascaMove move, LascaField destination) {
		if (destination.row == 7 && move.getPlayer() == whitePlayer) {
			destination.topFigure().upgrade();
		} else if (destination.row == 1 && move.getPlayer() == blackPlayer) {
			destination.topFigure().upgrade();
		}
	}

	private boolean tryOfficerMove(LascaMove move, LascaField origin, LascaField destination) {
		boolean diagonal = move.isDiagonal();

		if (diagonal) {
			// todo try strike
			if (destination.isEmpty()) {
				board.moveFigure(origin, destination);
				return true;
			} else {
				return false;
			}
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
		} else if (this.nextPlayer != player) {
			return false;
		} else if (!checkFieldFigure(origin, player)) {
			return false;
		}

		LascaFigure selectedFigure = origin.topFigure();
		switch (selectedFigure.type) {
		case SOLDIER:
			validMove = trySoldierMove(move, origin, destination);
			break;
		case OFFICER:
			validMove = tryOfficerMove(move, origin, destination);
			break;
		default:
			break;
		}

		if (validMove) {
			setNextPlayer(isWhiteNext() ? blackPlayer : whitePlayer);
			history.add(move);
		}

		return validMove;
	}

	private boolean checkFieldFigure(LascaField field, Player player) {
		return  !((field.topFigure().color == ColorType.BLACK && player != blackPlayer)
				|| ((field.topFigure().color == ColorType.WHITE && player != whitePlayer)));
	}

	// TODO debugging purpose
	public void printBoard() {
		this.board.printBoard();
	}

}
