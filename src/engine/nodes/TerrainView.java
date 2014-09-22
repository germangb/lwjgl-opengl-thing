package engine.nodes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import engine.IResourceLoader;
import engine.ResourceManager;
import engine.Scene;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.framework.LinearUtils;
import engine.graphics.Shader;

/**
 * @author germangb
 *
 */
public class TerrainView implements IResourceLoader, IGameRenderer {

	/**
	 * Shaders
	 * */
	private static Shader TERRAIN_SHADER = Shader
			.fromFile(
					"shaders/terrain.vert",
					"shaders/terrain.frag");

	/* terrain thing */
	private TerrainNode terrain;
	
	/* OpenGL buffer handlers */
	private int vbo;
	private int nbo;
	private int cbo;
	private int[][] chunks;
	int slize = 16;
	
	/**
	 * Creates a terrain instance
	 * @param key Game object key
	 */
	public TerrainView(TerrainNode t) {
		/* Create terrain */
		this.terrain = t;
		this.chunks = new int[terrain.getSize()/slize][terrain.getSize()/slize];
		ResourceManager.addResources(this);
	}
	
	/**
	 * get a reference of the terrain
	 * @return terrain reference
	 */
	public TerrainNode getTerrain () {
		return terrain;
	}
	
	//
	// IGameRenderer interface implementation
	//
	float a = 0;
	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		// TODO frustum culling
		/* buffer that will hold matrix data to push */
		/* to the gpu */
				
		/* bind shader */
		TERRAIN_SHADER.bind();
		TERRAIN_SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		TERRAIN_SHADER.uniformMat4("modelViewMatrix", false, mv);
		TERRAIN_SHADER.uniformMat4("viewMatrix", false, v);
		
		/* shadow ViewProjection upload */
		Matrix4f mfix = new Matrix4f();
		mfix.m00 = 0.5f;	mfix.m10 = 0.0f;	mfix.m20 = 0.0f;	mfix.m30 = 0.5f;
		mfix.m01 = 0.0f;	mfix.m11 = 0.5f;	mfix.m21 = 0.0f;	mfix.m31 = 0.5f;
		mfix.m02 = 0.0f;	mfix.m12 = 0.0f;	mfix.m22 = 0.5f;	mfix.m32 = 0.5f;
		mfix.m03 = 0.0f;	mfix.m13 = 0.0f;	mfix.m23 = 0.0f;	mfix.m33 = 1.0f;
		//int smvLoc = GL20.glGetUniformLocation(program, "shadowMatrix");
		CameraNode shadowCamera = Scene.getInstance().getShadowCamera();
		Matrix4f shadowViewProj = LinearUtils.getModelViewProjection(shadowCamera, terrain);
		// shadowCamera.getViewProjectionMatrix()
		Matrix4f shadowMatrix = new Matrix4f();
		Matrix4f.mul(mfix, shadowViewProj, shadowMatrix);
		TERRAIN_SHADER.uniformMat4("shadowMatrix", false, shadowMatrix);

		
		/* shadow map upload */
		TERRAIN_SHADER.uniform1i("shadowMap", 0);
		Scene.getInstance().getShadowMap().bindTo(0);
		TERRAIN_SHADER.uniform1i("renderShadows", Scene.getInstance().renderShadows() ? 1 : 0);

		/* upload time */
		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		int fogColor = WorldGlobals.FOG_COLOR;
		TERRAIN_SHADER.uniform1f("time", Framework.getInstance().getLocalTime()*0.001f);
		TERRAIN_SHADER.uniform1f("fogStart", WorldGlobals.FOG_START);
		TERRAIN_SHADER.uniform1f("fogConst", WorldGlobals.FOG_DENSITY);
		TERRAIN_SHADER.uniform3f("fogColor", ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		TERRAIN_SHADER.uniform3f("ambientTint", ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		Vector3f dir = Scene.getInstance().getShadowCamera().getLook();
		TERRAIN_SHADER.uniform3f("dirLight", dir.x, dir.y, dir.z);
		
		
		/* render */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbo);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0);
		
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		CameraNode camera = Scene.getInstance().getUsedCamera();
		Vector3f cameraPos = camera.getPosition();
		//int count = 0;
		for (int x = 0; x < chunks.length - 1; ++x)
			for (int z = 0; z < chunks[x].length - 1; ++z) {
				/* test chunk */
				float posX = (x-chunks.length/2 + 0.5f)*slize*terrain.getScale();
				float posY = 0;
				float posZ = (z-chunks[x].length/2 + 0.5f)*slize*terrain.getScale();
				Vector3f chunkPos = new Vector3f(posX-cameraPos.x, posY-cameraPos.y, posZ-cameraPos.z);
				float dot = chunkPos.dot(camera.getLook());
				boolean cond = dot > -0.0;
				if (camera instanceof PerspectiveCamera) {
					PerspectiveCamera per = (PerspectiveCamera) camera;
					float far = per.far();
					cond = cond && (chunkPos.lengthSquared()) < far*far;
				} else if (camera instanceof OrthogonalCamera) {
					OrthogonalCamera per = (OrthogonalCamera) camera;
					float far = per.far();
					cond = cond && (chunkPos.lengthSquared()) < far*far;
				}
				if (cond) {
					//++count;
					GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, chunks[x][z]);
					GL11.glDrawElements(GL11.GL_TRIANGLES, slize*slize*2*3, GL11.GL_UNSIGNED_INT, 0);
				}
			}
		//Framework.getInstance().log(count+" chunks being rendered");
		
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		/* unbind shader */
		//buffer.clear();
	}
	
	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* terrain attributes */
		int size = terrain.getSize();
		float scale = terrain.getScale();
		float height = terrain.getHeight();
		
		/* generate buffer */
		vbo = GL15.glGenBuffers();
		nbo = GL15.glGenBuffers();
		cbo = GL15.glGenBuffers();
		
		/* buffers */
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(size * size * 3);
		FloatBuffer normalData = BufferUtils.createFloatBuffer(size * size * 3);
		FloatBuffer colorData = BufferUtils.createFloatBuffer(size * size * 3);
		
		
		/* pixels */
		for (int i = 0; i < size*size; ++i) {
			int row = i / size;
			int col = i % size;
			
			float normHeight = terrain.getHeightValue(row, col);
			float normConcrete = terrain.getConcreteValue(row, col);
			float normSand = terrain.getSandValue(row, col);
			float normRoad = terrain.getRoadValue(row, col);
			
			/* coordinates */
			float x = (row-size/2)*scale;
			float y = normHeight*height;
			float z = (col-size/2)*scale;
			
			/* painting layers */
			float texX = normConcrete;
			float texY = normSand;
			float texZ = normRoad;
			
			/* put into the buffer */
			vertexData.put(x);	// x
			vertexData.put(y);	// y
			vertexData.put(z);	// z
			colorData.put(texX);	// concrete
			colorData.put(texY);	// sand
			colorData.put(texZ);	// nothing
			
			float nx = 0.0f;
			float ny = 1.0f;
			float nz = 0.0f;
			
			if (row > 0 && col > 0) {
				int mRow = row-1;
				int mCol = col;
				
				float ux = vertexData.get(3*(mRow*size+mCol)+0) - vertexData.get(3*(row*size+col)+0);
				float uy = vertexData.get(3*(mRow*size+mCol)+1) - vertexData.get(3*(row*size+col)+1);
				float uz = vertexData.get(3*(mRow*size+mCol)+2) - vertexData.get(3*(row*size+col)+2);
				
				mRow = row;
				mCol = col-1;
				
				float vx = vertexData.get(3*(mRow*size+mCol)+0) - vertexData.get(3*(row*size+col)+0);
				float vy = vertexData.get(3*(mRow*size+mCol)+1) - vertexData.get(3*(row*size+col)+1);
				float vz = vertexData.get(3*(mRow*size+mCol)+2) - vertexData.get(3*(row*size+col)+2);
				
				/* cross product */
				Vector3f u = new Vector3f(ux, uy, uz);
				u.normalize();
				Vector3f v = new Vector3f(vx, vy, vz);
				v.normalize();
				Vector3f cross = new Vector3f();
				cross.cross(v, u);
				
				/* add the normal */			
				nx += cross.x;
				ny += cross.y;
				nz += cross.z;
			}
			
			if (row < size/2 && col < size/2) {
				int mRow = row+1;
				int mCol = col;
				
				float ux = vertexData.get(3*(mRow*size+mCol)+0) - vertexData.get(3*(row*size+col)+0);
				float uy = vertexData.get(3*(mRow*size+mCol)+1) - vertexData.get(3*(row*size+col)+1);
				float uz = vertexData.get(3*(mRow*size+mCol)+2) - vertexData.get(3*(row*size+col)+2);
				
				mRow = row;
				mCol = col+1;
				
				float vx = vertexData.get(3*(mRow*size+mCol)+0) - vertexData.get(3*(row*size+col)+0);
				float vy = vertexData.get(3*(mRow*size+mCol)+1) - vertexData.get(3*(row*size+col)+1);
				float vz = vertexData.get(3*(mRow*size+mCol)+2) - vertexData.get(3*(row*size+col)+2);
				
				/* cross product */
				Vector3f u = new Vector3f(ux, uy, uz);
				u.normalize();
				Vector3f v = new Vector3f(vx, vy, vz);
				v.normalize();
				Vector3f cross = new Vector3f();
				cross.cross(u, v);
				
				/* add the normal */
				nx += cross.x;
				ny += cross.y;
				nz += cross.z;
			}
			
			/* fill normal */
			normalData.put(3*(row*size+col)+0, nx+normalData.get(3*(row*size+col)+0));
			normalData.put(3*(row*size+col)+1, ny+normalData.get(3*(row*size+col)+1));
			normalData.put(3*(row*size+col)+2, nz+normalData.get(3*(row*size+col)+2));
		}
		
		IntBuffer chunkIndex = BufferUtils.createIntBuffer(slize*slize*2*3);
		for (int x = 0; x < chunks.length; ++x) {
			for (int z = 0; z < chunks[x].length; ++z) {
				chunks[x][z] = GL15.glGenBuffers();
				int startX = slize*x;
				int startZ = slize*z;
				for (int i = startX; i < startX + slize; ++i) {
					for (int j = startZ; j < startZ + slize; ++j) {
						chunkIndex.put(( size*i + j ));
						chunkIndex.put(( size*i + (j+1) ));
						chunkIndex.put(( size*(i+1) + j ));
						
						chunkIndex.put(( size*i + (j+1) ));
						chunkIndex.put(( size*(i+1) + (j+1) ));
						chunkIndex.put(( size*(i+1) + j ));
					}
				}
				/* upload data */
				chunkIndex.flip();
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, chunks[x][z]);
				GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, chunkIndex, GL15.GL_STATIC_DRAW);
				chunkIndex.position(0);
			}
		}
		
		/* flip */
		vertexData.flip();
		colorData.flip();		
		
		/* tex coord buffer */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorData, GL15.GL_STATIC_DRAW);
		
		/* vertex buffer */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		
		/* normal buffer */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_STATIC_DRAW);

		/* unbind buffers */
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		/* free buffers */
		vertexData.clear();
		normalData.clear();
		colorData.clear();
		
		/* error check */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(cbo);
		for (int i = 0; i < chunks.length; ++i)
			for (int x = 0; x < chunks[i].length; ++x)
				GL15.glDeleteBuffers(chunks[i][x]);
	}
	
	//
	// END
	//

}
