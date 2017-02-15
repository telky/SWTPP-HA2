package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.util.Arrays;
import java.util.List;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class LascaMove extends Move  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Point2D origin;
	public Point2D destination;
	public Boolean isStrike;
	public Boolean isUpgrade;

	public LascaMove(String moveString, String state, Player player) {
		super(moveString, state, player); //TODO not sure
		List<String> positionStrings = Arrays.asList(moveString.split("-"));

		origin = CoordinatesHelper.corrdinateForString(positionStrings.get(0));
		destination = CoordinatesHelper.corrdinateForString(positionStrings.get(1));
		isStrike = false;
		isUpgrade = false;
	}

	public boolean isDiagonal() { // TODO only working for soldiers
		int lengthX = (int) Math.abs(this.destination.x - this.origin.x);
		int lengthY = (int) Math.abs(this.destination.y - this.origin.y);

		if(lengthX == lengthY){
			return true;
		}
		return false;
	}

	public boolean isSimpleMove() {
		return origin.x + 1 == destination.x || origin.x - 1 == destination.x;
	}

	public boolean isStrikeLength() {
		return !(origin.x + 2 != destination.x && origin.x - 2 != destination.x); 
	}

}
