package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

import com.sun.javafx.geom.Point2D;
import com.sun.xml.internal.ws.util.StringUtils;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaField;
import de.tuberlin.sese.swtpp.gameserver.test.lasca.MalformedFenException;

public class LascaBoard implements Serializable {

	HashMap<String, LascaField> fields;

	int fieldSize = 7;
	int minFieldIndex = 1;

	public LascaBoard(String fenState) {
		fields = new HashMap<String, LascaField>();
		parseFen(fenState);
		this.printBoard();
		connectFields();
	}

	// TODO: wrong order, FEN strings begin with upper left corner
	// currently unused
	public String toFenString() {
		String result = "";
		for (int row = 7; row >= minFieldIndex; row--) {
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

		return result;
	}

	private static String removeLastChar(String str) {
		return str.substring(0, str.length() - 1);
	}

	public HashMap<String, LascaField> getFields() {
		return fields;
	}

	private void parseFen(String fenString) {
		try {
			validateFEN(fenString);

			fenString = fenString.replaceAll(",,", ",-,");
			fenString = fenString.replaceAll("/,", "/-,");
			fenString = fenString.replaceAll(",/", ",-/");

			List<String> fenRows = Arrays.asList(fenString.split("/"));

			int row = 7;
			int column = 1;

			for (String fenRow : fenRows) {
				parseRow(fenRow, row, column);
				row--;
			}
		} catch (MalformedFenException excep) {
			System.err.println("FEN String hat falsches Format: " + excep.getMessage());
		}
	}

	private void parseRow(String row, int rowIndex, int columnIndex) {
		List<String> columnList = Arrays.asList(row.split(","));
		Boolean evenRow = rowIndex % 2 == 0;
		if (evenRow) { // set starting column in even row to second column
			columnIndex = 2;
		} else {
			columnIndex = 1;
		}

		for (String component : columnList) {
			parseColumn(component, rowIndex, columnIndex);
			columnIndex = columnIndex + 2; // skip invalid fields
		}
	}

	private void parseColumn(String component, int rowIndex, int columnIndex) {
		Boolean evenColumn = columnIndex % 2 == 0;
		Boolean evenRow = rowIndex % 2 == 0;

		if (evenRow == evenColumn) { // check if field is valid and can be used
										// by figure
			String fieldID = this.idFor(rowIndex, columnIndex);
			if (fields.get(fieldID) != null) { // field already exists, needs
												// update
				fields.get(fieldID).figures = new ArrayList<FigureType>();
				fields.get(fieldID).figures.add(parseFigures(component));
			} else {
				LascaField newField = new LascaField(rowIndex, columnIndex); // only
																				// valid
																				// fields
																				// are
																				// added
				newField.figures.add(parseFigures(component));
				fields.put(newField.id, newField);

			}
		}
	}

	private FigureType parseFigures(String figureString) {
		// TODO: Handling of multiple figures on the same field
		switch (figureString) {
		case "b":
			return (FigureType.BLACK_SOLDIER);
		case "B":
			return (FigureType.BLACK_OFFICER);
		case "w":
			return (FigureType.WHITE_SOLDIER);
		case "W":
			return (FigureType.WHITE_OFFICER);
		default:
			return (FigureType.EMPTY);
		}
	}

	private void validateFEN(String fen) throws MalformedFenException {
		HashMap<Character, Integer> characterDict = getCharMap(fen);
		System.out.println(characterDict.get('/'));
		if (characterDict.get('/') != 6) {
			throw new MalformedFenException("Illegal number of rows");
		}
		if (characterDict.get(',') != 18) {
			throw new MalformedFenException("Illegal number of columns");
		}
		int numberOfBlackSoldiers = characterDict.get('b');
		int numberOfWhiteSoldiers = characterDict.get('w');

		int numberOfBlackOfficers = 0;
		int numberOfWhiteOfficers = 0;

		if (characterDict.get('B') != null) {
			numberOfBlackOfficers = characterDict.get('B');
		}
		if (characterDict.get('W') != null) {
			numberOfWhiteOfficers = characterDict.get('W');
		}
		if (numberOfBlackSoldiers + numberOfBlackOfficers != 11) {
			throw new MalformedFenException("Illegal number of black figures");
		}
		if (numberOfWhiteSoldiers + numberOfWhiteOfficers != 11) {
			throw new MalformedFenException("Illegal number of white figures");
		}
	}

	private HashMap<Character, Integer> getCharMap(String base) {

		HashMap<Character, Integer> characterMap = new HashMap<Character, Integer>();
		char[] baseCharArr = base.toCharArray();

		for (char currentChar : baseCharArr) {
			if (characterMap.containsKey(currentChar)) {
				characterMap.put(currentChar, characterMap.get(currentChar) + 1);
			} else {
				characterMap.put(currentChar, 1);
			}
		}
		System.out.println(characterMap.get('/'));
		return characterMap;
	}

	private void connectFields() {
		for (int rowIndex = 1; rowIndex < 8; rowIndex++) {
			for (int colIndex = 1; colIndex < 8; colIndex++) {
				if (validField(rowIndex, colIndex)) {
					String fieldId = Integer.toString(rowIndex) + "-" + Integer.toString(colIndex);
					setNeighbours(fields.get(fieldId));
				}

			}
		}
	}

	private Boolean validField(int row, int col) {
		String fieldId = Integer.toString(row) + "-" + Integer.toString(col);
		return fields.containsKey(fieldId);
	}

	private Boolean validField(String fieldID) {
		return fields.containsKey(fieldID);
	}

	private void setNeighbours(LascaField field) {

		String topLeft = Integer.toString(field.row + 1) + "-" + Integer.toString(field.col - 1);
		if (validField(topLeft)) {
			field.neighbourFieldsWhiteDirection.add(fields.get(topLeft));
		}
		String topRight = Integer.toString(field.row + 1) + "-" + Integer.toString(field.col + 1);
		if (validField(topRight)) {
			field.neighbourFieldsWhiteDirection.add(fields.get(topRight));
		}
		String bottomLeft = Integer.toString(field.row - 1) + "-" + Integer.toString(field.col - 1);
		if (validField(bottomLeft)) {
			field.neighbourFieldsBlackDirection.add(fields.get(bottomLeft));
		}
		String bottomRight = Integer.toString(field.row - 1) + "-" + Integer.toString(field.col + 1);
		if (validField(bottomRight)) {
			field.neighbourFieldsBlackDirection.add(fields.get(bottomRight));
		}

	}

	private String idFor(int row, int column) { // TODO remove, unnecessary
												// wrapper
		Point2D tmp = new Point2D(column, row);
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}

	// for debugging
	private void printBoard() {

		System.out.println("Current board: \n\n");
		for (int rowIndex = 7; rowIndex > 0; rowIndex--) {
			System.out.println("\n");
			for (int colIndex = 1; colIndex < 8; colIndex++) {
				if (rowIndex % 2 != 0 && colIndex % 2 != 0 || rowIndex % 2 == 0 && colIndex % 2 == 0) { // determine
																										// whether
																										// its
																										// a
																										// 4
																										// or
																										// 3
																										// field
																										// row
					String fieldID = idFor(rowIndex, colIndex);
					if (!fields.containsKey(fieldID)) { // invalid field
						System.out.print("[/]");
					} else if (fields.get(fieldID).figures.size() != 0) { // field
																			// with
																			// figures
						// TODO print figure stack
						System.out.print("[" + fields.get(fieldID).figures.get(0).name().toString() + "]");
					}
				} else {
					System.out.print("[///////////]");
				}
			}
		}
		System.out.print("\n\n\n ------------------------------- \n\n\n");
	}

	public LascaField getField(String fenPoint) {
		return fields.get(fenPoint);
	}
	
	public void moveFigure(LascaField origin, LascaField destination) {
		FigureType selectedSoldier = origin.topFigure();
		origin.removeTopFigure();
		destination.addFigure(selectedSoldier);
		
		fields.put(origin.id, origin);
		fields.put(destination.id, destination);
	}

}
