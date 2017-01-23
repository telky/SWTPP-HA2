package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

public class LascaField implements Serializable {
	
	int row;
	int col;
	
	String id;
	
	LinkedList<LascaFigure> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	
	public LascaField(int row, int col){
		this.id = Integer.toString(row) + "-" + Integer.toString(col);
		this.row = row;
		this.col = col;
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		this.figures = new LinkedList<LascaFigure>();
		
	}

}
