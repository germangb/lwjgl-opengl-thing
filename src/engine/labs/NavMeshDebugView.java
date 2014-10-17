package engine.labs;

import java.util.Iterator;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import engine.graphics.Shader;

public class NavMeshDebugView implements IGameRenderer {

	/**
	 * gl shader
	 */
	private final static Shader SHADER = Shader.fromFile(
			"shaders/navmesh.vert",
			"shaders/navmesh.frag");
	
	/**
	 * navmesh reference
	 */
	private NavMeshNode mesh;
	
	/**
	 * @param mesh
	 */
	public NavMeshDebugView(NavMeshNode mesh) {
		this.mesh = mesh;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		SHADER.bind();
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		SHADER.uniform4f("color", 1, 1, 1, 0.125f);
		
		SHADER.uniform4f("color", 1, 1, 1, 0.125f);
		GL11.glBegin(GL11.GL_LINES);
		for (int i = 0; i < mesh.nodeCount(); ++i) {
			ConvexNode n = mesh.getNode(i);
			for (int x = 0; x < n.edgeCount(); ++x) {
				Vector3f a = n.getEdge(x).getA().getVector();
				Vector3f b = n.getEdge(x).getB().getVector();
				Vector3f normal = n.getEdge(x).normal();
				normal.scale(1);
				GL11.glVertex3f(a.x, a.y, a.z);
				GL11.glVertex3f(b.x, b.y, b.z);
				GL11.glVertex3f((a.x+b.x)*0.5f, (a.y+b.y)*0.5f, (a.z+b.z)*0.5f);
				GL11.glVertex3f((a.x+b.x)*0.5f+normal.x, (a.y+b.y)*0.5f+normal.y, (a.z+b.z)*0.5f+normal.z);
			}
			
		}
		GL11.glEnd();
		
		SHADER.uniform4f("color", 1, 1, 0, 0.25f);
		GL11.glBegin(GL11.GL_LINES);
		for (int i = 0; i < mesh.nodeCount(); ++i) {
			Vector3f mid = mesh.getNode(i).getMidPoint();
			ConvexNode n = mesh.getNode(i);
			Iterator<ConvexNode> neis = n.getNeighbours();
			while (neis.hasNext()) {
				ConvexNode node = neis.next();
				GL11.glVertex3f(mid.x, mid.y, mid.z);
				GL11.glVertex3f(node.getMidPoint().x, node.getMidPoint().y, node.getMidPoint().z);
			}
			
		}
		GL11.glEnd();
		
	}
	
	//
	// END
	//

}
