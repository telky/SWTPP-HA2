package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaMove;

public class LascaMoveTests {

	@Test
	public void testConstructor() {
		LascaMove move = new LascaMove("a6-b7", new Player(null, null));
		assert(move.origin.x == 1);
		assert(move.origin.y == 6);
		
		assert(move.destination.x == 2);
		assert(move.destination.y == 7);
	}

}
