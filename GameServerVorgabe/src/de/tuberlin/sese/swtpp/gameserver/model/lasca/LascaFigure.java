package de.tuberlin.sese.swtpp.gameserver.model.lasca;

public class LascaFigure {
	public FigureType type;
	public ColorType color;

	public enum FigureType {
		SOLDIER, OFFICER
	}

	public enum ColorType {
		WHITE, BLACK
	}

	public LascaFigure(String fenString) {
		if(fenString.equals("b")){
			type = FigureType.SOLDIER;
			color = ColorType.BLACK;
		} else if(fenString.equals("B")){
			color = ColorType.BLACK;
			type = FigureType.OFFICER;
		} else if(fenString.equals("w")){
			type = FigureType.SOLDIER;
			color = ColorType.WHITE;
		} else if(fenString.equals("W")){
			type = FigureType.OFFICER;
			color = ColorType.WHITE;
		}
	}

	public String toFenString() {
		String returnString = null;
		if(this.type.equals(FigureType.OFFICER)){
			if (color == ColorType.WHITE) {
				returnString = "W";
			} else {
				returnString = "B";
			}
		} else if(this.type.equals(FigureType.SOLDIER)){
			if (color == ColorType.WHITE) {
				returnString = "w";
			} else {
				returnString = "b";
			}
		}
		return returnString;
	}

	public void upgrade() {
		this.type = FigureType.OFFICER;
	}
}
