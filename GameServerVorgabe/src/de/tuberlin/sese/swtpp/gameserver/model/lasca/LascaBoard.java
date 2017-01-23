package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

public class LascaBoard implements Serializable {
		
	HashMap<String, LascaField> fields;
	
	public LascaBoard() {
		fields = new HashMap<String, LascaField>();
		setupBoard();
	}
	
	private void setupBoard(){
		createFields();
		connectFields();
		
		
		for ( LascaField field: fields.values()) {
			LascaField fieldTemp = field;
		}
		
	}
	
	private void createFields(){
		for(int rowIndex = 1; rowIndex < 8; rowIndex++){
			for (int colIndex = 1; colIndex < 8; colIndex++){
				if(rowIndex%2 != 0 && colIndex%2 != 0){ // determine whether its a 4 or 3 field row
					LascaField newField = new LascaField(rowIndex, colIndex);
					fields.put(newField.id, newField);
				} else if(rowIndex%2 == 0 && colIndex%2 == 0){
					LascaField newField = new LascaField(rowIndex, colIndex);
					fields.put(newField.id, newField);
				}
			}
		}
	}
	
	private void connectFields(){
		for(int rowIndex = 1; rowIndex < 8; rowIndex++){
			for (int colIndex = 1; colIndex < 8; colIndex++){
				if(validField(rowIndex, colIndex)){
					String fieldId = Integer.toString(rowIndex) + "-" + Integer.toString(colIndex);
					setNeighbours(fields.get(fieldId));
				}
				
			}
		}
	}
	
	private Boolean validField(int row, int col){
		String fieldId = Integer.toString(row) + "-" + Integer.toString(col);
		return fields.containsKey(fieldId);
	}
	
	private Boolean validField(String fieldID){
		return fields.containsKey(fieldID);
	}
	
	private void setNeighbours(LascaField field){
		List<LascaField> whiteDirectionNeighbours;
		List<LascaField> blackBirectionNeighbours;

		String topLeft = Integer.toString(field.row+1) + "-" +Integer.toString(field.col-1);
		if(validField(topLeft)){
			field.neighbourFieldsWhiteDirection.add(fields.get(topLeft));
		}
		String topRight =  Integer.toString(field.row+1) + "-" +Integer.toString(field.col+1);
		if(validField(topRight)){
			field.neighbourFieldsWhiteDirection.add(fields.get(topRight));
		}
		String bottomLeft =  Integer.toString(field.row-1) + "-" +Integer.toString(field.col-1);
		if(validField(bottomLeft)){
			field.neighbourFieldsBlackDirection.add(fields.get(bottomLeft));
		}
		String bottomRight =  Integer.toString(field.row-1) + "-" +Integer.toString(field.col+1);
		if(validField(bottomRight)){
			field.neighbourFieldsBlackDirection.add(fields.get(bottomRight));
		}
		
	}

}
