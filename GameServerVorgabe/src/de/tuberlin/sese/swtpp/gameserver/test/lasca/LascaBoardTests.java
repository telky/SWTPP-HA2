package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaGame;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaBoard;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaField;

public class LascaBoardTests {

	LascaBoard board = null;
	
	@Before
	public void setUp() throws Exception{
		board = new LascaBoard("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w");
	}
	
	
	@Test
	public void testValidStartingBoard_shouldPass(){
		String startingBoard = "b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w";
		String fenCurrentBoard = board.toFenString();
		assertEquals(startingBoard, fenCurrentBoard);	
	}
	
	@Test
	public void testValidStartingBoard_shouldFail(){
		String invalidBoard = "b,b,b,b/b,b,b/b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w";
		String fenCurrentBoard = board.toFenString();
		assertNotEquals(invalidBoard, fenCurrentBoard);
	}
	
	
	
	@Test
	public void testInvalidBoard_TooManyFigures(){
		// how should invalid state be tested with the given function?
	}

}
