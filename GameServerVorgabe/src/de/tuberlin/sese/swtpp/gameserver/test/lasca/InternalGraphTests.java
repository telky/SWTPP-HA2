package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaGame;

public class InternalGraphTests {
	/*******************************************
	 * !!!!!!!!! Internal Tests !!!!!!!!!!!!
	 *******************************************/
	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");

	Player whitePlayer = null;
	Player blackPlayer = null;
	LascaGame game = null;
	GameController controller;

	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();

		int gameID = controller.startGame(user1, "");

		game = (LascaGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);
	}


	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		game.setState(initialBoard);
		game.printBoard();
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	@Test
	public void testGraph_topLeftCorner(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("a7").neighbourFieldTopLeft, null);
		assertEquals(game.getBoard().getField("a7").neighbourFieldTopRight, null);
		assertEquals(game.getBoard().getField("a7").neighbourFieldBottomLeft, null);
		assertEquals(game.getBoard().getField("a7").neighbourFieldBottomRight, game.getBoard().getField("b6"));
	}
	@Test
	public void testGraph_topRightCorner(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("g7").neighbourFieldTopLeft, null);
		assertEquals(game.getBoard().getField("g7").neighbourFieldTopRight, null);
		assertEquals(game.getBoard().getField("g7").neighbourFieldBottomLeft, game.getBoard().getField("f6"));
		assertEquals(game.getBoard().getField("g7").neighbourFieldBottomRight, null);
	}
	@Test
	public void testGraph_BottomRightCorner(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("g1").neighbourFieldTopLeft, game.getBoard().getField("f2"));
		assertEquals(game.getBoard().getField("g1").neighbourFieldTopRight, null);
		assertEquals(game.getBoard().getField("g1").neighbourFieldBottomLeft, null);
		assertEquals(game.getBoard().getField("g1").neighbourFieldBottomRight, null);
	}
	@Test
	public void testGraph_BottomLeftCorner(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("a1").neighbourFieldTopLeft, null);
		assertEquals(game.getBoard().getField("a1").neighbourFieldTopRight, game.getBoard().getField("b2"));
		assertEquals(game.getBoard().getField("a1").neighbourFieldBottomLeft, null);
		assertEquals(game.getBoard().getField("a1").neighbourFieldBottomRight, null);
	}
	@Test
	public void testGraph_centralField(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("d4").neighbourFieldTopLeft, game.getBoard().getField("c5"));
		assertEquals(game.getBoard().getField("d4").neighbourFieldTopRight, game.getBoard().getField("e5"));
		assertEquals(game.getBoard().getField("d4").neighbourFieldBottomLeft, game.getBoard().getField("c3"));
		assertEquals(game.getBoard().getField("d4").neighbourFieldBottomRight, game.getBoard().getField("e3"));
	}
	
	@Test
	public void testGraph_leftBorder(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertEquals(game.getBoard().getField("a3").neighbourFieldTopLeft, null);
		assertEquals(game.getBoard().getField("a3").neighbourFieldTopRight, game.getBoard().getField("b4"));
		assertEquals(game.getBoard().getField("a3").neighbourFieldBottomLeft, null);
		assertEquals(game.getBoard().getField("a3").neighbourFieldBottomRight, game.getBoard().getField("b2"));
	}
	
}
