package game;

import engine.GameObject;
import engine.IGameUpdater;

/**
 * @author germangb
 *
 */
public class GameController implements IGameUpdater {
	
	/**
	 * singleton instance
	 */
	private static GameController instance = null;
	
	/**
	 * get the singleton
	 * @return singleton
	 */
	public static GameController getInstance () {
		if (instance == null)
			instance = new GameController();
		return instance;
	}
	
	/* current game state */
	private GameState state;
	
	/**
	 * default private
	 * constructor
	 */
	private GameController() {
		state = GameState.WORLD;
	}
	
	/**
	 * get current game state
	 * @return game state
	 */
	public GameState getState () {
		return state;
	}
	
	public void setState (GameState state) {
		this.state = state;
	}
	
	//
	// IGameUpdater interface implementation
	//

	@Override
	public void update(GameObject object) {
		// TODO Auto-generated method stub
		
	}
	
	//
	// END
	//

}
