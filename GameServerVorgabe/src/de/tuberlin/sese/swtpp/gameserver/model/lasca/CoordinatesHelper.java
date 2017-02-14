package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import com.sun.javafx.geom.Point2D;
import java.io.Serializable;

public class CoordinatesHelper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 232464158468576215L;

	public static Point2D corrdinateForString(String stringValue) {
		char first = stringValue.charAt(0);
		int secondInt = Character.getNumericValue(stringValue.charAt(1));
		int firstInt = 0;

		switch (first) {
		case 'a':
			firstInt = 1;
			break;
		case 'b':
			firstInt = 2;
			break;
		case 'c':
			firstInt = 3;
			break;
		case 'd':
			firstInt = 4;
			break;
		case 'e':
			firstInt = 5;
			break;
		case 'f':
			firstInt = 6;
			break;
		case 'g':
			firstInt = 7;
			break;
		}

		return new Point2D(firstInt, secondInt);
	}

	public static String fenStringForCoordinate(Point2D point) {
		String fenString = "";
		int tmp = (int) point.y;
		String row = Integer.toString(tmp);
		int column = (int) point.x;
		switch (column) {
		case 1:
			fenString = fenString + ("a");
			break;
		case 2:
			fenString = fenString + ("b");
			break;
		case 3:
			fenString = fenString + ("c");
			break;
		case 4:
			fenString = fenString + ("d");
			break;
		case 5:
			fenString = fenString + ("e");
			break;
		case 6:
			fenString = fenString + ("f");
			break;
		case 7:
			fenString = fenString + ("g");
			break;
		}
		fenString = fenString + (row);

		return fenString;
	}
}
