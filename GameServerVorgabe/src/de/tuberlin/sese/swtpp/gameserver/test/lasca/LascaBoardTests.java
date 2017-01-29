package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaBoard;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaField;

public class LascaBoardTests {

	@Test
	public void testSize() {
		String fen = "b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w";
		LascaBoard board = new LascaBoard(fen);
		
		assertEquals(board.getFields().size(), 25);
		assertEquals(board.toFenString(), fen);
	}
	
	@Test
	public void testParseColumn(){
		String fen = "Wb,,b,b/b,bb,b/b,,bw,b/,,/w,w,,/w,bww,w/w,,w,w w";
		LascaBoard board = new LascaBoard(fen);
		assertEquals(board.toFenString(),fen);
	}
	@Test
	public void testInvalidBoard_TooManyFigures(){
		String fen = "b,b,b,b/b,b,b/b,b,b,b/b,b,/w,w,w,w/w,w,w/w,w,w,w";
		// how should invalid state be tested with the given function?
	}

}
