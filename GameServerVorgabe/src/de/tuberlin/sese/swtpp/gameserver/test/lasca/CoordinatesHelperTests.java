package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.CoordinatesHelper;

public class CoordinatesHelperTests {

	@Test
	public void corrdinateForString() {
		com.sun.javafx.geom.Point2D a = CoordinatesHelper.corrdinateForString("a5");
		assert(a.x == 1);
		assert(a.y == 5);
		
		com.sun.javafx.geom.Point2D b = CoordinatesHelper.corrdinateForString("g0");
		assert(b.x == 7);
		assert(b.y == 0);
	}

}
