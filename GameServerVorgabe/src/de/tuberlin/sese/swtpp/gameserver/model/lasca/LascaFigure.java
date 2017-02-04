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

		switch (fenString) {
		case "b":
			type = FigureType.SOLDIER;
			color = ColorType.BLACK;
			break;
		case "B":
			color = ColorType.BLACK;
			type = FigureType.OFFICER;
			break;
		case "w":
			type = FigureType.SOLDIER;
			color = ColorType.WHITE;
			break;
		case "W":
			type = FigureType.OFFICER;
			color = ColorType.WHITE;
			break;
		default:
			type = FigureType.Empty;
		}

		// TODO remove empty state
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
