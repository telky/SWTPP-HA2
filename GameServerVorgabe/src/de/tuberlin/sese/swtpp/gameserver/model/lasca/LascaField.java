package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

public class LascaField implements Serializable {
	
	enum figureType {
		EMPTY,
		WHITE_SOLDIER,
		WHITE_OFFICER,
		BLACK_SOLDIER,
		BLACK_OFFICER
	}
	
	int row;
	int col;
	
	String id;
	
	ArrayList<figureType> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	
	public LascaField(int row, int col){
		this.id = Integer.toString(row) + "-" + Integer.toString(col);
		this.row = row;
		this.col = col;
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		
		this.figures = new ArrayList<figureType>();
	}
	
	public String toFenString() {
		return "TODO";
	}

}
