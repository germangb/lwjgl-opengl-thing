package engine.graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import engine.framework.IResourceLoader;
import engine.framework.ResourceManager;

/**
 * @author germangb
 *
 */
public class PolyModel implements IResourceLoader {
	
	/* OBJ model path */
	private String path;
	
	/* OpenGL object handlers */
	public int totalTris;
	public int vbo;
	public int nbo;
	public int uvbo;
	
	/**
	 * creates a mesh given the obj path file
	 * @param path
	 * @throws FileNotFoundException 
	 */
	public PolyModel (String path) {
		this.path = path;
		ResourceManager.addResources(this);
	}
	
	/**
	 * scan a position
	 * format: "v <X> <Y> <Z>"
	 * @param line
	 * @return
	 */
	private Position scanPosition (String line) {
		Position pos = new Position();
		String[] str = line.split(" ");
		pos.x = Float.parseFloat(str[1]);
		pos.y = Float.parseFloat(str[2]);
		pos.z = Float.parseFloat(str[3]);
		return pos;
	}
	
	/**
	 * scan a position
	 * format: "vn <X> <Y> <Z>"
	 * @param line
	 * @return
	 */
	private Normal scanNormal (String line) {
		Normal normal = new Normal();
		String[] str = line.split(" ");
		normal.x = Float.parseFloat(str[1]);
		normal.y = Float.parseFloat(str[2]);
		normal.z = Float.parseFloat(str[3]);
		return normal;
	}
	
	/**
	 * scan a position
	 * format: "vn <X> <Y> <Z>"
	 * @param line
	 * @return
	 */
	private Uv scanUv (String line) {
		Uv uv = new Uv();
		String[] str = line.split(" ");
		uv.u = Float.parseFloat(str[1]);
		uv.v = 1.0f - Float.parseFloat(str[2]);
		return uv;
	}
	
	/**
	 * scan a triangle
	 * format: "f <p0>/<t0>/<n1> <p1>/<t1>/<n1> <p2>/<t2>/<n2>"
	 * @param line
	 * @return
	 */
	private Tri scanTri (String line) {
		Tri tri = new Tri();
		String[] vertex = line.split(" ");
		String[] a = vertex[1].split("/");
		String[] b = vertex[2].split("/");
		String[] c = vertex[3].split("/");
		tri.p0 = Integer.parseInt(a[0])-1;
		tri.t0 = Integer.parseInt(a[1])-1;
		tri.n0 = Integer.parseInt(a[2])-1;
		tri.p1 = Integer.parseInt(b[0])-1;
		tri.t1 = Integer.parseInt(b[1])-1;
		tri.n1 = Integer.parseInt(b[2])-1;
		tri.p2 = Integer.parseInt(c[0])-1;
		tri.t2 = Integer.parseInt(c[1])-1;
		tri.n2 = Integer.parseInt(c[2])-1;
		return tri;
	}
	
	/* internally used classes */
	private class Position { public float x,y,z; }
	private class Normal { public float x,y,z; }
	private class Uv { public float u,v; }
	private class Tri {
		public int p0,n0,t0;
		public int p1,n1,t1;
		public int p2,n2,t2;
	}
	
	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		Scanner scanFile;
		try {
			scanFile = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			return false;
		}
		List<Position> positions = new ArrayList<Position>();
		List<Normal> normals = new ArrayList<Normal>();
		List<Uv> uvs = new ArrayList<Uv>();
		List<Tri> tris = new ArrayList<Tri>();
		/* parse file */
		while (scanFile.hasNextLine()) {
			String line = scanFile.nextLine();
			if (line.startsWith("vt"))
				uvs.add(scanUv(line));
			else if (line.startsWith("vn"))
				normals.add(scanNormal(line));
			else if (line.startsWith("v"))
				positions.add(scanPosition(line));
			else if (line.startsWith("f"))
				tris.add(scanTri(line));
		}
		this.totalTris = tris.size();
		FloatBuffer positionData = BufferUtils.createFloatBuffer(totalTris * 3 * 3);
		FloatBuffer normalData = BufferUtils.createFloatBuffer(totalTris * 3 * 3);
		FloatBuffer uvData = BufferUtils.createFloatBuffer(totalTris * 2 * 3);
		for (Tri tri : tris) {
			Position pos0 = positions.get(tri.p0);
			Normal nor0 = normals.get(tri.n0);
			Uv uv0 = uvs.get(tri.t0);
			Position pos1 = positions.get(tri.p1);
			Normal nor1 = normals.get(tri.n1);
			Uv uv1 = uvs.get(tri.t1);
			Position pos2 = positions.get(tri.p2);
			Normal nor2 = normals.get(tri.n2);
			Uv uv2 = uvs.get(tri.t2);
			positionData.put(pos0.x).put(pos0.y).put(pos0.z);
			positionData.put(pos1.x).put(pos1.y).put(pos1.z);
			positionData.put(pos2.x).put(pos2.y).put(pos2.z);
			normalData.put(nor0.x).put(nor0.y).put(nor0.z);
			normalData.put(nor1.x).put(nor1.y).put(nor1.z);
			normalData.put(nor2.x).put(nor2.y).put(nor2.z);
			uvData.put(uv0.u).put(uv0.v);
			uvData.put(uv1.u).put(uv1.v);
			uvData.put(uv2.u).put(uv2.v);
		}
		/* create data buffers */
		scanFile.close();
		
		/* flip buffers */
		positionData.flip();
		normalData.flip();
		uvData.flip();
		
		/* create opengl buffers */
		vbo = GL15.glGenBuffers();
		nbo = GL15.glGenBuffers();
		uvbo = GL15.glGenBuffers();
		
		/* upload data */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvData, GL15.GL_STATIC_DRAW);
		
		/* clear */
		positionData.clear();
		normalData.clear();
		uvData.clear();
		
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(uvbo);
	}
	
	//
	// END
	//
}
