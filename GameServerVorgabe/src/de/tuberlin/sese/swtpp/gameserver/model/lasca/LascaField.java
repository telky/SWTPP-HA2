package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.*;

import com.sun.javafx.geom.Point2D;

public class LascaField implements Serializable {
		
	int row;
	int col;
	
	String id;
	
	ArrayList<FigureType> figures;
	
	List<LascaField> neighbourFieldsBlackDirection;
	List<LascaField> neighbourFieldsWhiteDirection;
	
	public LascaField(int row, int col){
		this.row = row;
		this.col = col;
		this.id = this.toFenString();
		this.neighbourFieldsBlackDirection = new ArrayList<LascaField>();
		this.neighbourFieldsWhiteDirection =  new ArrayList<LascaField>();
		
		this.figures = new ArrayList<FigureType>();
	}
	
	public String toFenString() {
		Point2D tmp = new Point2D(row, col);
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}
	
	public FigureType topFigure() {
		return figures.get(figures.size()-1);
	}
	
	public Boolean isEmpty() {
		return figures.isEmpty();
	}
	
	public void removeTopFigure() {
		figures.remove(figures.size()-1);
	}
	
	public void addFigure(FigureType figure) {
		figures.add(figure);
	}

}
