package engine.labs;

import java.util.Iterator;

import javax.vecmath.Vector3f;

/**
 * @author germangb
 *
 */
public class Path {

	private Vector3f from;
	private Vector3f to;
	private Iterator<Edge> edges;
	private Edge nextEdge;
	
	/**
	 * @param origin
	 * @param destiny
	 * @param it
	 */
	public Path(Vector3f origin, Vector3f destiny, Iterator<Edge> it) {
		this.from = new Vector3f(origin);
		this.to = new Vector3f(destiny);
		this.edges = it;
		this.next();
	}
	
	/**
	 * 
	 */
	public void next () {
		if (edges.hasNext())
			nextEdge = edges.next();
	}
	
	/**
	 * @return
	 */
	public Edge getTarget () {
		return nextEdge;
	}
	
	/**
	 * @return
	 */
	public Edge getCurrentEdge () {
		return nextEdge;
	}
	
	/**
	 * @return
	 */
	public Vector3f from () {
		return new Vector3f(from);
	}
	
	/**
	 * @return
	 */
	public Vector3f to () {
		return new Vector3f(to);
	}

}
