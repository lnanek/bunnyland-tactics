package name.nanek.gdwprototype.client.controller.support;

import name.nanek.gdwprototype.client.controller.screen.CreateGameScreenController;
import name.nanek.gdwprototype.client.controller.screen.CreateMapScreenController;
import name.nanek.gdwprototype.client.controller.screen.CreditsScreenController;
import name.nanek.gdwprototype.client.controller.screen.GameScreenController;
import name.nanek.gdwprototype.client.controller.screen.MenuScreenController;
import name.nanek.gdwprototype.client.controller.screen.OptionsScreenController;
import name.nanek.gdwprototype.client.controller.screen.ScreenController;
import name.nanek.gdwprototype.client.controller.screen.StartGameScreenController;
import name.nanek.gdwprototype.client.controller.screen.StartObservationScreenController;
import name.nanek.gdwprototype.client.controller.screen.StrategyScreenController;

/**
 * Converts between history tokens and screen controllers.
 * 
 * @author Lance Nanek
 *
 */
public class ScreenControllers {
	//XXX We can't just have a data structure with the screen controller class names 
	//because GWT doesn't support Class#newInstance.
	
	public enum Screen {
		CREDITS,
		GAME,
		START_GAME,
		START_OBSERVATION,
		CREATE_GAME,
		CREATE_MAP,
		OPTIONS,
		STRATEGY,
		MENU, //XXX Menu has to be last, because it has a special token of nothing as the default, which matches the prefix of anything.
	}
	
	public static String getHistoryToken(Screen screen) {
		if ( Screen.MENU == screen ) {
			return "";
		}
		return screen.toString().toLowerCase();
	}
	
	public static ScreenController getController(Screen screen) {
		if ( null == screen ) {
			return new MenuScreenController();
		}
		
		switch ( screen ) {
			case CREDITS:
				return new CreditsScreenController();
			case GAME:
				return new GameScreenController();
			case START_GAME:
				return new StartGameScreenController();
			case START_OBSERVATION:
				return new StartObservationScreenController();
			case CREATE_GAME:
				return new CreateGameScreenController();
			case CREATE_MAP:
				return new CreateMapScreenController();
			case OPTIONS:
				return new OptionsScreenController();
			case STRATEGY:
				return new StrategyScreenController();
			default:
				return new MenuScreenController();
		}
	}
	
	public static ScreenController getController(String historyToken) {
		if ( null == historyToken ) {
			return getController((Screen) null);
		}
		
		for( Screen screen : Screen.values() ) {
			if ( historyToken.startsWith(getHistoryToken(screen)) ) {
				return getController(screen);
			}
		}
		return getController((Screen) null);
	}
}
