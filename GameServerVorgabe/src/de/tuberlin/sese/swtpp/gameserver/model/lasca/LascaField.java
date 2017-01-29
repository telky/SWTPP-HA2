package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

public class LascaField implements Serializable {
	
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
	
	int row;
	int col;
	
	String id;
	
	ArrayList<figureType> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	
	public LascaField(int row, int col){
		this.row = row;
		this.col = col;
		this.id = this.toFenString();
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		
		this.figures = new ArrayList<figureType>();
	}
	
	public String toFenString() {
		return Integer.toString(this.row) + "-" + Integer.toString(this.col);
	}

}
