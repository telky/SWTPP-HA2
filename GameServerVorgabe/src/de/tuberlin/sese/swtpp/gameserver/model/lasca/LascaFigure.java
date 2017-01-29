package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

public class LascaFigure {
	public FigureType type;
	public ColorType color;

	public enum FigureType {
		SOLDIER, OFFICER, Empty
	}

	public enum ColorType {
		WHITE, BLACK
	}

	private final String fenString;

	public LascaFigure(String fenString) {
		this.fenString = fenString;

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
	}

	public boolean equalsName(String otherName) {
		// (otherName == null) check is not needed because name.equals(null)
		// returns false
		return fenString.equals(otherName);
	}

	public String toFenString() {
		if (type == FigureType.Empty) {
			return "";
		}
		return this.fenString;
	}
}
