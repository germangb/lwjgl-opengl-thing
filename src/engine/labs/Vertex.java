package engine.labs;

import javax.vecmath.Vector3f;

/**
 * @author germangb
 *
 */
public class Vertex {
	
	private Vector3f u;
	
	/**
	 * @param u
	 */
	public Vertex (Vector3f u) {
		this.u = new Vector3f(u);
	}
	
	/**
	 * @return
	 */
	public Vector3f getVector () {
		return new Vector3f(u);
	}

}