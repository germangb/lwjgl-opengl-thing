package engine.nodes;

import javax.vecmath.Vector3f;

import engine.GameNode;
import engine.framework.Framework;

/**
 * @author germangb
 *
 */
public class DynamicNode extends GameNode {

	/**
	 * velocity
	 */
	private Vector3f linearVelocity;
	private boolean gravity;
	
	/**
	 * @param key GameObject key name
	 */
	public DynamicNode(String key) {
		super(key);

		/* set dynamic parameters */
		this.linearVelocity = new Vector3f();
		this.gravity = true;
	}
	
	/**
	 * Set gravity flag
	 * @param b
	 */
	public void useGravity (boolean b) {
		this.gravity = b;
	}

	/**
	 * Returns a velocity vector reference
	 * @return vector REFERENCE
	 */
	public Vector3f getVelocity () {
		return new Vector3f(linearVelocity);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setVelocity (float x, float y, float z) {
		linearVelocity.x = x;
		linearVelocity.y = y;
		linearVelocity.z = z;
	}
	
	/**
	 * @param v
	 */
	public void setVelocity (Vector3f v) {
		setVelocity(v.x, v.y, v.z);
	}

	//
	// GameObject re-implementation
	//
	
	@Override
	public void update() {
		float dt = Framework.getInstance().getDeltaTime();
		/* update player position */
		Vector3f pos = getPosition();
		pos.x += linearVelocity.x * dt;
		pos.y += linearVelocity.y * dt;
		pos.z += linearVelocity.z * dt;
		setPosition(pos);
		if (gravity) 
			linearVelocity.y -= 9.81f * 4.0f * dt;
		super.update();
	}

	//
	// END
	//
	
}
