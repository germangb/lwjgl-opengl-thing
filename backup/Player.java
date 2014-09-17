package game;

/**
 * @author germangb
 *
 */
public class Player {
	
	/**
	 * Max healt value
	 */
	private static final int MAX_HEALTH = 100;
	
	private int health;
	
	/**
	 * Instantiate player
	 */
	public Player() {
		this.health = MAX_HEALTH;
	}
	
	public int getHealth () {
		return health;
	}

}
