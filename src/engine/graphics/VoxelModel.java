package engine.graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import engine.framework.IResourceLoader;
import engine.framework.ResourceManager;

/**
 * @author germangb
 *
 */
public class VoxelModel implements IResourceLoader {

	/* OpenGL buffer handlers */
	private String path;
	public int totalVoxels;
	public int vbo;	// vertex
	public int cbo;	// color
	
	/**
	 * @param path
	 */
	public VoxelModel(String path) {
		this.path = path;
		ResourceManager.addResources(this);
	}
	
	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		vbo = GL15.glGenBuffers();
		cbo = GL15.glGenBuffers();
		
		List<Vector3f> pos = new ArrayList<Vector3f>();
		List<Vector3f> color = new ArrayList<Vector3f>();
		
		Scanner scan;
		try {
			scan = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			return false;
		}
		while (scan.hasNextLine()) {
			/* scan two lines */
			String[] posInfo = scan.nextLine().split(":")[1].split(",");
			String[] colInfo = scan.nextLine().split(":")[1].split(",");
			Vector3f newPos = new Vector3f(Integer.parseInt(posInfo[0].replace(" ", "")), Integer.parseInt(posInfo[1].replace(" ", "")), Integer.parseInt(posInfo[2].replace(" ", "")));
			Vector3f newColor = new Vector3f(Integer.parseInt(colInfo[0].replace(" ", ""))/255f, Integer.parseInt(colInfo[1].replace(" ", ""))/255f, Integer.parseInt(colInfo[2].replace(" ", ""))/255f);
			pos.add(newPos);
			color.add(newColor);
		}
		scan.close();
		
		/* buffers */
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(3*pos.size());
		FloatBuffer colorData = BufferUtils.createFloatBuffer(3*pos.size());
		
		/* fill data */
		for (int i = 0; i < pos.size(); ++i) {
			vertexData.put(pos.get(i).x).put(pos.get(i).y).put(pos.get(i).z);
			colorData.put(color.get(i).x).put(color.get(i).y).put(color.get(i).z);
		}
		
		/* flip */
		vertexData.flip();
		colorData.flip();
		
		/* upload to the gpu */
		this.totalVoxels = pos.size();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorData, GL15.GL_STATIC_DRAW);
		
		/* clear */
		vertexData.clear();
		colorData.clear();
		
		/* gl error check */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(cbo);
	}
	
	//
	// END
	//

}
