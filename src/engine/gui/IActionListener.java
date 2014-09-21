package engine.gui;

/**
 * @author germangb
 *
 */
public interface IActionListener {
	
	/**
	 * called when an action is performed by
	 * a widget from the gui hierarchy
	 * @param who action performer
	 */
	public void action (Widget who);
	
}
