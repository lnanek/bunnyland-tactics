package name.nanek.gdwprototype.client;

import junit.framework.TestCase;


public class GameAnchorTest extends TestCase {
	
	public void testGetIdFromAnchor() {
		String anchor = "game_1_My_Awesome_Game";
		Long result = GameAnchor.getIdFromAnchor(anchor);
		System.out.println("Result is: " + result);
		TestCase.assertEquals(new Long(1), result);
	}

}
