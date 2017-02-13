package de.tuberlin.sese.swtpp.gameserver.model.lasca;

public class LascaFigure {
	public FigureType type;
	public ColorType color;

	public enum FigureType {
		SOLDIER, OFFICER, Empty
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
		switch (type) {
		case OFFICER:
			if (color == ColorType.WHITE) {
				return "W";
			} else {
				return "B";
			}
		case SOLDIER:
			if (color == ColorType.WHITE) {
				return "w";
			} else {
				return "b";
			}
		default:
			return "";
		}
	}

	public void upgrade() {
		this.type = FigureType.OFFICER;
	}
}
