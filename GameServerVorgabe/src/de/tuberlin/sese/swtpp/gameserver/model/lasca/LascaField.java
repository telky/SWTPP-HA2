package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.FigureType;

public class LascaField implements Serializable {

	int row;
	int col;

	String id;
	
	Deque<LascaFigure> figures;
	
	
    public LascaField neighbourFieldTopLeft = null;
    public LascaField neighbourFieldTopRight = null;
    public LascaField neighbourFieldBottomRight = null;
    public LascaField neighbourFieldBottomLeft = null;

	public LascaField(int row, int col) {
		this.row = row;
		this.col = col;
		this.id = this.calculateID();

		this.figures = new LinkedList<LascaFigure>();
	}
	
	public String getFiguresOnField() {
		String figuresOnField = "";
		for (LascaFigure figure : this.figures) {
			figuresOnField = figuresOnField + (figure.toFenString());
		}
		return figuresOnField;
	}

	private String calculateID() {
		Point2D tmp = getCoordinate();
		return CoordinatesHelper.fenStringForCoordinate(tmp);
	}
 
	// get first element without removing
	public LascaFigure getTopFigure() {
		return figures.element();
	}
	
	// get first element and remove it
	public LascaFigure removeTopFigure(){
		return figures.remove();
	}

	public Boolean isEmpty() {
		for (LascaFigure figure : figures) {
			if (figure.type != FigureType.Empty) {
				return false;
			}
		}
		return true;
	}

	public void addFigure(LascaFigure figure) {
		figures.add(figure);
	}
	
	public void addLastFigure(LascaFigure figure){
		figures.addLast(figure);
	}
	
	

	public void removeAllFigures() {
		figures.clear();
	}
	
	public Point2D getCoordinate() {
		return new Point2D(col, row);
	}

}
