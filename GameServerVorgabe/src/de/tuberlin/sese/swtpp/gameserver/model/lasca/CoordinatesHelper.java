package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import com.sun.javafx.geom.Point2D;

public class CoordinatesHelper {

	public static Point2D corrdinateForString(String stringValue) {
		char first = stringValue.charAt(0);
		int secondInt = (int)stringValue.charAt(0);
		int firstInt = 0;
	
        switch(first){
        case 'a': firstInt = 1; break;
        case 'b': firstInt = 2; break;
        case 'c': firstInt = 3; break;
        case 'd': firstInt = 4; break;
        case 'e': firstInt = 5; break;
        case 'f': firstInt = 6; break;
        case 'g': firstInt = 7; break;
        }
		
		return new Point2D(firstInt, secondInt);
	}
}
