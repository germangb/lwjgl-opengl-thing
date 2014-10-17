package engine.labs;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.vecmath.Vector3f;

import engine.GameNode;
import engine.IResourceLoader;
import engine.ResourceManager;

/**
 * @author germangb
 *
 */
public class NavMeshNode extends GameNode implements IResourceLoader {

	/**
	 * Navmesh formated description
	 * file
	 */
	private String file;
	
	private List<Vertex> vertex;
	private List<Edge> edges;
	private List<ConvexNode> nodes;
	
	/**
	 * created a nav mesh defined in a referenced
	 * file in the project
	 * 
	 * @param key game node key
	 * @param file navmesh information file
	 */
	public NavMeshNode(String key, String file) {
		super(key);
		this.file = file;
		this.vertex = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.nodes = new ArrayList<ConvexNode>();
		this.setDebug(true);
		this.debugRenderer = new NavMeshDebugView(this);
		ResourceManager.addResources(this);
	}
	
	/**
	 * @param v
	 * @return
	 */
	public ConvexNode getPositionNode (Vector3f v) {
		for (ConvexNode node : nodes) {
			if (node.testPoint(v))
				return node;
		}
		return null;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	public Path computePath (Vector3f from, Vector3f to) {
		Path path = null;
		for (ConvexNode node : nodes) {
			if (node.testPoint(from)) {
				System.out.println("starting path from node "+node);
				break;
			}
		}
		return path;
	}

	/**
	 * @return
	 */
	public int edgeCount () {
		return edges.size();
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Edge getEdge (int index) {
		return edges.get(index);
	}
	
	/**
	 * @return
	 */
	public int nodeCount () {
		return nodes.size();
	}
	
	/**
	 * @param index
	 * @return
	 */
	public ConvexNode getNode (int index) {
		return nodes.get(index);
	}
	
	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		try {
			FileReader ref = new FileReader(file);
			Scanner scan = new Scanner(ref);
			int vertexCount = Integer.parseInt(scan.nextLine());
			for (int i = 0; i < vertexCount; ++i) {
				String line = scan.nextLine();
				String[] v = line.split(",");
				Vector3f vec3 = new Vector3f();
				vec3.x = Float.parseFloat(v[0]);
				vec3.y = Float.parseFloat(v[1]);
				vec3.z = Float.parseFloat(v[2]);
				vertex.add(new Vertex(vec3));
			}
			int polyCount = Integer.parseInt(scan.nextLine());
			for (int i = 0; i < polyCount; ++i) {
				int edgeCount = Integer.parseInt(scan.nextLine());
				Edge eds[] = new Edge[edgeCount];
				for (int x = 0; x < edgeCount; ++x) {
					String[] edStr = scan.nextLine().split(",");
					int indexA = Integer.parseInt(edStr[0]);
					int indexB = Integer.parseInt(edStr[1]);
					Edge ed = new Edge(vertex.get(indexA), vertex.get(indexB));
					edges.add(ed);
					eds[x] = ed;
				}
				ConvexNode node = new ConvexNode(eds);
				nodes.add(node);
			}
			scan.close();
			
			/* connect nodes */
			
			for (ConvexNode n : nodes) {
				for (ConvexNode m : nodes) {
					if (n != m) {
						boolean neighbours = false;
						for (int i = 0; i < n.edgeCount() && !neighbours; ++i) {
							for (int x = 0; x < m.edgeCount() && !neighbours; ++x) {
								if (n.getEdge(i).equals(m.getEdge(x))) {
									neighbours = true;
									n.addNeighbour(m);
									m.addNeighbour(n);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void cleanResources() {
		// TODO Auto-generated method stub
		
	}

	//
	// END
	//
	
}
