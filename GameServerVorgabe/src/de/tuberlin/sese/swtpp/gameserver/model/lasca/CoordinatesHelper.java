package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import com.sun.javafx.geom.Point2D;
import java.io.Serializable;

public class CoordinatesHelper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 232464158468576215L;

	public static Point2D corrdinateForString(String stringValue) {
		Character first = stringValue.charAt(0);
		int secondInt = Character.getNumericValue(stringValue.charAt(1));
		int firstInt = 0;

		
		if(first.equals('a')){
			firstInt = 1;
		} else if(first.equals('b')){
			firstInt = 2;
		} else if(first.equals('c')){
			firstInt = 3;
		} else if(first.equals('d')){
			firstInt = 4;
		} else if(first.equals('e')){
			firstInt = 5;
		} else if(first.equals('f')){
			firstInt = 6;
		} else{
			firstInt = 7;
		}
		return new Point2D(firstInt, secondInt);
	}

	public static String fenStringForCoordinate(Point2D point) {
		String fenString = "";
		int tmp = (int) point.y;
		String row = Integer.toString(tmp);
		int column = (int) point.x;
		if(column == 1){
			fenString = fenString + ("a");
		} else if(column == 2){
			fenString = fenString + ("b");
		} else if(column == 3){
			fenString = fenString + ("c");
		} else if(column == 4){
			fenString = fenString + ("d");
		} else if(column == 5){
			fenString = fenString + ("e");
		} else if(column == 6){
			fenString = fenString + ("f");
		} else if(column == 7){
			fenString = fenString + ("g");
		}
		fenString = fenString + (row);

		return fenString;
	}
}
