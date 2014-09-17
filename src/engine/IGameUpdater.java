package engine;


/**
 * @author germangb
 *
 */
public interface IGameUpdater {

	/**
	 * Called once every frame
	 * @param object Game object the interface is called from
	 */
	public void update (GameNode object);

}
