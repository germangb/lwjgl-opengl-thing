package engine.labs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.vecmath.Vector3f;

/**
 * @author germangb
 *
 */
public class ConvexNode {
	
	private Edge[] edges;
	private Set<ConvexNode> neigbours;
	
	/**
	 * copy the list of edges
	 * @param edges edges array (will not be referenced!)
	 */
	public ConvexNode (Edge[] eds) {
		this.edges = new Edge[eds.length];
		this.neigbours = new HashSet<ConvexNode>();
		for (int i = 0; i < eds.length; ++i)
			this.edges[i] = eds[i];
	}
	
	//
	// Object re-implementation
	//
	
	@Override
	public String toString () {
		return edges.length+" edges; "+neigbours.size()+" connections";
	}
	
	//
	// END
	//

	/**
	 * @return
	 */
	public Iterator<ConvexNode> getNeighbours () {
		return neigbours.iterator();
	}
	
	/**
	 * @param ne
	 */
	public void addNeighbour (ConvexNode ne) {
		this.neigbours.add(ne);
	}
	
	/**
	 * @return
	 */
	public int edgeCount () {
		return edges.length;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Edge getEdge (int index) {
		if (index >= edges.length)
			throw new IndexOutOfBoundsException();
		return edges[index];
	}
	
	/**
	 * @return
	 */
	public Vector3f getMidPoint () {
		Vector3f mid = new Vector3f();
		for (int x = 0; x < edges.length; ++x) {
			Edge ed = edges[x];
			Vector3f u = ed.getA().getVector();
			Vector3f p = ed.getB().getVector();
			mid.x += (u.x + p.x) / (edges.length * 2);
			mid.y += (u.y + p.y) / (edges.length * 2);
			mid.z += (u.z + p.z) / (edges.length * 2);
		}
		return mid;
	}
	
	/**
	 * @param point
	 * @return
	 */
	public boolean testPoint (Vector3f point) {
		for (int i = 0; i < edges.length; ++i) {
			Vector3f test = new Vector3f(point);
			test.x -= edges[i].getA().getVector().x;
			test.y -= edges[i].getA().getVector().y;
			test.z -= edges[i].getA().getVector().z;
			test.normalize();
			if (test.dot(edges[i].normal()) < 0)
				return false;
		}
		return true;
	}
}
