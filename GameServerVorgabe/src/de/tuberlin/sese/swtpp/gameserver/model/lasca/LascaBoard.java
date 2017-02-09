package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.ColorType;

public class LascaBoard implements Serializable {

	HashMap<String, LascaField> fields;
	Character currentPlayer = null;

	int fieldSize = 7;
	int minFieldIndex = 1;

	public LascaBoard(String fenState) {
		fields = new HashMap<String, LascaField>();
		parseFen(fenState);
		connectFields();
	}

	public String toFenString() {
		String result = "";
		for (int row = fieldSize; row >= minFieldIndex; row--) {
			String currentColumn = "";
			for (int column = 1; column <= fieldSize; column++) {
				String id = idFor(row, column);
				LascaField field = fields.get(id);
				if (field != null) {
					currentColumn = currentColumn + (field.getFiguresOnField());
					currentColumn = currentColumn + (",");
				}
			}
			result = result + (currentColumn);
			result = removeLastChar(result);
			result = result + ("/");
		}
		result = removeLastChar(result);
		return result;
	}

	private static String removeLastChar(String str) {
		return str.substring(0, str.length() - 1);
	}

	public HashMap<String, LascaField> getFields() {
		return fields;
	}

	private void setCurrentPlayer(String fen) {
		String lastTwoCharacters = fen.substring(fen.length() - 2);
		if (lastTwoCharacters.startsWith(" ")) {
			this.currentPlayer = lastTwoCharacters.charAt(1);
		} else {
			System.out.println("FEN String is missing attribute for currentPlayer");
		}
	}

	private void parseFen(String fenString) {
		setCurrentPlayer(fenString);
		if (currentPlayer != null) {
			fenString = fenString.substring(0, fenString.length() - 2);
		}

		List<String> fenRows = Arrays.asList(fenString.split("/"));

		int row = fieldSize;

		for (String fenRow : fenRows) {
			parseRow(fenRow, row);
			row--;
		}
	}

	private void parseRow(String row, int rowIndex) {
		row = row.replaceAll(".(?=)", " $0 ");
		List<String> columnList = Arrays.asList(row.split(","));
		Boolean evenRow = rowIndex % 2 == 0;
		int columnIndex = evenRow ? 2 : 1;

		for (String component : columnList) {
			parseColumn(component, rowIndex, columnIndex);
			columnIndex = columnIndex + 2; // skip invalid fields
		}
	}

	private void parseColumn(String component, int rowIndex, int columnIndex) {
		component = component.replaceAll("\\s","");
		Boolean evenColumn = columnIndex % 2 == 0;
		Boolean evenRow = rowIndex % 2 == 0;
		 Deque<LascaFigure> figuresOnCurrentField = new LinkedList<LascaFigure>();
		// check if field is valid and can be used by figure
		if (evenRow == evenColumn) { 
			LascaField newField = new LascaField(rowIndex, columnIndex);
			// only valid fields are added
			figuresOnCurrentField = parseFigures(component);
			newField.figures = figuresOnCurrentField;
			fields.put(newField.id, newField);
		}
	}

	private LinkedList<LascaFigure> parseFigures(String figureString) {
		LinkedList<LascaFigure> figuresRead = new LinkedList<LascaFigure>();
		for (int i = 0; i < figureString.length(); i++) {
			String currentFigure = Character.toString(figureString.charAt(i));
			figuresRead.addLast(new LascaFigure(currentFigure));
		}
		return figuresRead;
	}

	private String idFor(int row, int column) { // TODO remove, unnecessary
		// wrapper
		Point2D tmp = new Point2D(column, row);
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}

	// for debugging
	public void printBoard() {
		System.out.println("Current board:");
		for (int rowIndex = fieldSize; rowIndex >= minFieldIndex; rowIndex--) {

			for (int colIndex = minFieldIndex; colIndex <= fieldSize; colIndex++) {
				if (rowIndex % 2 != 0 && colIndex % 2 != 0 || rowIndex % 2 == 0 && colIndex % 2 == 0) { 
					// determine whether it is a 4 field or 3 field row 
					String fieldID = idFor(rowIndex, colIndex);
					if (!fields.containsKey(fieldID)) { 
						// invalid field
						System.out.print("[/]");
					} else if (!fields.get(fieldID).isEmpty()) { 
						// field with figures
						// print figure stack
						System.out.print("[" + fields.get(fieldID).getFiguresOnField() + "]");
					} else {
						// print empty field
						System.out.print("[_]");
					}
				} else {
					System.out.print("[ ]");
				}
			}
			System.out.println("\n");
		}
		System.out.print(" ---------------------- \n");
	}

	public LascaField getField(String fenPoint) {
		return fields.get(fenPoint);
	}

	public LascaField getField(Point2D point) {
		return getField(CoordinatesHelper.fenStringForCoordinate(point));
	}
	
	public Boolean isValidField(Point2D point) {
		return fields.containsKey(CoordinatesHelper.fenStringForCoordinate(point));
	}

	public LascaField getFieldBetween(LascaField source, LascaField destination) {
		boolean left = source.col > destination.col;
		boolean whiteForward = source.row < destination.row;
		Point2D tempPoint = new Point2D(source.col + (left ? -1 : 1), source.row + (whiteForward ? 1 : -1));
		LascaField fieldBetween = getField(tempPoint);
		return fieldBetween;
	}

	// only call for valid moves
	// move top figure (and prisoners if they exist) from origin to destination
	public void moveFigure(LascaField origin, LascaField destination) {
		LascaFigure selectedSoldier = origin.removeTopFigure();
		destination.addFigure(selectedSoldier);
		
		// if figure has prisoners, move prisoners as well
		// if stack of figures of same color, move only top figure
		if(origin.figures.size() > 0 && origin.getTopFigure().color != selectedSoldier.color){
			for(LascaFigure figure: origin.figures){
				destination.addLastFigure(figure);
			}
			// remove all figures from origin, as all figures must have been moved
			origin.removeAllFigures();
		}
		fields.put(origin.id, origin);
		fields.put(destination.id, destination);
	}

	public void strike(LascaField origin, LascaField destination, boolean movingRight, boolean forward) {
		Point2D destinationPoint = CoordinatesHelper.corrdinateForString(destination.id);

		Point2D newDestinationPoint = new Point2D(destinationPoint.x + (movingRight ? 1 : -1),
				destinationPoint.y + (forward ? 1 : -1));
		LascaField newDestination = getField(CoordinatesHelper.fenStringForCoordinate(newDestinationPoint));
		
		//  move figure from origin (and prisoners if exist) to newDestination
		ColorType saveOwnColor = origin.getTopFigure().color;
		moveFigure(origin, newDestination);
		// take all opponents figures from destination and move them to newDestination
		while(destination.figures.size() > 0 && destination.getTopFigure().color != saveOwnColor){
			newDestination.figures.addLast(destination.removeTopFigure());
		}
	}
	
	public List<LascaField> figuresForColor(ColorType color) {
		List<LascaField> result = new ArrayList<LascaField>();
		for (LascaField field : fields.values()) {
			if (!field.isEmpty() && field.getTopFigure().color == color) {
				result.add(field);
			}
		}
		return result;
	}

	// set the neighbours of every field on the board
	private void connectFields() {
		for (int rowIndex = fieldSize; rowIndex >= minFieldIndex; rowIndex--) {
			for (int colIndex = minFieldIndex; colIndex <= fieldSize; colIndex++) {
				if (validField(rowIndex, colIndex)) {
					Point2D tmp = new Point2D(colIndex, rowIndex);
					String fieldId = CoordinatesHelper.fenStringForCoordinate(tmp);
					setNeighbours(fields.get(fieldId));
				}

			}
		}
	}
	
	private boolean validField(int row, int col){
		Point2D tmp = new Point2D(col, row);
		String fenStringIdentifier = CoordinatesHelper.fenStringForCoordinate(tmp);	
		return fields.containsKey(fenStringIdentifier);
	}
	
	private boolean validField(Point2D point){
		return fields.containsKey(CoordinatesHelper.fenStringForCoordinate(point));	
	}

	// set each fields neighbours
	private void setNeighbours(LascaField field) {
		Point2D topLeft = new Point2D(field.col - 1, field.row + 1);
		if (validField(topLeft)) {
			field.neighbourFieldTopLeft = (fields.get(CoordinatesHelper.fenStringForCoordinate(topLeft)));
		}
		Point2D topRight = new Point2D(field.col + 1, field.row + 1);
		if (validField(topRight)) {
			field.neighbourFieldTopRight = (fields.get(CoordinatesHelper.fenStringForCoordinate(topRight)));
		}
		Point2D bottomLeft = new Point2D(field.col - 1, field.row - 1);
		if (validField(bottomLeft)) {
			field.neighbourFieldBottomLeft = (fields.get(CoordinatesHelper.fenStringForCoordinate(bottomLeft)));
		}
		Point2D bottomRight = new Point2D(field.col + 1, field.row - 1);
		if (validField(bottomRight)) {
			field.neighbourFieldBottomRight = (fields.get(CoordinatesHelper.fenStringForCoordinate(bottomRight)));
		}
	}


}
