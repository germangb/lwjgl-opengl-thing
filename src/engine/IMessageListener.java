package engine;

/**
 * @author germangb
 *
 */
public interface IMessageListener {

	/**
	 * Called when a message is received
	 * @param message message sent
	 */
	public void messageReceived (Object message);
	
}
