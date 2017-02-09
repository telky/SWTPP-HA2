package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.FigureType;

public class LascaField implements Serializable {

	int row;
	int col;

	String id;

	Stack<LascaFigure> figures;
	
	
    public LascaField neighbourFieldTopLeft = null;
    public LascaField neighbourFieldTopRight = null;
    public LascaField neighbourFieldBottomRight = null;
    public LascaField neighbourFieldBottomLeft = null;

	public LascaField(int row, int col) {
		this.row = row;
		this.col = col;
		this.id = this.calculateID();

		this.figures = new Stack<LascaFigure>();
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
 
	public LascaFigure getTopFigure() {
		return figures.peek();
	}
	
	public LascaFigure popTopFigure(){
		return figures.pop();
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
		figures.push(figure);
	}

	public void removeAllFigures() {
		figures.clear();
	}
	
	public Point2D getCoordinate() {
		return new Point2D(col, row);
	}

}
