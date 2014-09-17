package engine.nodes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import engine.GameNode;
import engine.framework.IResourceLoader;
import engine.framework.ResourceManager;

/**
 * @author germangb
 *
 */
public class TerrainNode extends GameNode implements IResourceLoader {

	private static float SCALE = 4.0f;
	private static float HEIGHT = 32.0f;
	private static int SIZE = 512;
	
	/**
	 * height map path
	 */
	private String heightMapPath;
	private String concreteMapPath;
	private String sandMapPath;
	private String roadMapPath;
	
	/**
	 * height values
	 */
	private float[][] heightValues;
	private float[][] concreteValues;
	private float[][] sandValues;
	private float[][] roadValues;

	/**
	 * Default constructor
	 */
	public TerrainNode() {
		super("terrain");
		writeShadow = false;
		this.heightMapPath = "maps/test/heightmap.png";
		this.concreteMapPath = "maps/test/concrete.png";
		this.sandMapPath = "maps/test/sand.png";
		this.roadMapPath = "maps/test/road.png";
		
		/* add to the resource manager */
		ResourceManager.addResources(this);
		
		/* set height values */
		heightValues = new float[SIZE][SIZE];
		concreteValues = new float[SIZE][SIZE];
		sandValues = new float[SIZE][SIZE];
		roadValues = new float[SIZE][SIZE];
	}
	
	/**
	 * @return terrain scale
	 */
	public float getScale () {
		return SCALE;
	}
	
	/**
	 * @return terrain height
	 */
	public float getHeight () {
		return HEIGHT;
	}
	
	/**
	 * @return heightmap size
	 */
	public int getSize () {
		return SIZE;
	}
	
	/**
	 * returns a height value (normalized)
	 * @param row row
	 * @param col column
	 * @return normalized height value
	 */
	public float getHeightValue (int row, int col) {
		return heightValues[row][col];
	}
	
	/**
	 * returns a concrete value (normalized)
	 * @param row row
	 * @param col column
	 * @return normalized concrete value
	 */
	public float getConcreteValue (int row, int col) {
		return concreteValues[row][col];
	}
	
	/**
	 * returns a road value (normalized)
	 * @param row row
	 * @param col column
	 * @return normalized concrete value
	 */
	public float getRoadValue (int row, int col) {
		return roadValues[row][col];
	}
	
	/**
	 * returns a sand value (normalized)
	 * @param row row
	 * @param col column
	 * @return normalized concrete value
	 */
	public float getSandValue (int row, int col) {
		return sandValues[row][col];
	}
	
	/**
	 * THIS METHOD ASUMES the (0,0) to be the center
	 * of the height-map in world space coordinates.
	 * 
	 * Return the road, based on discrete
	 * height information and interpolation
	 * 
	 * when triangles are treated as units equations
	 * are stupidly simple
	 * 
	 * http://en.wikipedia.org/wiki/Barycentric_coordinate_system
	 * 
	 * @param x X coordinate
	 * @param z Z coordinate
	 */
	public float getRoad (float x, float z) {
		int row = (int) (x / SCALE + SIZE/2);
		int col = (int) (z / SCALE + SIZE/2);
		
		/* clamp position */
		if (row < 0) row = 0;
		else if (row >= SIZE-1) row = SIZE-2;
		if (col < 0) col = 0;
		else if (col >= SIZE-1) col = SIZE-2;
		float intX = (x+SIZE/2*SCALE)/SCALE;
		float intZ = (z+SIZE/2*SCALE)/SCALE;
		intX -= (int)intX;
		intZ -= (int)intZ;
		intZ = 1-intZ;
		
		/* height values */
		float h0 = roadValues[row][col];
		float h1 = roadValues[row][col+1];
		float h2 = roadValues[row+1][col+1];
		float h3 = roadValues[row+1][col];
		
		/* check triangle */
		if (intX+intZ<1.0f) {
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h0 + coef2 * h2 + coef1 * h1);
		} else {
			// this case is the same one as before but
			// mirrored
			intX = 1-intX;
			intZ = 1-intZ;
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h2 + coef2 * h0 + coef1 * h3);
		}
	}
	
	/**
	 * THIS METHOD ASUMES the (0,0) to be the center
	 * of the height-map in world space coordinates.
	 * 
	 * Return the sand, based on discrete
	 * height information and interpolation
	 * 
	 * when triangles are treated as units equations
	 * are stupidly simple
	 * 
	 * http://en.wikipedia.org/wiki/Barycentric_coordinate_system
	 * 
	 * @param x X coordinate
	 * @param z Z coordinate
	 */
	public float getSand (float x, float z) {
		int row = (int) (x / SCALE + SIZE/2);
		int col = (int) (z / SCALE + SIZE/2);
		
		/* clamp position */
		if (row < 0) row = 0;
		else if (row >= SIZE-1) row = SIZE-2;
		if (col < 0) col = 0;
		else if (col >= SIZE-1) col = SIZE-2;
		float intX = (x+SIZE/2*SCALE)/SCALE;
		float intZ = (z+SIZE/2*SCALE)/SCALE;
		intX -= (int)intX;
		intZ -= (int)intZ;
		intZ = 1-intZ;
		
		/* height values */
		float h0 = sandValues[row][col];
		float h1 = sandValues[row][col+1];
		float h2 = sandValues[row+1][col+1];
		float h3 = sandValues[row+1][col];
		
		/* check triangle */
		if (intX+intZ<1.0f) {
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h0 + coef2 * h2 + coef1 * h1);
		} else {
			// this case is the same one as before but
			// mirrored
			intX = 1-intX;
			intZ = 1-intZ;
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h2 + coef2 * h0 + coef1 * h3);
		}
	}

	/**
	 * THIS METHOD ASUMES the (0,0) to be the center
	 * of the height-map in world space coordinates.
	 * 
	 * Return the concrete, based on discrete
	 * height information and interpolation
	 * 
	 * when triangles are treated as units equations
	 * are stupidly simple
	 * 
	 * http://en.wikipedia.org/wiki/Barycentric_coordinate_system
	 * 
	 * @param x X coordinate
	 * @param z Z coordinate
	 */
	public float getConcrete (float x, float z) {
		int row = (int) (x / SCALE + SIZE/2);
		int col = (int) (z / SCALE + SIZE/2);
		
		/* clamp position */
		if (row < 0) row = 0;
		else if (row >= SIZE-1) row = SIZE-2;
		if (col < 0) col = 0;
		else if (col >= SIZE-1) col = SIZE-2;
		float intX = (x+SIZE/2*SCALE)/SCALE;
		float intZ = (z+SIZE/2*SCALE)/SCALE;
		intX -= (int)intX;
		intZ -= (int)intZ;
		intZ = 1-intZ;
		
		/* height values */
		float h0 = concreteValues[row][col];
		float h1 = concreteValues[row][col+1];
		float h2 = concreteValues[row+1][col+1];
		float h3 = concreteValues[row+1][col];
		
		/* check triangle */
		if (intX+intZ<1.0f) {
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h0 + coef2 * h2 + coef1 * h1);
		} else {
			// this case is the same one as before but
			// mirrored
			intX = 1-intX;
			intZ = 1-intZ;
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h2 + coef2 * h0 + coef1 * h3);
		}
	}
	
	/**
	 * THIS METHOD ASUMES the (0,0) to be the center
	 * of the height-map in world space coordinates.
	 * 
	 * Return the height, based on discrete
	 * height information and interpolation
	 * 
	 * when triangles are treated as units equations
	 * are stupidly simple
	 * 
	 * http://en.wikipedia.org/wiki/Barycentric_coordinate_system
	 * 
	 * @param x X coordinate
	 * @param z Z coordinate
	 */
	public float getHeight (float x, float z) {
		int row = (int) (x / SCALE + SIZE/2);
		int col = (int) (z / SCALE + SIZE/2);
		
		/* clamp position */
		if (row < 0) row = 0;
		else if (row >= SIZE-1) row = SIZE-2;
		if (col < 0) col = 0;
		else if (col >= SIZE-1) col = SIZE-2;
		float intX = (x+SIZE/2*SCALE)/SCALE;
		float intZ = (z+SIZE/2*SCALE)/SCALE;
		intX -= (int)intX;
		intZ -= (int)intZ;
		intZ = 1-intZ;
		
		/* height values */
		float h0 = heightValues[row][col];
		float h1 = heightValues[row][col+1];
		float h2 = heightValues[row+1][col+1];
		float h3 = heightValues[row+1][col];
		
		/* check triangle */
		if (intX+intZ<1.0f) {
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h0 + coef2 * h2 + coef1 * h1)*HEIGHT;
		} else {
			// this case is the same one as before but
			// mirrored
			intX = 1-intX;
			intZ = 1-intZ;
			float coef1 = -(intX+intZ-1);
			float coef2 = intX;
			float coef3 = 1.0f - coef2 - coef1;
			return (coef3 * h2 + coef2 * h0 + coef1 * h3)*HEIGHT;
		}
	}
	
	/**
	 * Instead of interpolating normals
	 * and wasting memory, use the getHeight()
	 * method to calculate the gradient of the
	 * height-map
	 * 
	 * @param x X coordinate
	 * @param z Z coordinate
	 * @return normal at point [x, y(x,z), z] of the height-map
	 */
	public Vector3f getNormal (float x, float z) {
		Vector3f normal = new Vector3f();
		float dif = 0.0125f;
		float h = getHeight(x, z);
		float hdx = getHeight(x+dif, z);
		float hdz = getHeight(x, z+dif);
		
		/* gradient */
		normal.x = (h-hdx);
		normal.y = dif;
		normal.z = (h-hdz);
		normal.normalize();
		return normal;
	}
	
	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* generate buffer */
		BufferedImage image = null;
		BufferedImage imageConcrete = null;
		BufferedImage imageSand = null;
		BufferedImage imageRoad = null;
		
		/* get image data */
		try {
			image = ImageIO.read(new File(heightMapPath));
			imageConcrete = ImageIO.read(new File(concreteMapPath));
			imageSand = ImageIO.read(new File(sandMapPath));
			imageRoad = ImageIO.read(new File(roadMapPath));
		} catch (IOException e) {
			/* image io error */
			return false;
		}

		/* pixels */
		for (int i = 0; i < SIZE*SIZE; ++i) {
			int row = i / SIZE;
			int col = i % SIZE;
			int sample = image.getRGB(col, row) & 0xFF;
			int sampleConcrete = imageConcrete.getRGB(col, row) & 0xFF;
			int sampleSand = imageSand.getRGB(col, row) & 0xFF;
			int roadSand = imageRoad.getRGB(col, row) & 0xFF;
			float normHeight = sample / 255.0f;
			float normConcrete = sampleConcrete / 255.0f;
			float normSand = sampleSand / 255.0f;
			float normRoad = roadSand / 255.0f;
			if (normHeight < 0.0) normHeight = 0.0f;
			heightValues[row][col] = normHeight;
			concreteValues[row][col] = normConcrete;
			sandValues[row][col] = normSand;
			roadValues[row][col] = normRoad;
		}
		
		/* it is safe to add the view now */
		addGameRenderer(new TerrainView(this));
		
		/* report success */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		// do nothing here
	}
	
	//
	// END
	//

}
