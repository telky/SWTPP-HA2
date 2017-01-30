package de.tuberlin.sese.swtpp.gameserver.test.lasca;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaGame;

public class TryMoveTest {

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
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(game.tryMove(move, whitePlayer), expectedResult);
		else 
			assertEquals(game.tryMove(move, blackPlayer), expectedResult);
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(game.getState(), expectedBoard);
		assertEquals(game.isWhiteNext(), whiteNext);

		assertEquals(game.isFinished(), finished);
		if (game.isFinished()) {
			assertEquals(whitePlayer.isWinner(), whiteWon);
			assertEquals(blackPlayer.isWinner(), !whiteWon);
		}
	}
	
	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testMoveSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);	
		assertMove("a1-a1", true, false);		
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeSoldier() {
		startGame(",,,/,,/b,,,/w,,/,,,/,,/w,w,w,w", false);	
		assertMove("a5-b4", false, true);	
		assertGameState(",,,/,,/,,,/,,/,wb,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeSoldierNotEmptyField() {
		startGame(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w", false);	
		assertMove("a5-b4", false, false);	
		assertGameState(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testStrikeOwnSoldier() {
		startGame(",,,/,,/b,,,/b,,/,,,/,,/w,w,w,w", false);
		assertMove("a5-b4", false, false);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testUpgradeToSoldierBlack() {
		startGame(",,,/,,/b,,,/b,,/,,,/,b,/w,,,w", false);
		assertMove("d2-e1", false, true);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,,B,w", false, false, false);
	}
	
	@Test
	public void testUpgradeToSoldierWhite() {
		startGame(",,,/,w,/b,,,/b,,/,,,/,b,/w,,,w", true);
		assertMove("d6-c7", true, true);
		assertGameState(",W,,/,,/b,,,/b,,/,,,/,b,/w,,,w", true, false, false);
	}
	

	//TODO: implement test cases of same kind as example here
	
}
