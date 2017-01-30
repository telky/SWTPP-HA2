package de.tuberlin.sese.swtpp.gameserver.model.lasca;
import java.util.Arrays;
import java.util.List;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class LascaMove extends Move {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Point2D origin;
	public Point2D destination;
	
	public LascaMove(String moveString, Player player) {	
		// TODO: Needs Implementation for move over multiple points
		super(moveString, "TODO insert state here", player);
		List<String> positionStrings = Arrays.asList(moveString.split("-"));
		
		origin = CoordinatesHelper.corrdinateForString(positionStrings.get(0));
		destination = CoordinatesHelper.corrdinateForString(positionStrings.get(1));
		
		
	}
	
}
