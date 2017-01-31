package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.FigureType;

public class LascaField implements Serializable {
		
	int row;
	int col;
	
	String id;
	
	ArrayList<LascaFigure> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	public LascaField(int row, int col){
		this.row = row;
		this.col = col;
		this.id = this.calculateID();
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		
		this.figures = new ArrayList<LascaFigure>();
	}
	
	public String getFiguresOnField() {
		String figuresOnField = "";
		for(LascaFigure figure: this.figures){
            figuresOnField=figuresOnField+(figure.toFenString());
        }
		return figuresOnField;
	}
	
	private String calculateID(){
		Point2D tmp = new Point2D(col, row);
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}
	
	public LascaFigure topFigure() {
		return figures.get(0);
	}
	
	public Boolean isEmpty() {
		for (LascaFigure figure : figures) {
			if (figure.type != FigureType.Empty) {
				return false;
			}
		}
		return true;
	}
	
	public void removeTopFigure() {
		figures.remove(0);
	}
	
	public void addFigure(LascaFigure figure) {
		figures.add(0, figure);
	}

	public void removeAllFigures() {
		ArrayList<LascaFigure> newFigures = new ArrayList<LascaFigure>();;
		for (LascaFigure figure : figures) {
			newFigures.add(new LascaFigure(""));
		}
		figures = newFigures;
	}

}
