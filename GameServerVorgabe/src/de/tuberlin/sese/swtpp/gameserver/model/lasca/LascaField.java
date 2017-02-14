package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import com.sun.javafx.geom.Point2D;

import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaFigure.FigureType;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaGame.MoveType;

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
		return this.figures.size()==0;
	}

	public void addFigure(LascaFigure figure) {
		figures.add(figure);
	}

	public void addLastFigure(LascaFigure figure){
		figures.addLast(figure);
	}

	public LascaField getNeighbourByMoveType(MoveType moveType){
		LascaField neighbour = null;
		if(moveType.equals(MoveType.BOTTOMLEFT)){
			neighbour = this.neighbourFieldBottomLeft;
		} else if(moveType.equals(MoveType.BOTTOMRIGHT)){
			neighbour = this.neighbourFieldBottomRight;
		} else if(moveType.equals(MoveType.TOPLEFT)){
			neighbour = this.neighbourFieldTopLeft;
		} else if(moveType.equals(MoveType.TOPRIGHT)){
			neighbour = this.neighbourFieldTopRight;
		}
		return neighbour;
	}

	public void removeAllFigures() {
		figures.clear();
	}

	public Point2D getCoordinate() {
		return new Point2D(col, row);
	}

	public Boolean hasOpponentFigure(LascaField field){
		return this.getTopFigure().color != field.getTopFigure().color;
	}

}
