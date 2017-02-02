//package de.tuberlin.sese.swtpp.gameserver.test.lasca;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import de.tuberlin.sese.swtpp.gameserver.control.GameController;
//import de.tuberlin.sese.swtpp.gameserver.model.Player;
//import de.tuberlin.sese.swtpp.gameserver.model.User;
//import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaBoard;
//import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaField;
//import de.tuberlin.sese.swtpp.gameserver.model.lasca.LascaGame;
//
//
//// TODO: internal test, not for use with code coverage
//public class LascaBoardTests {
//	
//	User user1 = new User("Alice", "alice");
//	User user2 = new User("Bob", "bob");
//	
//	Player whitePlayer = null;
//	Player blackPlayer = null;
//	LascaGame game = null;
//	GameController controller;
//
//	@Before
//	public void setUp() throws Exception {
//		controller = GameController.getInstance();
//		controller.clear();
//		
//		int gameID = controller.startGame(user1, "");
//		
//		game = (LascaGame) controller.getGame(gameID);
//		whitePlayer = game.getPlayer(user1);
//	}
//	
//	@Test
//	// TODO: internal test, not for use with code coverage
//	public void testValidStartingBoard_shouldSucceed() {
//		String validBoard = "b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w";
//		String fenCurrentBoard = game.getState();
//		assertEquals(validBoard, fenCurrentBoard);
//	}
//    
//    @Test
//	// TODO: internal test, not for use with code coverage
//    public void testValidStartingBoard_shouldFail(){
//        String invalidBoard = "b,b,b,b/b,b,b/b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w";
//        String fenCurrentBoard = game.getState();
//        assertNotEquals(invalidBoard, fenCurrentBoard);
//    }
//	
//	@Test
//	// TODO: internal test, not for use with code coverage
//	public void testParseColumn(){
//		String fen = "Wb,,b,b/b,bb,b/b,,bw,b/,,/w,w,,/w,bww,w/w,,w,w w";
//		LascaBoard board = new LascaBoard(fen);
//		System.out.print(game.getState());
//		board.printBoard();
//		assertEquals(game.getState(),fen);
//	}
//}
