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
	public enum MoveType{
		TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
	}
	public enum MoveTypeHelper {
		MAIN;
		private MoveType moveType;
		
		private MoveTypeHelper getMove(boolean top, boolean left){
			if(top){
				if(left){
					this.moveType = MoveType.TOPLEFT;
				} else {
					this.moveType = MoveType.TOPRIGHT;
				}
			} else{
				if(!left){
					this.moveType = MoveType.BOTTOMRIGHT;
				} else {
					this.moveType = MoveType.BOTTOMLEFT;
				}
			}

			return this;
		}
		
		public MoveType getMoveType(boolean top, boolean left){
			return getMove(top, left).moveType;
		}
		
		public MoveType getOppositeDirection(MoveType currentMoveType){
			if(currentMoveType == MoveType.BOTTOMLEFT){
				return MoveType.TOPRIGHT;
			} else if(currentMoveType == MoveType.BOTTOMRIGHT){
				return MoveType.TOPLEFT;
			} else if(currentMoveType == MoveType.TOPLEFT){
				return MoveType.BOTTOMRIGHT;
			} else {
				return MoveType.BOTTOMLEFT;
			}
		}
		
	}

	/************************
	 * constructors
	 ***********************/

	public LascaGame() {
		super();
		this.board = new LascaBoard("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w");
		// initialize internal game model (state/ board here)
		setCurrentPlayer(isWhiteNext() ? 'w' : 'b');
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
		return board.toFenString();
	}

	// only call for valid moves!
	private boolean tryStrikeSoldier(LascaMove move, LascaField origin, LascaField destination) {
		if (!move.isStrikeLength()) {
			return false;
		} else if (!destination.isEmpty()) {
			return false;
		}

		LascaField opponentField = board.getFieldBetween(origin, destination);
		if (!opponentField.isEmpty()) {
			LascaFigure figureToStrike = opponentField.getTopFigure();
			if (figureIsStrikable(figureToStrike.color, move.getPlayer())) {
				board.strike(origin, opponentField, move.origin.x < move.destination.x,
						move.origin.y < move.destination.y);
				checkUpgrade(move, destination);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	private boolean tryOfficerMove(LascaMove move, LascaField origin, LascaField destination, boolean canStrike) {
		boolean diagonal = move.isDiagonal();
		if (diagonal) {
			if(!canStrike){
				if(destination.isEmpty()){
					if(move.isSimpleMove()){
						board.moveFigure(origin, destination, false);
						return true;
					}
				}
			}
			
//			if (!canStrike && destination.isEmpty() && move.isSimpleMove()) {
//				board.moveFigure(origin, destination, false);
//				return true;
//			} 
			if (canStrike && tryStrikeSoldier(move, origin, destination)) {
				move.isStrike = true;
				return true;
			} else if(canStrike){
				return false;
			} 
		}
		return false;
	}
	
	private MoveType getMoveType(LascaField origin, LascaField destination){
		boolean topLeft, topRight, bottomLeft = false;
		topLeft =  destination.col < origin.col && origin.row < destination.row;
		topRight = destination.col > origin.col && origin.row < destination.row;
		bottomLeft =  destination.col < origin.col && origin.row > destination.row;
		
		return MoveTypeHelper.MAIN.getMoveType(topLeft || topRight, topLeft || bottomLeft);
	}
	

	private boolean figureIsStrikable(ColorType figureColor, Player movePlayer) {
		if(figureColor == ColorType.WHITE){
			return movePlayer == blackPlayer;
		} else {
			return movePlayer == whitePlayer;
		}

	}

	private boolean trySoldierMove(LascaMove move, LascaField origin, LascaField destination, boolean canStrike) {
		boolean diagonal = move.isDiagonal();
		boolean forward = checkSoldierDirectionForward(move);

		if (diagonal && forward) {
			// check: strike possible?
			boolean strike = tryStrikeSoldier(move, origin, destination);
			if (canStrike && strike) {	
				move.isStrike = true;
				return true;
			} else if(canStrike && !strike){
				move.isStrike = false;
				return false;
			} else if (destination.isEmpty() && move.isSimpleMove()) {
				// simple move without strike 
				board.moveFigure(origin, destination, false);
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
		} else {
			return move.origin.y > move.destination.y;
		}
	}

	private void checkUpgrade(LascaMove move, LascaField destination) {
		if(destination.getTopFigure().type == FigureType.OFFICER){
			return;
		} else if (destination.row == 7 && move.getPlayer() == whitePlayer) { 
			upgrade(move, destination);
		} else if (destination.row == 1 && move.getPlayer() == blackPlayer) {
			upgrade(move, destination);
		}
	}
	
	private void upgrade(LascaMove move, LascaField destination){
		destination.getTopFigure().upgrade();
		move.isUpgrade = true;
	}
	
	// TODO Refactoring
	private boolean canStrike(LascaField field) {
		LascaFigure currentFigure = field.getTopFigure();
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
			if (selectedField != null && !selectedField.isEmpty() && selectedField.getTopFigure().color != currentColor) {
				LascaField destination = board.getField(points.get(i+1));
				if (destination != null && destination.isEmpty()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean canMove(LascaField field) {
		LascaFigure currentFigure = field.getTopFigure();
		ColorType currentColor = currentFigure.color;
		Point2D coordinate = field.getCoordinate();
		
		int yDir = currentColor == ColorType.WHITE ? 1 : -1;
		
		List<Point2D> points = new ArrayList<Point2D>();
		
		Point2D topLeft  = new Point2D(coordinate.x - 1 , coordinate.y + yDir);
		points.add(topLeft);
		
		Point2D topRight  = new Point2D(coordinate.x + 1 , coordinate.y + yDir);
		points.add(topRight);
		
		if (currentFigure.type == FigureType.OFFICER) {
			Point2D bottomLeft  = new Point2D(coordinate.x - 1 , coordinate.y - yDir);
			points.add(bottomLeft);
			
			Point2D bottomRight  = new Point2D(coordinate.x + 1 , coordinate.y - yDir);
			points.add(bottomRight);
		}
		
		for (int i = 0; i <  points.size(); i++) {
			LascaField selectedField = board.getField(points.get(i));
			if (selectedField != null && selectedField.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean canMove(Player player) {
		List<LascaField> fields = board.figuresForColor(colorForPlayer(player));
		for (LascaField field: fields) {
			if (canMove(field)) {
				return true;
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
	
	private boolean canMoveOrStrike(Player player) {
		if(canMove(player)){
			return true;
		} else {
			return canStrike(player);
		}
	}
	

	@Override
	public boolean tryMove(String moveString, Player player) {
		if(this.nextPlayer != player){
			return false;
		}
		if(expectedMoves.size() > 0){
			LascaMove tmpMove = new LascaMove(moveString, player);
			if(!checkExpectedMoveContains(tmpMove)){
				return false;
			}
		}
		
		Boolean canStrike = canStrike(player);
		LascaMove move = new LascaMove(moveString, player);
		LascaField origin = board.getField(move.origin);
		LascaField destination = board.getField(move.destination);
		if(origin.isEmpty()){
			return false;
		}
		Boolean currentPlayerCantMoveOrigin = !checkFieldFigure(origin, player);
		
		if (currentPlayerCantMoveOrigin) {
			return false;
		} 

		LascaFigure selectedFigure = origin.getTopFigure();

		boolean validMove = selectedFigure.type == FigureType.SOLDIER ? trySoldierMove(move, origin, destination, canStrike) : tryOfficerMove(move, origin, destination, canStrike);
		Boolean otherPlayerCanMoveOrStrike = this.canMoveOrStrike(isWhiteNext() ? blackPlayer : whitePlayer);
	
		if (!otherPlayerCanMoveOrStrike){
			this.finish(isWhiteNext() ? whitePlayer : blackPlayer);
			// reset next player if game is ended
			setCurrentPlayer(isWhiteNext() ? 'b' : 'w');
		}
		if(validMove){
			  this.history.add(move);
			  checkMoveStatus(move);
			  return true;
		} else {
			return false;
		}
	}
	
	private void checkMoveStatus(LascaMove move){
		// reset expectedMoves
		LascaField origin = board.getField(move.origin);
		LascaField destination = board.getField(move.destination);
		MoveType currentMoveType = move.isStrike ? this.getMoveType(origin, destination): null;
		this.expectedMoves = new ArrayList<LascaMove>();
		if(move.isStrike && strikeCanBeContinued(move, currentMoveType)){
			return;
		} else if(this.isFinished()) {
			return;
		} else {
			setNextPlayer(isWhiteNext() ? blackPlayer : whitePlayer);
			return;
		}
	}
	
	// check whether a strike must be continued, save possible moves to expectedMoves
	private boolean strikeCanBeContinued(LascaMove move, MoveType currentMoveType){
		if(move.isUpgrade || isFinished()){
			return false;
		}
		LascaField currentField = board.getField(move.destination);
		calculatePossibleDestintations(currentField, move.getPlayer(), currentMoveType);
		return expectedMoves.size() > 0;
	}
	
	private boolean checkExpectedMoveContains(LascaMove move){
		for(LascaMove current: this.expectedMoves){
			if(current.origin.equals(move.origin) && current.destination.equals(move.destination)){
				return true;
			}
		}
		return false;
	}
	
	// calculate possible moves that the topFigure on currentField can perform, save to expectedMoves for nextMove
	private void calculatePossibleDestintations(LascaField currentField, Player player, MoveType currentMoveType){
		LascaFigure currentFigure = currentField.getTopFigure();
		if(currentFigure.type == FigureType.OFFICER){
			// calculate possible officer moves
			calculatePossibleDestinations_OfficerMove(currentField, player, currentMoveType);
		} else {
			// calculate possible soldier moves
			calculatePossibleDestinations_SoldierMove(currentField, player);
		}
	}
	
	private void calculatePossibleDestinations_SoldierMove(LascaField currentField, Player player){
		if(currentField.getTopFigure().color == ColorType.BLACK){
			calculatePossibleDestinations_SoldierBlack(currentField, player);
		} else {
			calculatePossibleDestinations_SoldierWhite(currentField, player);
		}
	}
	
	private void calculatePossibleDestinations_SoldierBlack(LascaField currentField, Player player){
		Boolean enemyOnBottomLeft =  this.isPossibleMove(currentField, MoveType.BOTTOMLEFT, ColorType.WHITE);
		Boolean enemyOnBottomRight = this.isPossibleMove(currentField, MoveType.BOTTOMRIGHT, ColorType.WHITE);
		
		if (enemyOnBottomLeft && currentField.neighbourFieldBottomLeft != null && currentField.neighbourFieldBottomLeft.neighbourFieldBottomLeft != null && currentField.neighbourFieldBottomLeft.neighbourFieldBottomLeft.isEmpty()){
			updateExpectedMovesSoldier(currentField, MoveType.BOTTOMLEFT, player);
		}
		if(enemyOnBottomRight && currentField.neighbourFieldBottomRight != null && currentField.neighbourFieldBottomRight.neighbourFieldBottomRight != null &&currentField.neighbourFieldBottomRight.neighbourFieldBottomRight.isEmpty()){
			updateExpectedMovesSoldier(currentField, MoveType.BOTTOMRIGHT, player);
		}
	}
	
	private void calculatePossibleDestinations_SoldierWhite(LascaField currentField, Player player){
		Boolean enemyOnTopLeft =  this.isPossibleMove(currentField, MoveType.TOPLEFT, ColorType.BLACK);
		Boolean enemyOnTopRight =  this.isPossibleMove(currentField, MoveType.TOPRIGHT, ColorType.BLACK);
		
		if (enemyOnTopLeft  && currentField.neighbourFieldTopLeft.neighbourFieldTopLeft != null && currentField.neighbourFieldTopLeft.neighbourFieldTopLeft.isEmpty()){
			updateExpectedMovesSoldier(currentField, MoveType.TOPLEFT, player);
		}
		if (enemyOnTopRight && currentField.neighbourFieldTopRight.neighbourFieldTopRight != null && currentField.neighbourFieldTopRight.neighbourFieldTopRight.isEmpty()){
			updateExpectedMovesSoldier(currentField, MoveType.TOPRIGHT, player);
		}
	}
	
	private boolean isPossibleMove(LascaField field, MoveType moveType, ColorType opponentColor){
		return field.getNeighbourByMoveType(moveType)  != null && !field.getNeighbourByMoveType(moveType).isEmpty() && field.getNeighbourByMoveType(moveType).getTopFigure().color == opponentColor;
	}
	
	// update expected move for soldier strike length
	private void updateExpectedMovesSoldier(LascaField field, MoveType moveType, Player player){
		String moveString = field.id + "-" + field.getNeighbourByMoveType(moveType).getNeighbourByMoveType(moveType) .id;
		LascaMove possibleMove = new LascaMove(moveString, player);
		this.expectedMoves.add(possibleMove);
	}
	
	// update expected move for officer 
	private void updateExpectedMovesOfficer(LascaField origin, LascaField destination, Player player){
		String moveString = origin.id + "-" + destination.id;
		LascaMove possibleMove = new LascaMove(moveString, player);
		this.expectedMoves.add(possibleMove);
	}
	
	private void calculatePossibleDestinations_OfficerMove(LascaField currentField, Player player, MoveType currentMoveType){
		MoveType[] moves = {MoveType.TOPLEFT, MoveType.TOPRIGHT, MoveType.BOTTOMLEFT, MoveType.BOTTOMRIGHT};
		MoveType oppositeDirectionMoveType = MoveTypeHelper.MAIN.getOppositeDirection(currentMoveType);
		for(MoveType moveType: moves){
			LascaField nextField = currentField.getNeighbourByMoveType(moveType);
			if(nextField != null && moveType != oppositeDirectionMoveType){
				if (!nextField.isEmpty() && nextField.hasOpponentFigure(currentField)){
					nextField = nextField.getNeighbourByMoveType(moveType); //get destinationField
					if(nextField != null && nextField.isEmpty()){ // found possible move
						updateExpectedMovesOfficer(currentField, nextField, player);
					}
				}
			}
		}
	}
	
	private boolean checkFieldFigure(LascaField field, Player player) {
		return field.getTopFigure().color == colorForPlayer(player);
	}
	
	private ColorType colorForPlayer(Player player) {
		return player == blackPlayer ? ColorType.BLACK : ColorType.WHITE;
	}

	// TODO debugging purpose
	public void printBoard() {
		this.board.printBoard();
	}

}
