package game;

import javax.vecmath.Vector3f;

import framework.Framework;
import framework.engine.DynamicObject;
import framework.engine.GameObject;
import framework.engine.IGameUpdater;

/**
 * @author germangb
 *
 */
public class ObjectMotion implements IGameUpdater {

	//
	// IGameUpdater interface implementation
	//
	
	@Override
	public void update(GameObject object) {
		float dt = Framework.getInstance().getDeltaTime();
		DynamicObject dyn = (DynamicObject) object;
		Vector3f velo = dyn.velocity();
		Vector3f pos = dyn.position();

		/* update Y position */
		pos.y += velo.y * dt;
		pos.x += velo.x * dt;
		pos.z += velo.z * dt;
		velo.y -= 9.8f * 8.0f * dt;
	}
	
	//
	// END
	//

}
