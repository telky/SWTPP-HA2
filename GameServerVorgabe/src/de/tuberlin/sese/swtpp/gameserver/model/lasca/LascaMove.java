package de.tuberlin.sese.swtpp.gameserver.model.lasca;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import com.sun.javafx.geom.Point2D;

public class LascaMove {
	
	public Point2D origin;
	public Point2D destination;
	
	public LascaMove(String moveString) {
		List<String> positionStrings = Arrays.asList(moveString.split("-"));
		
		origin = CoordinatesHelper.corrdinateForString(positionStrings.get(0));
		destination = CoordinatesHelper.corrdinateForString(positionStrings.get(1));

	}
	
}
