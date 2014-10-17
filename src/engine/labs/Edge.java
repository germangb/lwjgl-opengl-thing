package engine.labs;

import javax.vecmath.Vector3f;

/**
 * @author germangb
 *
 */
public class Edge {
	
	private boolean normalDirty;
	private Vector3f normal;
	private Vertex A;
	private Vertex B;
	
	/**
	 * @param A
	 * @param B
	 */
	public Edge (Vertex A, Vertex B) {
		this.A = A;
		this.B = B;
		this.normal = new Vector3f();
		this.normalDirty = true;
	}

	/**
	 * @return
	 */
	public Vertex getA () {
		return A;
	}
	
	/**
	 * @return
	 */
	public Vertex getB () {
		return B;
	}
	
	/**
	 * @return
	 */
	public Vector3f normal () {
		if (normalDirty) {
			normalDirty = false;
			float x = B.getVector().x - A.getVector().x;
			float z = B.getVector().z - A.getVector().z;
			normal.x = -z;
			normal.z = x;
			normal.y = 0;
			normal.normalize();
		}
		return normal;
	}
	
	//
	// Object re-implementation
	//
	
	@Override
	public boolean equals (Object obj) {
		if (! (obj instanceof Edge))
			return false;
		Edge e = (Edge) obj;
		return  (A == e.A && B == e.B) ||
				(A == e.B && B == e.A); 
	}
	
	//
	// END
	//
	
}