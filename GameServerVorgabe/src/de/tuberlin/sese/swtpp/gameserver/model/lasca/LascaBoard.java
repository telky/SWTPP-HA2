package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaField.figureType;

public class LascaBoard implements Serializable {
		
	HashMap<String, LascaField> fields;
	
	int fieldSize = 7;
	
	public LascaBoard(String fenState) {
		fields = new HashMap<String, LascaField>();
		parseFen(fenState);
		connectFields();
		//setupBoard();
	}
	
	
	public String toFenString() {
		String result = "";
		for (int row = 1; row <= fieldSize; row ++) {
			String currentColumn = "";
			for (int column = 1; column <= fieldSize; column ++) {
				String id = idFor(row, column);
				LascaField field = fields.get(id);
				currentColumn.concat(field.toFenString());
			}
			result.concat(currentColumn);
			if (row != fieldSize) {
				result.concat("/");
			}
		}
		
			
		return result; 
	}
	
	public HashMap<String, LascaField> getFields() {
		return fields;
	}
	
	private void setupBoard(){
		createFields();
		connectFields();
		
		
		for ( LascaField field: fields.values()) {
			LascaField fieldTemp = field;
		}
		
	}
	
	private void parseFen(String fenString) {
		fenString = fenString.replaceAll(",,", ",-,");
		fenString = fenString.replaceAll("/,", "/-,");
		fenString = fenString.replaceAll(",/", ",-/");
		
		List<String> fenRows = Arrays.asList(fenString.split("/"));
		
		int row = 1;
		int column = 1;
		 
		for (String fenRow: fenRows) {
			
			List<String> columnArray = Arrays.asList(fenRow.split(","));
			Boolean even = row % 2 == 0;
			
			
			row = 1;
			for (String component : columnArray) {
				
				LascaField field = new LascaField(row, column);
				switch (component) {
				case "b":
					field.figures.add(figureType.BLACK_SOLDIER);
					break;
				case "B":
					field.figures.add(figureType.BLACK_OFFICER);
					break;
				case "w":
					field.figures.add(figureType.WHITE_SOLDIER);
					break;
				case "W":
					field.figures.add(figureType.WHITE_OFFICER);
					break;
				default:
					field.figures.add(figureType.EMPTY);
					break;
				}
				
				
				String fieldId = Integer.toString(row) + "-" + Integer.toString(column);
				fields.put(fieldId, field);
				row++;
			}
			column++;
		}
	
	}
	
	private void createFields(){
		for(int rowIndex = 1; rowIndex < 8; rowIndex++){
			for (int colIndex = 1; colIndex < 8; colIndex++){
				if(rowIndex%2 != 0 && colIndex%2 != 0 || rowIndex%2 == 0 && colIndex%2 == 0){ // determine whether its a 4 or 3 field row
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
	
	private String idFor(int row, int column) {
		return Integer.toString(row) + "-" + Integer.toString(column);
	}

}