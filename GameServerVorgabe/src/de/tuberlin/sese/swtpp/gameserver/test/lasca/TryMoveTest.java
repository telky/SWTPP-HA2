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
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	@Test
	public void testMoveWhiteSoldier_invalidDirection(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true, false, false);
		assertMove("b4-a3", true, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}

	// Black Soldiers

	@Test
	public void testMoveBlackSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", false);
		assertMove("a5-b4", false, true);
		assertGameState("b,b,b,b/b,b,b/,b,b,b/b,,/w,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}
	@Test
	public void testMoveBlackSoldier_invalidDirection(){
		startGame("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false, false, false);
		assertMove("f4-g5", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	// Black is next but white wants to move - should fail
	@Test
	public void testMoveSoldier_wrongColor(){
		startGame("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false, false, false);
		assertMove("a3-b4", true, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,/,,b/w,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	@Test
	public void testInvalidMoveSoldier() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);	
		assertMove("a1-a1", true, false);		
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}

	@Test
	public void testStrikeSoldier() {
		startGame(",,,/,,/b,,,/w,,/,,,/,,/w,w,w,w", false);
		assertMove("a5-c3", false, true);	
		assertGameState(",,,/,,/,,,/,,/,bw,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeSoldier_Continue() {
		startGame(",,,/,,/b,,,/w,,/,,,/w,,/,w,w,w", false);
		assertMove("a5-c3", false, true);	
		assertGameState(",,,/,,/,,,/,,/,bw,,/w,,/,w,w,w", false, false, false);
		assertMove("c3-d2", false, false);	
		assertGameState(",,,/,,/,,,/,,/,bw,,/w,,/,w,w,w", false, false, false);
		assertMove("c3-a1", false, true);	
		assertGameState(",,,/,,/,,,/,,/,,,/,,/Bww,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeOfficerNormalLength() {
		startGame(",,,/,,/B,,,/w,,/,,,/,,/w,w,w,w", false);
		assertMove("a5-c3", false, true);	
		assertGameState(",,,/,,/,,,/,,/,Bw,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeOfficerLengthTwoFields() {
		startGame(",,,/,,/B,,,/,,/,w,,/,,/w,w,w,w", false);
		assertMove("a5-d2", false, false);	
		assertGameState(",,,/,,/B,,,/,,/,w,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testStrikeOfficerNormalLengthBackwards() {
		startGame(",,,/,,/,,,/,,/,w,,/B,,/w,w,w,w", false);
		assertMove("b2-d4", false, true);
		assertGameState(",,,/,,/,,,/,Bw,/,,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testStrikeSoldierNormalLengthBackwards() {
		startGame(",,,/,,/,,,/,,/,w,,/b,,/,w,w,w", false);
		assertMove("c3-d4", false, false);
		assertGameState(",,,/,,/,,,/,,/,w,,/b,,/,w,w,w", false, false, false);
	}

	@Test
	public void testStrikeSoldierNotEmptyField() {
		startGame(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w", false);	
		assertMove("a5-c3", false, false);	
		assertGameState(",,,/,,/b,,,/w,,/,b,,/,,/w,w,w,w", false, false, false);
	}

	@Test
	public void testStrikeOwnSoldier() {
		startGame(",,,/,,/b,,,/b,,/,,,/,,w/w,w,w,w", false);
		assertMove("a5-c3", false, false);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,w/w,w,w,w", false, false, false);
		assertMove("b4-a3", false, true);
		assertGameState(",,,/,,/b,,,/,,/b,,,/,,w/w,w,w,w", true, false, false);
		assertMove("g1-e3", true, false);
		assertGameState(",,,/,,/b,,,/,,/b,,,/,,w/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testSoldierStrikeEmptyField() {
		startGame(",,,/,,/b,,,/,,/,,,/,,/w,w,w,w", false);
		assertMove("a5-c3", false, false);
		assertGameState(",,,/,,/b,,,/,,/,,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testOfficerStrikeEmptyField() {
		startGame(",,,/,,/B,,,/,,/,,,/,,/w,w,w,w", false);
		assertMove("a5-c3", false, false);
		assertGameState(",,,/,,/B,,,/,,/,,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testMoveWhiteOfficerForward() {
		startGame(",,,/,,/,,,/,,/,,,/,,/W,w,w,w", true);
		assertMove("a1-b2", true, true);
		assertGameState(",,,/,,/,,,/,,/,,,/W,,/,w,w,w", false, true, true);
	}

	@Test 
	public void testMoveWhiteOfficerBackward() {
		startGame(",,,/,,/W,,,/,,/,,,/,,/w,w,w,w", true);
		assertMove("a5-b4", true, true);
		assertGameState(",,,/,,/,,,/W,,/,,,/,,/w,w,w,w", false, true, true);
	}
	
	@Test
	public void testStrikeWithOfficerWhite_ContinueTopRight(){
		startGame(",,,/,,/,W,,/,b,b/,,,/,,/w,w,w,w", true);
		assertMove("c5-e3", true, true);
		assertGameState(",,,/,,/,,,/,,b/,,Wb,/,,/w,w,w,w", true, false, false);
		assertMove("e3-g5", true, true);
		assertGameState(",,,/,,/,,,Wbb/,,/,,,/,,/w,w,w,w", false, true, true);
	}
	@Test
	public void testStrikeWithOfficerWhite_ContinuationNotPossible(){
		startGame(",,,/,,/,W,,/,bb,/,,,/,,/w,w,w,w", true);
		assertMove("c5-e3", true, true);
		assertGameState(",,,/,,/,,,/,b,/,,Wb,/,,/w,w,w,w", false, false, false);
	}
	@Test
	public void testStrikeWithOfficerWhite_ContinueBottomLeft(){
		startGame(",,,/,,/,W,,/,b,/,,,/,b,/w,,w,w", true);
		assertMove("c5-e3", true, true);
		assertGameState(",,,/,,/,,,/,,/,,Wb,/,b,/w,,w,w", true, false, false);
		assertMove("e3-c1", true, true);
		assertGameState(",,,/,,/,,,/,,/,,,/,,/w,Wbb,w,w", false, true, true);
	}
	
	@Test
	public void testStrikeWithOfficerWhite_ContinueBottomRight(){
		startGame(",,,/,,/,W,,/,b,/,,,/,,b/w,,,", true);
		assertMove("c5-e3", true, true);
		assertGameState(",,,/,,/,,,/,,/,,Wb,/,,b/w,,,", true, false, false);
		assertMove("e3-g1", true, true);
		assertGameState(",,,/,,/,,,/,,/,,,/,,/w,,,Wbb", false, true, true);
	}
	
	@Test
	public void testMoveBlackOfficerForward() {
		startGame(",,,/,,/B,,,/,,/,,,/w,w,/w,w,w,w", false);
		assertMove("a5-b4", false, true);
		assertGameState(",,,/,,/,,,/B,,/,,,/w,w,/w,w,w,w", true, false, false);
		assertMove("b2-c3", true, true);
		assertGameState(",,,/,,/,,,/B,,/,w,,/,w,/w,w,w,w", false, false, false);
		assertMove("b4-c3", false, false);
		assertMove("b4-d2", false, false);
		assertGameState(",,,/,,/,,,/B,,/,w,,/,w,/w,w,w,w", false, false, false);
		assertMove("b4-a3", false, true);
		assertGameState(",,,/,,/,,,/,,/B,w,,/,w,/w,w,w,w", true, false, false);

	}

	@Test 
	public void testMoveBlackOfficerBackward() {
		startGame(",,,/,,/,,,/,,/,,,/,,/B,w,w,w", false);
		assertMove("a1-b2", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,/B,,/,w,w,w", true, false, false);
	}

	@Test
	public void testUpgradeToOfficerWhite() {
		startGame(",,,/,w,/b,,,/b,,/,,,/,b,/w,,,w", true);
		assertMove("d6-c7", true, true);
		assertGameState(",W,,/,,/b,,,/b,,/,,,/,b,/w,,,w", false, false, false);
	}

	@Test
	public void testStrikeUpgradeToOfficerBlack() {
		startGame(",,,/,,/b,,,/b,,/,b,,/,w,/w,,,w", false);
		assertMove("c3-e1", false, true);
		assertGameState(",,,/,,/b,,,/b,,/,,,/,,/w,,Bw,w", true, false, false);
	}

	@Test
	public void testStrikeUpgradeToOfficerWhite() {
		startGame(",,,/,b,/b,,w,/b,,/,,,/,b,/w,,,w", true);
		assertMove("e5-c7", true, true);
		assertGameState(",Wb,,/,,/b,,,/b,,/,,,/,b,/w,,,w", false, false, false);
	}


	@Test
	public void testStrikeSoldierStack_completeHistory(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, true);
		assertMove("c5-a3", false, true);
		assertMove("c3-b4", true, true);
		assertMove("a5-c3", false, true);
		assertMove("d2-b4", true, true);
		assertMove("d6-c5", false, true);
		assertGameState("b,b,b,b/b,,b/,b,b,b/wb,,/bw,w,w,w/w,,w/w,w,w,w", true, false, false);
	}
	
	@Test
	// strike with white a stack of black - white - white 
	public void testWhite_StrikeSoldierStack(){
		startGame(",,,/,,/,,,/,,/,bww,,/w,,/w,w,w,w", true);
		assertMove("b2-d4", true, true);
		assertGameState(",,,/,,/,,,/,wb,/,ww,,/,,/w,w,w,w", false, true, true);
	}
	
	@Test
	// strike with black a stack of white - black - black 
	public void testBlack_StrikeSoldierStack(){
		startGame(",,,/,,/,,,/b,,/,wbb,,/,,/,,,", false);
		assertMove("b4-d2", false, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,/,,,", true, true, false);
	}
	
	@Test
	// strike with black a stack of white - black - black 
	public void testBlack_StrikeSoldierStack2(){
		startGame(",,,/,,/,,,/b,,/,wBb,,/,,/,,,", false);
		assertMove("b4-d2", false, true);
		assertGameState(",,,/,,/,,,/,,/,Bb,,/,bw,/,,,", true, true, false);
	}
	
	@Test
	// strike with black a stack of white - black - black 
	public void testBlack_StrikeOfficerStack(){
		startGame(",,,/,,/,,,/B,,/,wbb,,/,,/,,,", false);
		assertMove("b4-d2", false, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,Bw,/,,,", true, true, false);
	}
	
	// should move complete stack
	@Test
	public void testMoveStack(){
		startGame(",,,/,,/,,,/B,,/,bb,,/,,/,,,", false);
		assertMove("c3-b2", false, true);
		assertGameState(",,,/,,/,,,/B,,/,,,/bb,,/,,,", true, true, false);
	}

	
	@Test
	// move figure after
	public void testBlack_moveFreedSoldierAfterStrike(){
		// free black soldiers
		startGame(",,,/,,/,,,/b,,/,wbb,,/,,/,,,w", false);
		assertMove("b4-d2", false, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,/,,,w", true, false, false);
		// white move - meaningless
		assertMove("g1-f2", true, true);
		assertGameState(",,,/,,/,,,/,,/,bb,,/,bw,w/,,,", false, false, false);	
		// move one of the freed soldiers
		assertMove("c3-b2", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,/bb,bw,w/,,,", true, false, false);	
	}
	
	
	// Can Strike
	
	@Test
	public void testMoveBlackSoldier_StrikePossible() {
		startGame(",,,/,,/,b,,/,w,/,,,/,,/w,w,w,w", false);
		assertMove("c5-b4", false, false);	
		assertGameState(",,,/,,/,b,,/,w,/,,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testMoveWhiteSoldier_StrikePossible() {
		startGame(",,,/,,/,,,/,b,/,,w,/,,/w,w,w,w", true);
		assertMove("e3-f4", true, false);	
		assertGameState(",,,/,,/,,,/,b,/,,w,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testMoveWhiteStrikePossible() {
		startGame(",,,/,,/,W,,/,b,/,,,/,,/w,w,w,w", false);
		assertMove("c5-b4", false, false);	
		assertGameState(",,,/,,/,W,,/,b,/,,,/,,/w,w,w,w", false, false, false);
	}
	
	@Test
	public void testMoveBlackOfficerStrikePossible() {
		startGame(",,,/,,/,,,/,w,/,,B,/,,/w,w,w,w", true);
		assertMove("e3-f4", true, false);	
		assertGameState(",,,/,,/,,,/,w,/,,B,/,,/w,w,w,w", true, false, false);
	}
	
	
	@Test
	public void testMove_continueStrikeAfterSuccessfulStrike() {
		startGame(",,,/,,/,b,,/,,/,,B,/,,w/w,w,w,w", true);
		assertMove("f2-d4", true, true);
		assertGameState(",,,/,,/,b,,/,wB,/,,,/,,/w,w,w,w", true, false, false);
		assertMove("d4-b6", true, true);
		assertGameState(",,,/wBb,,/,,,/,,/,,,/,,/w,w,w,w", false, true, true);
	}
	
	@Test
	public void testMove_continueStrikeAfterSuccessfulStrikeAndUpgrade() {
		startGame(",,,/,b,b/,,,w/,,/,,,/,,/w,w,w,w", true);
		assertMove("g5-e7", true, true);
		assertGameState(",,Wb,/,b,/,,,/,,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("e7-c5", true, false); // try to continue strike
		assertGameState(",,Wb,/,b,/,,,/,,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("d6-c5", false, true); // move with black
		assertGameState(",,Wb,/,,/,b,,/,,/,,,/,,/w,w,w,w", true, false, false);

	}
	
	// strike with with white soldier, strike cant be continued, try to strike with white and black
	@Test
	public void testMove_continueStrikeAfterSuccessfulStrike_notPossible() {
		startGame(",,,b/,,/,,,/,,/,,B,/,,w/w,w,w,w", true);
		assertMove("f2-d4", true, true);
		assertGameState(",,,b/,,/,,,/,wB,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("d4-b6", false, false); // not white's turn
		assertGameState(",,,b/,,/,,,/,wB,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("g7-f6", false, true);
		assertGameState(",,,/,,b/,,,/,wB,/,,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	public void testMove_continueStrikeWithOfficerAfterSuccessfulStrike() {
		startGame(",,,/,,/,b,,/,,/,,B,/,,W/w,w,w,w", true);
		assertMove("f2-d4", true, true);
		assertGameState(",,,/,,/,b,,/,WB,/,,,/,,/w,w,w,w", true, false, false);
		assertMove("d4-e6", true, false);
		assertMove("d4-b6", true, true);
		assertGameState(",,,/WBb,,/,,,/,,/,,,/,,/w,w,w,w", false, true, true); 
	}
	
	// strike with with white officer, strike cant be continued, try to strike with white and black
	@Test
	public void testMove_continueStrikeWithOfficerAfterSuccessfulStrike_notPossible() {
		startGame(",,,b/,,/,,,/,,/,,B,/,,W/w,w,w,w", true);
		assertMove("f2-d4", true, true);
		assertGameState(",,,b/,,/,,,/,WB,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("d4-b6", false, false); // not white's turn
		assertGameState(",,,b/,,/,,,/,WB,/,,,/,,/w,w,w,w", false, false, false);
		assertMove("g7-f6", false, true);
		assertGameState(",,,/,,b/,,,/,WB,/,,,/,,/w,w,w,w", true, false, false);
	}
	
	@Test
	// white has no figures, black won
	public void testFinishedGame_NoFiguresWhite(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/,,,", false);
		assertMove("a5-b4", false, true);
		assertGameState("b,b,b,b/b,b,b/,b,b,b/b,,/,,,/,,/,,,", true, true, false);
	}
	
	@Test
	// black has no figures, white won
	public void testFinishedGame_NoFiguresBlack(){
		startGame(",,,/,,/,,,/,,/,,,/b,,/w,w,w,w", true);
		assertMove("a1-c3", true, true);
		assertGameState(",,,/,,/,,,/,,/,wb,,/,,/,w,w,w", false, true, true);
	}
	
	@Test
	public void testFinishedGame_NoMovableFigureBlack(){
		startGame(",,,/,,/,b,,/w,,/,,,/,,/w,w,w,w", true);
		assertMove("b4-d6", true, true);	
		assertGameState(",,,/,wb,/,,,/,,/,,,/,,/w,w,w,w", false, true, true);
	}
	
	@Test
	public void testFinishedGame_cantContinue(){
		startGame(",,,b/,,/,,,/,,/,b,,/,,/w,B,,", false);
		assertMove("c1-b2", false, true);	
		assertGameState(",,,b/,,/,,,/,,/,b,,/B,,/w,,,", true, true, false);		
	}
	
	@Test
	public void testFinishedGame_TryToContinue(){
		startGame(",,,/,,/,b,,/w,,/,,,/,,/w,w,w,w", true);
		assertMove("b4-d6", true, true);	
		assertGameState(",,,/,wb,/,,,/,,/,,,/,,/w,w,w,w", false, true, true);
		assertMove("a1-b2", true, false);	
		assertGameState(",,,/,wb,/,,,/,,/,,,/,,/w,w,w,w", false, true, true);
	}
	
	@Test
	public void testMove_EmptyField(){
		startGame(",,,/,,/,,,/,,/,,,/b,,/w,w,w,w", true);
		assertMove("a7-b6", true, false);
		assertGameState(",,,/,,/,,,/,,/,,,/b,,/w,w,w,w", true, false , false);
	}
	
	@Test
	public void testMove_nullNeighbour(){
		startGame(",,,/,,/,,,/,,/B,,,/w,,/,,,w", false);
		assertMove("a3-c1", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,/,,/,Bw,,w", true, false , false);
	}
	
	@Test
	public void testMove_nullNeighbour2(){
		startGame(",,b,/,,/,b,,/W,,/,,,b/,,/,,,w", true);
		assertMove("b4-d6", true, true);
		assertGameState(",,b,/,Wb,/,,,/,,/,,,b/,,/,,,w", false, false , false);
	}
	
	@Test
	public void testMove_NonDiagonalMove(){
		startGame(",,b,/,,/,b,,/W,,/,,,b/,,/,,,W", true);
		assertMove("g1-e1", true, false);
		assertGameState(",,b,/,,/,b,,/W,,/,,,b/,,/,,,W", true, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsWhiteSoldierLeft(){
		startGame(",,,/,,/,b,,/,w,/,,,b/,,/,,,W", true);
		assertMove("d4-b6", true, true);
		assertGameState(",,,/wb,,/,,,/,,/,,,b/,,/,,,W", false, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsWhiteSoldierLeftEnemy(){
		startGame(",,,/,,/,,,/b,,/,w,,b/,,/,,,W", true);
		assertMove("c3-a5", true, true);
		assertGameState(",,,/,,/wb,,,/,,/,,,b/,,/,,,W", false, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsWhiteSoldierRight(){
		startGame(",,,/,,/,,b,/,w,/,,,b/,,/,,,W", true);
		assertMove("d4-f6", true, true);
		assertGameState(",,,/,,wb/,,,/,,/,,,b/,,/,,,W", false, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsWhiteSoldierRightEnemy(){
		startGame(",,,/,,/,,,/,,b/,,w,b/,,/,,,W", true);
		assertMove("e3-g5", true, true);
		assertGameState(",,,/,,/,,,wb/,,/,,,b/,,/,,,W", false, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsWhiteSoldierRightEnemy2(){
		startGame(",,,/,,b/,,,/,b,/,w,,b/,,/,,,W", true);
		assertMove("c3-e5", true, true);
		assertGameState(",,,/,,b/,,wb,/,,/,,,b/,,/,,,W", true, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsBlackSoldierLeftEnemy(){
		startGame(",,,/,,/,,,/,b,/,w,,b/,,/w,,,W", false);
		assertMove("d4-b2", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,b/bw,,/w,,,W", true, false , false);
	}
	
	@Test
	public void testMove_PossibleDestinationsBlackSoldierRightEnemy(){
		startGame(",,,/,,/,,,/,b,/,,w,b/,,/w,,,W", false);
		assertMove("d4-f2", false, true);
		assertGameState(",,,/,,/,,,/,,/,,,b/,,bw/w,,,W", true, false , false);
	}
	
	@Test
	public void testContinue_AfterStrikeAndUpgrade(){
		startGame(",,,/b,b,b/,w,,/,,/,,w,b/,,/w,,,", true);
		assertMove("c5-e7", true, true);
		game.printBoard();
		assertGameState(",,Wb,/b,,b/,,,/,,/,,w,b/,,/w,,,", false, false , false);
	}
	
	@Test
	public void testGame_Part1(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("c3-b4", true, true);
		assertMove("a5-c3", false, true);
		assertGameState("b,b,b,b/b,b,b/,b,b,b/,,/w,bw,w,w/w,w,w/w,w,w,w", true, false, false);
		assertMove("d2-b4", true, true);
		assertMove("c5-d4", false, true);
		assertGameState("b,b,b,b/b,b,b/,,b,b/wb,b,/w,w,w,w/w,,w/w,w,w,w", true, false, false);
		// e3 - c5 possible strike, other white moves must be invalid
		assertMove("b4-a5", true, false);
		assertMove("g3-f4", true, false);
		assertMove("e3-f4", true, false);
		assertMove("e1-d2", true, false);
		// possible strike
		assertMove("e3-c5", true, true);
		assertMove("b6-d4", false, true);
		assertGameState("b,b,b,b/,b,b/,b,b,b/wb,bw,/w,w,,w/w,,w/w,w,w,w", true, false, false);
		assertMove("c1-d2", true, true);
		assertMove("d4-e3", false, true);
		assertGameState("b,b,b,b/,b,b/,b,b,b/wb,,/w,w,bw,w/w,w,w/w,,w,w", true, false, false);
	}
	
	@Test
	public void testGame_Part2(){
		startGame("b,b,b,b/,b,b/,b,b,b/wb,,/w,w,bw,w/w,w,w/w,,w,w", true);
		assertMove("f2-d4", true, true); 	// white did strike and can continue strike
		assertMove("g5-f4", false,false); 	// black move must fail
		assertMove("g3-f4", true, false);	// white move with other figure must fail
		assertMove("d4-b6", true, true);    // continue strike must succeed
		assertMove("c7-a5", false, true);  
		assertGameState("b,,b,b/bb,b,b/bw,,b,b/wb,,/w,w,w,w/w,w,/w,,w,w", true, false, false);
		assertMove("e3-d4", true, true);
		assertMove("d6-c5", false, true);
		assertGameState("b,,b,b/bb,,b/bw,b,b,b/wb,w,/w,w,,w/w,w,/w,,w,w", true, false, false);
		assertMove("d2-e3", true, false); 	// possible strike, moves should not work
		assertMove("b4-d6", true, true);	
		assertMove("e7-c5", false, true);	// black has strike flow
		assertMove("c5-e3", false, true);
		assertMove("e3-c1", false, true);
		assertGameState("b,,,b/bb,bb,b/bw,,b,b/,,/w,w,,w/w,,/w,Bwww,w,w", true, false, false);
		assertMove("a3-b4", true, true);	
		assertMove("c1-a3", false, true);
		assertMove("a3-c5", false, true);
		assertGameState("b,,,b/bb,bb,b/bw,Bwwwww,b,b/,,/,w,,w/,,/w,,w,w", true, false, false);
		assertMove("a1-b2", true, true);	
		assertMove("c5-d4", false, true);
		assertGameState("b,,,b/bb,bb,b/bw,,b,b/,Bwwwww,/,w,,w/w,,/,,w,w", true, false, false);
	}
	
	@Test
	public void testGame_Part3(){
		startGame("b,,,b/bb,bb,b/bw,,b,b/,Bwwwww,/,w,,w/w,,/,,w,w", true);
		assertMove("e1-f2", true, true);	
		assertMove("e5-f4", false, true);
		assertGameState("b,,,b/bb,bb,b/bw,,,b/,Bwwwww,b/,w,,w/w,,w/,,,w", true, false, false);
		assertMove("c3-e5", true, true);	// white has strike flow
		assertMove("e5-c7", true, true);
		assertMove("a5-b4", false, true);
		assertGameState("b,WBb,,b/bb,b,b/,,,b/bw,wwwww,b/,,,w/w,,w/,,,w", true, false, false);
		assertMove("c7-a5", true, true);	// white has strike flow
		assertMove("a5-c3", true, true);	
		assertMove("f4-e3", false, true);
		assertGameState("b,,,b/b,b,b/,,,b/w,wwwww,/,WBbbb,b,w/w,,w/,,,w", true, false, false);
		assertMove("b4-a5", true, true);	
		assertMove("f6-e5", false, true);
		assertGameState("b,,,b/b,b,/w,,b,b/,wwwww,/,WBbbb,b,w/w,,w/,,,w", true, false, false);
		assertMove("a5-c7", true, true);
		assertMove("a7-b6", false, true);
		assertGameState(",Wb,,b/b,b,/,,b,b/,wwwww,/,WBbbb,b,w/w,,w/,,,w", true, false, false);
		assertMove("c7-a5", true, true);
		assertMove("e3-d2", false, true);
		assertGameState(",,,b/,b,/Wbb,,b,b/,wwwww,/,WBbbb,,w/w,b,w/,,,w", true, false, false);
	}
	
	@Test
	public void testGame_Part4(){
		startGame(",,,b/,b,/Wbb,,b,b/,wwwww,/,WBbbb,,w/w,b,w/,,,w", true);
		assertMove("d4-f6", true, true);
		assertMove("g7-e5", false, true);
		assertGameState(",,,/,b,wwwwb/Wbb,,bw,b/,,/,WBbbb,,w/w,b,w/,,,w", true, false, false);
		assertMove("a5-b6", true, false); // should fail because white can strike
		assertMove("c3-e1", true, true);
		assertMove("d6-c5", false, true);
		assertGameState(",,,/,,wwwwb/Wbb,b,bw,b/,,/,,,w/w,,w/,,WBbbbb,w", true, false, false);
		assertMove("a5-b6", true, true);
		assertMove("g5-f4", false, true);
		assertGameState(",,,/Wbb,,wwwwb/,b,bw,/,,b/,,,w/w,,w/,,WBbbbb,w", true, false, false);
		assertMove("b6-d4", true, true);
		assertMove("e5-c3", false, true);	// black has strike flow
		assertMove("c3-a1", false, true);
		assertGameState(",,,/,,wwwwb/,,,/,bbb,b/,,,w/,,w/BwWw,,WBbbbb,w", true, false, false);
		assertMove("g3-e5", true, true);
		assertMove("a1-b2", false, true);
		assertGameState(",,,/,,wwwwb/,,wb,/,bbb,/,,,/BwWw,,w/,,WBbbbb,w", true, false, false);
		assertMove("e5-d6", true, true);
		assertMove("d4-e3", false, true);
		assertGameState(",,,/,wb,wwwwb/,,,/,,/,,bbb,/BwWw,,w/,,WBbbbb,w", true, false, false);
		assertMove("f2-d4", true, true);
		assertMove("e3-f2", false, true);
		assertGameState(",,,/,wb,wwwwb/,,,/,wb,/,,,/BwWw,,bb/,,WBbbbb,w", true, false, false);
	}
	
	@Test
	public void testGame_Part5(){
		startGame(",,,/,wb,wwwwb/,,,/,wb,/,,,/BwWw,,bb/,,WBbbbb,w", true);
		assertMove("g1-e3", true, true);
		assertMove("f2-g1", false, true);
		assertGameState(",,,/,wb,wwwwb/,,,/,wb,/,,wb,/BwWw,,/,,WBbbbb,B", true, false, false);
		assertMove("d6-c7", true, true);
		assertMove("b2-c3", false, true);
		assertGameState(",Wb,,/,,wwwwb/,,,/,wb,/,BwWw,wb,/,,/,,WBbbbb,B", true, false, false);
		assertMove("f6-e7", true, true);
		assertMove("c3-e5", false, true);
		assertGameState(",Wb,Wwwwb,/,,/,,BwWww,/,b,/,,wb,/,,/,,WBbbbb,B", true, false, false);
		assertMove("e3-f5", true, false); // strike possible
		assertMove("e3-c5", true, true);
		assertMove("e5-f4", false, true);
		assertGameState(",Wb,Wwwwb,/,,/,wbb,,/,,BwWww/,,,/,,/,,WBbbbb,B", true, false, false);
		assertMove("c7-d6", true, true);
		assertMove("f4-e3", false, true);
		assertGameState(",,Wwwwb,/,Wb,/,wbb,,/,,/,,BwWww,/,,/,,WBbbbb,B", true, false, false);
		assertMove("e7-f6", true, true);
		assertMove("e3-f4", false, true);
		assertMove("d6-e5", true, true);
		assertMove("f4-d6", false, true);
		assertMove("d6-b4", false, true);
		assertGameState(",,,/,,Wwwwb/,bb,b,/BwWwwWw,,/,,,/,,/,,WBbbbb,B", true, false, false);
	}
	
	@Test
	public void testGame_Part6(){
		startGame(",,,/,,Wwwwb/,bb,b,/BwWwwWw,,/,,,/,,/,,WBbbbb,B", true);
		assertMove("f6-d4", true, true);
		assertMove("d4-b6", true, true);
		assertMove("g1-f2", false, true); // fails, tries to continue strike by going back
		assertMove("e1-g3", true, true);
		assertMove("c5-d4", false, true);
		assertGameState(",,,/Wwwwbbb,,/,,,/BwWwwWw,b,/,,,WBbbbbB/,,/,,,", true,false, false);
		assertMove("g3-f4", true, true);
		assertMove("d4-c3", false, true);
		assertGameState(",,,/Wwwwbbb,,/,,,/BwWwwWw,,WBbbbbB/,b,,/,,/,,,", true, false, false);
		assertMove("b6-c7", true, true);
		assertMove("b4-c5", false, true);
		assertGameState(",Wwwwbbb,,/,,/,BwWwwWw,,/,,WBbbbbB/,b,,/,,/,,,", true, false, false);
		assertMove("f4-e5", true, true);
		assertMove("c5-d6", false, true);
		assertGameState(",Wwwwbbb,,/,BwWwwWw,/,,WBbbbbB,/,,/,b,,/,,/,,,", true, false, false);
		assertMove("e5-d4", true, true);
		assertMove("d6-c5", false, true);
		assertGameState(",Wwwwbbb,,/,,/,BwWwwWw,,/,WBbbbbB,/,b,,/,,/,,,", true, false, false);
		assertMove("d4-b6", true, true);
		assertMove("c3-b2", false, true);
		assertGameState(",Wwwwbbb,,/WBbbbbBB,,/,wWwwWw,,/,,/,,,/b,,/,,,", true, false, false);
		assertMove("c5-d6", true, true);
		assertMove("b2-c1", false, true);
		assertGameState(",Wwwwbbb,,/WBbbbbBB,wWwwWw,/,,,/,,/,,,/,,/,B,,", true, false, false);
	}
	
	
	@Test
	public void testProblemOne(){ // white wins by capture
		startGame("Bw,,,b/,BB,/w,,,www/b,bw,/,B,,w/,,wb/,WWb,,Wbb", true);
		assertGameState("Bw,,,b/,BB,/w,,,www/b,bw,/,B,,w/,,wb/,WWb,,Wbb", true, false, false);
		assertMove("g5-f6", true, true);
		assertMove("g7-e5", false, true);
		assertMove("g3-f4", true, true);
		assertMove("e5-g3", false, true);
		assertMove("g3-e1", false, true);
		assertMove("g1-e3", true, true);
		assertMove("e3-c5", true, true);
		assertMove("c5-e7", true, true);
		assertMove("c3-e5", false, true);
		assertGameState("Bw,,WbbbbB,/,B,ww/w,,Bw,/b,,/,,,/,,/,WWb,Bwww,", false, false, false); 
		assertMove("e5-g7", false, true);
		assertMove("e7-c5", true, true);
		assertMove("c5-a3", true, true);
		assertMove("g7-e5", false, true);
		assertMove("a5-b6", true, true);
		assertMove("a7-c5", false, true);
		assertMove("c1-d2", true, true);
		assertMove("e1-c3", false, true);
		assertMove("d2-b4", true, true);
		assertMove("b4-d6", true, true);
		assertMove("d6-f4", true, true);
		assertGameState(",,,/,,/,ww,www,/,,WbBBB/WbbbbBBb,wwwW,,/,,/,,,", false, true, true);
	}
	

}
