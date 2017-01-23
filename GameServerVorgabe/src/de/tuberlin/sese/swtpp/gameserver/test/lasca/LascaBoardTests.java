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

}
