package de.tuberlin.sese.swtpp.gameserver.model.lasca;
import java.util.Arrays;
import java.util.List;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class LascaMove {
	
	public Point2D origin;
	public Point2D destination;
	public Player player;
	
	public LascaMove(String moveString, Player player) {	// TODO: Needs Implementation for move over multiple points
		List<String> positionStrings = Arrays.asList(moveString.split("-"));
		
		origin = CoordinatesHelper.corrdinateForString(positionStrings.get(0));
		destination = CoordinatesHelper.corrdinateForString(positionStrings.get(1));
		this.player = player;
	}
	
}
