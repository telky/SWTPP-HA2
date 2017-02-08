package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.ColorType;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.FigureType;

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
	ArrayList<LascaMove> expectedMoves;

	/************************
	 * constructors
	 ***********************/

	public LascaGame() {
		super();
		this.board = new LascaBoard("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w");
		// initialize internal game model (state/ board here)
		setCurrentPlayer(board.currentPlayer);
		expectedMoves = new ArrayList<LascaMove>();
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

	// TODO Delete before publishing, only needed for internal tests
	public LascaBoard getBoard(){
		return this.board;
	}
	
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
				move.isStrike = true;
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
			move.isUpgrade = true;
		} else if (destination.row == 1 && move.getPlayer() == blackPlayer) {
			destination.topFigure().upgrade();
			move.isUpgrade = true;
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
	
	private boolean canStrike(LascaField field) {
		LascaFigure currentFigure = field.topFigure();
		ColorType currentColor = currentFigure.color;
		Point2D coordinate = field.getCoordinate();
		
		int yDir = currentColor == ColorType.WHITE ? 1 : -1;
		
		List<Point2D> points = new ArrayList<Point2D>();
		
		Point2D topLeft  = new Point2D(coordinate.x - 1 , coordinate.y + yDir);
		Point2D topLeftDestination  = new Point2D(coordinate.x - 2 , coordinate.y + (yDir * 2));
		points.add(topLeft);
		points.add(topLeftDestination);
		
		Point2D topRight  = new Point2D(coordinate.x + 1 , coordinate.y + yDir);
		Point2D topRightDestination  = new Point2D(coordinate.x + 2 , coordinate.y + (yDir * 2));
		points.add(topRight);
		points.add(topRightDestination);
		
		if (currentFigure.type == FigureType.OFFICER) {
			Point2D bottomLeft  = new Point2D(coordinate.x - 1 , coordinate.y - yDir);
			Point2D bottomLeftDestination  = new Point2D(coordinate.x - 2 , coordinate.y - (yDir * 2));
			points.add(bottomLeft);
			points.add(bottomLeftDestination);
			
			Point2D bottomRight  = new Point2D(coordinate.x + 1 , coordinate.y - yDir);
			Point2D bottomRightDestination  = new Point2D(coordinate.x + 2 , coordinate.y - (yDir * 2));
			points.add(bottomRight);
			points.add(bottomRightDestination);
		}
		
		for (int i = 0; i <  points.size()-1; i = i+2) {
			LascaField selectedField = board.getField(points.get(i));
			if (selectedField != null && !selectedField.isEmpty() && selectedField.topFigure().color != currentColor) {
				LascaField destination = board.getField(points.get(i+1));
				if (destination != null && destination.isEmpty()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean canStrike(Player player) {
		List<LascaField> fields = board.figuresForColor(colorForPlayer(player));
		for (LascaField field: fields) {
			if (canStrike(field)) {
				return true;
			}	
		}
		return false;
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		if(expectedMoves.size() > 0){
			LascaMove tmpMove = new LascaMove(moveString, player);
			if(!checkExpectedMoveContains(tmpMove)){
				return false;
			}
			// TODO fix double checking
		}
		Boolean canStrike = canStrike(player);
		String oldState = getState();
		LascaMove move = new LascaMove(moveString, player);
		
		LascaField origin = board.getField(move.origin);
		LascaField destination = board.getField(move.destination);

		Boolean movingFieldEmpty = origin.isEmpty();
		Boolean wrongPlayerIsMoving = this.nextPlayer != player;
		Boolean currentPlayerCantMoveOrigin = !checkFieldFigure(origin, player);
		
		if (movingFieldEmpty || wrongPlayerIsMoving || currentPlayerCantMoveOrigin) {
			return false;
		} 

		LascaFigure selectedFigure = origin.topFigure();

		boolean validMove = selectedFigure.type == FigureType.SOLDIER ? trySoldierMove(move, origin, destination) : tryOfficerMove(move, origin, destination);
				
		if (!move.isStrike && canStrike) {
			validMove = false;
			setState(oldState);
		}

		if (validMove) {
			// reset expectedMoves
			this.expectedMoves = new ArrayList<LascaMove>();
			if(move.isStrike && strikeCanBeContinued(move)){
				return validMove;
			} else {
				setNextPlayer(isWhiteNext() ? blackPlayer : whitePlayer);
			}
		}

		return validMove;
	}
	
	// check whether a strike must be continued, save possible moves to expectedMoves
	private boolean strikeCanBeContinued(LascaMove move){
		if(move.isUpgrade){
			return false;
		}
		LascaField currentField = board.getField(move.destination);
		calculatePossibleDestintations(currentField, move.getPlayer());
		return expectedMoves.size() > 0;
	}
	
	private boolean checkExpectedMoveContains(LascaMove move){
		for(LascaMove current: this.expectedMoves){
			if(current.origin.equals(move.origin) && current.destination.equals(move.destination) && current.getPlayer().equals(move.getPlayer())){
				return true;
			}
		}
		return false;
	}
	
	// calculate possible moves that the topFigure on currentField can perform, save to expectedMoves for nextMove
	private void calculatePossibleDestintations(LascaField currentField, Player player){
		LascaFigure currentFigure = currentField.topFigure();
		switch(currentFigure.type){
			case OFFICER:
				// calculate officer move
				break;
			case SOLDIER:
				// calculate possible soldier moves
				calculatePossibleDestinations_SoldierMove(currentField, player);
				break;
			default:
				break;
		}
	}
	
	private void calculatePossibleDestinations_SoldierMove(LascaField currentField, Player player){
		if(currentField.topFigure().color == ColorType.BLACK){
			Boolean enemyOnBottomLeft = !currentField.neighbourFieldBottomLeft.isEmpty() && currentField.neighbourFieldBottomLeft.topFigure().color == ColorType.WHITE;
			Boolean enemyOnBottomRight = !currentField.neighbourFieldBottomRight.isEmpty() && currentField.neighbourFieldBottomRight.topFigure().color == ColorType.WHITE;
			if (enemyOnBottomLeft && currentField.neighbourFieldBottomLeft.neighbourFieldBottomLeft.isEmpty()){
				// continuing strike posssible, add to expected moves
				String moveString = currentField.id + "-" + currentField.neighbourFieldBottomLeft.neighbourFieldBottomLeft.id;
				LascaMove possibleMove = new LascaMove(moveString, player);
				this.expectedMoves.add(possibleMove);
			}
			if(enemyOnBottomRight && currentField.neighbourFieldBottomRight.neighbourFieldBottomRight.isEmpty()){
				// continuing strike posssible, add to expected moves
				String moveString = currentField.id + "-" + currentField.neighbourFieldBottomRight.neighbourFieldBottomRight.id;
				LascaMove possibleMove = new LascaMove(moveString, player);
				this.expectedMoves.add(possibleMove);
			}
			
		} else {
			Boolean enemyOnTopLeft = !currentField.neighbourFieldTopLeft.isEmpty() && currentField.neighbourFieldTopLeft.topFigure().color == ColorType.BLACK;
			Boolean enemyOnTopRight = !currentField.neighbourFieldTopRight.isEmpty() && currentField.neighbourFieldTopRight.topFigure().color == ColorType.BLACK;
			if (enemyOnTopLeft && currentField.neighbourFieldTopLeft.neighbourFieldTopLeft.isEmpty()){
				String moveString = currentField.id + "-" + currentField.neighbourFieldTopLeft.neighbourFieldTopLeft.id;
				LascaMove possibleMove = new LascaMove(moveString, player);
				this.expectedMoves.add(possibleMove);
			}
			if (enemyOnTopRight && currentField.neighbourFieldTopRight.neighbourFieldTopRight.isEmpty()){
				String moveString = currentField.id + "-" + currentField.neighbourFieldTopRight.neighbourFieldTopRight.id;
				LascaMove possibleMove = new LascaMove(moveString, player);
				this.expectedMoves.add(possibleMove);
			}
		}
	}
	
	private void calculatePossibleDestinations_OfficerMove(LascaField currentField){
		
	}
	
	private boolean checkFieldFigure(LascaField field, Player player) {
		return field.topFigure().color == colorForPlayer(player);
	}
	
	private ColorType colorForPlayer(Player player) {
		return player == blackPlayer ? ColorType.BLACK : ColorType.WHITE;
	}

	// TODO debugging purpose
	public void printBoard() {
		this.board.printBoard();
	}

}
