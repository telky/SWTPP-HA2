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
		game.printBoard();
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}

	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(game.tryMove(move, whitePlayer), expectedResult);
		else 
			assertEquals(game.tryMove(move, blackPlayer), expectedResult);
	}

	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		System.out.print(game.getState());
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


	// White Soldiers

	@Test
	public void testMoveWhiteSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertMove("a3-b4", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w b", false, false, false);
	}

	@Test
	public void testMoveWhiteSoldier_invalidDirection(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w w", true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w w", true, false, false);
		assertMove("b4-a3", true, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w w", true, false, false);
	}

	// Black Soldiers

	@Test
	public void testMoveBlackSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w b", false);
		assertMove("a5-b4", false, true);
		assertGameState("b,b,b,b/b,b,b/,b,b,b/b,,/w,w,w,w/w,w,w/w,w,w,w w", true, false, false);
	}
	@Test
	public void testMoveBlackSoldier_invalidDirection(){
		startGame("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false, false, false);
		assertMove("f4-g5", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false, false, false);
	}

	// Black is next but white wants to move - should fail
	@Test
	public void testMoveSoldier_wrongColor(){
		startGame("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false, false, false);
		assertMove("a3-b4", true, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w b", false, false, false);
	}

	@Test
	public void testInvalidMoveSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);	
		assertMove("a1-a1", true, false);		
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true, false, false);
	}

	@Test
	public void testStrikeSoldier() {
		startGame(",,,/,,/b,,,/w,,/,,,/,,/w,w,w,w b", false);
		assertMove("a5-c3", false, true);	
		assertGameState(",,,/,,/,,,/,,/,bw,,/,,/w,w,w,w w", true, false, false);
	}

	@Test
	public void testStrikeSoldierNotEmptyField() {
		startGame(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w b", false);	
		assertMove("a5-c3", false, false);	
		assertGameState(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w b", false, false, false);
	}

	@Test
	public void testStrikeOwnSoldier() {
		startGame(",,,/,,/b,,,/b,,/,,,/,,/w,w,w,w b", false);
		assertMove("a5-c3", false, false);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,w,w,w b", false, false, false);
	}

	@Test
	public void testUpgradeToOfficerBlack() {
		startGame(",,,/,,/b,,,/b,,/,,,/,b,/w,,,w b", false);
		assertMove("d2-e1", false, true);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,,B,w w", true, false, false);
	}
	public void testMoveWhiteOfficerForward() {
		startGame(",,,/,,/,,,/,,/,,,/,,/W,w,w,w w", true);
		assertMove("a1-b2", true, true);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,,B,w w", true, false, false);
	}

	@Test 
	public void testMoveWhiteOfficerBackward() {
		startGame(",,,/,,/W,,,/,,/,,,/,,/w,w,w,w w", true);
		assertMove("a5-b4", true, true);
		assertGameState(",,,/,,/,,,/W,,/,,,/,,/w,w,w,w b", false, false, false);
	}

	@Test
	public void testMoveBlackOfficerForward() {
		startGame(",,,/,,/B,,,/,,/,,,/,,/w,w,w,w b", false);
		assertMove("a5-b4", false, true);
		assertGameState(",,,/,,/,,,/B,,/,,,/,,/w,w,w,w w", true, false, false);
	}

	@Test 
	public void testMoveBlackOfficerBackward() {
		startGame(",,,/,,/,,,/,,/,,,/,,/B,w,w,w b", false);
		assertMove("a1-b2", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,/B,,/,w,w,w w", true, false, false);
	}


	@Test
	public void testUpgradeToOfficerWhite() {
		startGame(",,,/,w,/b,,,/b,,/,,,/,b,/w,,,w w", true);
		assertMove("d6-c7", true, true);
		assertGameState(",W,,/,,/b,,,/b,,/,,,/,b,/w,,,w b", false, false, false);
	}

	@Test
	public void testStrikeUpgradeToOfficerBlack() {
		startGame(",,,/,,/b,,,/b,,/,b,,/,w,/w,,,w b", false);
		game.printBoard();
		assertMove("c3-e1", false, true);
		game.printBoard();
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,,Bw,w w", true, false, false);
	}

	@Test
	public void testStrikeUpgradeToOfficerWhite() {
		startGame(",,,/,b,/b,,w,/b,,/,,,/,b,/w,,,w w", true);
		assertMove("e5-c7", true, true);
		assertGameState(",Wb,,/,,/b,,,/b,,/,,,/,b,/w,,,w b", false, false, false);
	}


	@Test
	public void testStrikeSoldierStack_completeHistory(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w", true);
		assertMove("a3-b4", true, true);
		assertMove("c5-a3", false, true);
		assertMove("c3-b4", true, true);
		assertMove("a5-c3", false, true);
		assertMove("d2-b4", true, true);
		assertMove("d6-c5", false, true);
		assertGameState("b,b,b,b/b,,b/,b,b,b/wb,,/bw,w,w,w/w,,w/w,w,w,w w", true, false, false);
	}
	
	@Test
	// strike with white a stack of black - white - white 
	public void testWhite_StrikeSoldierStack(){
		startGame(",,,/,,/,,,/,,/,bww,,/w,,/w,w,w,w w", true);
		assertMove("b2-d4", true, true);
		game.printBoard();
		assertGameState(",,,/,,/,,,/,wb,/,ww,,/,,/w,w,w,w b", false, false, false);
	}
	
	@Test
	// strike with black a stack of white - black - black 
	public void testBlack_StrikeSoldierStack(){
		startGame(",,,/,,/,,,/b,,/,wbb,,/,,/,,, b", false);
		game.printBoard();
		assertMove("b4-d2", false, true);
		game.printBoard();
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,/,,, w", true, false, false);
		// TODO failing because of wrong next
	}
	
	@Test
	// move figure after
	public void testBlack_moveFreedSoldierAfterStrike(){
		// free black soldiers
		startGame(",,,/,,/,,,/b,,/,wbb,,/,,/,,,w b", false);
		assertMove("b4-d2", false, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,/,,,w w", true, false, false);
		// white move - meaningless
		assertMove("g1-f2", true, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,w/,,, b", false, false, false);	
		// move one of the freed soldiers
		assertMove("c3-b2", false, true);
		assertGameState(",,,/,,/,,,/,,/,b,,/b,bw,w/,,, w", true, false, false);	
	}
	
	@Test
	public void testBlack_moveCapturedSoldier(){
		// TODO try to move a soldier while captured
	}
	
	// Can Strike
	
	@Test
	public void testMoveBlackSoldier_StrikePossible() {
		startGame(",,,/,,/,b,,/,w,/,,,/,,/w,w,w,w b", false);
		assertMove("c5-b4", false, false);	
		assertGameState(",,,/,,/,b,,/,w,/,,,/,,/w,w,w,w b", false, false, false);
	}
	
	@Test
	public void testMoveWhiteSoldier_StrikePossible() {
		startGame(",,,/,,/,,,/,b,/,,w,/,,/w,w,w,w w", true);
		assertMove("e3-f4", true, false);	
		assertGameState(",,,/,,/,,,/,b,/,,w,/,,/w,w,w,w w", true, false, false);
	}
	
	@Test
	public void testMoveWhiteStrikePossible() {
		startGame(",,,/,,/,W,,/,b,/,,,/,,/w,w,w,w b", false);
		assertMove("c5-b4", false, false);	
		assertGameState(",,,/,,/,W,,/,b,/,,,/,,/w,w,w,w b", false, false, false);
	}
	
	@Test
	public void testMoveBlackOfficerStrikePossible() {
		startGame(",,,/,,/,,,/,w,/,,B,/,,/w,w,w,w w", true);
		assertMove("e3-f4", true, false);	
		assertGameState(",,,/,,/,,,/,w,/,,B,/,,/w,w,w,w w", true, false, false);
	}
	
	
	@Test
	public void testMove_continueStrikeAfterSuccessfulStrike() {
		startGame(",,,/,,/,b,,/,,/,,B,/,,w/w,w,w,w w", true);
		assertMove("f2-d4", true, true);
		assertGameState(",,,/,,/,b,,/,wB,/,,,/,,/w,w,w,w w", true, false, false);
		assertMove("f4-b6", true, true);
		assertGameState(",,,/wBb,,/,,,/,wB,/,,,/,,/w,w,w,w b", false, false, false);
	}
}
