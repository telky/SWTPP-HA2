package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

public class LascaFigure implements Serializable{
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
		String fenString = null;
		if(this.type.equals(FigureType.OFFICER)){
			if (color == ColorType.WHITE) {
				fenString = "W";
			} else {
				fenString = "B";
			}
		} else{
			if (color == ColorType.WHITE) {
				fenString = "w";
			} else {
				fenString = "b";
			}
		}
		return fenString;
	}

	public void upgrade() {
		this.type = FigureType.OFFICER;
	}
}
