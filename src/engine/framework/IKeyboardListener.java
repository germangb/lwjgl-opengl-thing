package engine.framework;

/**
 * @author germangb
 *
 */
public interface IKeyboardListener {

	/**
	 * @param ascii Ascii code
	 * @param key Framework key code
	 */
	public void keyDown (char ascii, int key);
	
	/**
	 * @param ascii Ascii code
	 * @param key Framework key code
	 */
	//public void keyUp (char ascii, int key);
	
}
