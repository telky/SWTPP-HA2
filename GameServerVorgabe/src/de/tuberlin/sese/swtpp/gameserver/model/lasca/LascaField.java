package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

import com.sun.javafx.geom.Point2D;

public class LascaField implements Serializable {
	int row;
	int col;
	
	String id;
	
	ArrayList<figureType> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	public enum figureType {
		EMPTY ("__"),
		WHITE_SOLDIER ("w"),
		WHITE_OFFICER ("W"),
		BLACK_SOLDIER ("b"),
		BLACK_OFFICER ("B");
		
		private final String name;       

	    private figureType(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName) {
	        // (otherName == null) check is not needed because name.equals(null) returns false 
	        return name.equals(otherName);
	    }

	    public String toBoardName() {
	       return this.name;
	    }
	}
	
	public LascaField(int row, int col){
		this.row = row;
		this.col = col;
		this.id = this.calculateID();
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		
		this.figures = new ArrayList<figureType>();
	}
	
	public String getFiguresOnField() {
		String figuresOnField = "";
		for(figureType figure: this.figures){
			figuresOnField.concat(figure.toBoardName());
		}
		return figuresOnField;
	}
	
	private String calculateID(){
		Point2D tmp = new Point2D(row, col);
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}

}
