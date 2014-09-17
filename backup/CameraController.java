package game;

import engine.Camera;
import engine.GameObject;
import engine.IGameUpdater;
import framework.Framework;
import framework.Input;

/**
 * @author germangb
 *
 */
public class CameraController implements IGameUpdater {

	private Camera camera;
	private float x;
	private float y;
	
	public CameraController(Camera camera) {
		this.camera = camera;
		this.x = y = 0;
	}
	
	//
	// IGameUpdater interface implementation
	//

	@Override
	public void update(GameObject object) {
		if (GameController.getInstance().getState() == GameState.WORLD) {
			if (Input.isMouseDown(0)) {
				int dx = Input.getMouseDX();
				int dy = Input.getMouseDY();
				x -= dx/2f;
				y -= dy/2f;
				camera.position().x = (int)x;
				camera.position().y = (int)y;
			}
		}
	}
	
	//
	// END
	//

}
