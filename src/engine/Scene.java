package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
//import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.framework.Framework;
import engine.graphics.FrameBuffer;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.nodes.CameraNode;
import engine.nodes.OrthogonalCamera;
import engine.nodes.VoidNode;

/**
 * @author germangb
 *
 */
public final class Scene implements IResourceLoader {

	/**
	 * Singleton instance
	 */
	private static Scene instance = null;
	
	/**
	 * get singleton instance
	 * @return
	 */
	public static Scene getInstance () {
		if (instance == null)
			instance = new Scene();
		return instance;
	}
	
	/* fullQuad shader */
	private static Shader SHADER = Shader
			.fromFile(
					"shaders/fullQuad.vert",
					"shaders/fullQuad.frag");
	
	private FrameBuffer GBUFFER_TARGET = null;
	private FrameBuffer RENDER_TARGET = null;
	private FrameBuffer SHADOW_TARGET = null;
	private Quality shadowQuality;
	private boolean renderShadows;
	private CameraNode SHADOW_CAMERA;
	private CameraNode FLAT_CAMERA;
	
	/* full quad vbo */
	private int vbo;

	/* hierarchy roots */
	private GameNode root;
	private GameNode flatRoot;
	
	/* camera being used in the screen */
	private CameraNode usedCamera;
	private int scale;
	private boolean started;
	private int background;
		
	/**
	 * Default constructor
	 */
	private Scene () {
		/* init Scene stuff */
		scale = 1;
		ResourceManager.addResources(this);
		this.root = new VoidNode("root");
		this.flatRoot = new VoidNode("flat_root"); 
		this.usedCamera = null;
		this.started = false;
		this.shadowQuality = Quality.MEDIUM;
		this.renderShadows = false;
		this.background = 0x1C1D19;
	}
	
	public void setShadowQuality (Quality quality) {
		if (!started)
			this.shadowQuality = quality;
		else {
			String err = "You can't set shadow quality after the scene's been created";
			Framework.getInstance().err(err);
		}
	}
	
	/**
	 * @param color
	 */
	public void setBackground (int color) {
		this.background = color;
	}
	
	/**
	 * toggle shadow mapping
	 * @param b
	 */
	public void setRenderShadows (boolean b) {
		this.renderShadows = b;
	}
	
	public boolean renderShadows () {
		return renderShadows;
	}
	
	/**
	 * return camera used by the shadow map
	 * @return camera
	 */
	public CameraNode getShadowCamera () {
		return SHADOW_CAMERA;
	}
	
	/**
	 * depth map from the shadow
	 * @return textured shadow map
	 */
	public Texture getShadowMap () {
		return SHADOW_TARGET.getDepthTexture();
	}
	
	/**
	 * method called once every frame
	 */
	public void update () {		
		/* update tree */
		root.update();
		flatRoot.update();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		/* render shadow map */
		{
			CameraNode aux = usedCamera;
			usedCamera = SHADOW_CAMERA;
			SHADOW_TARGET.bind();
			/* set up for fbo render */
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glViewport(0, 0, SHADOW_TARGET.getWidth(), SHADOW_TARGET.getHeight());
			/* render using shaders */
			if (renderShadows)
				root.render(true, false);
			usedCamera = aux;
		}
		
		/* bind gbuffer target */
		{
			GBUFFER_TARGET.bind();
			/* set up for fbo render */
			int r = (background & 0xff0000) >> 16;
			int g = (background & 0x00ff00) >> 8;
			int b = (background & 0x0000ff) >> 0;
			GL11.glClearColor(r/255f, g/255f, b/255f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glViewport(0, 0, GBUFFER_TARGET.getWidth(), GBUFFER_TARGET.getHeight());
			root.render(false, true);
		}
		
		/* bind render target */
		{
			RENDER_TARGET.bind();
			/* set up for fbo render */
			int r = (background & 0xff0000) >> 16;
			int g = (background & 0x00ff00) >> 8;
			int b = (background & 0x0000ff) >> 0;
			GL11.glClearColor(r/255f, g/255f, b/255f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glViewport(0, 0, RENDER_TARGET.getWidth(), RENDER_TARGET.getHeight());
			/* render using shaders */
			root.render(false, false);
			/* render flat */
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			CameraNode aux = usedCamera;
			usedCamera = FLAT_CAMERA;
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			flatRoot.render(false, false);
			GL11.glPopAttrib();
			usedCamera = aux;
		}

		/* bind window render target */
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		renderFrameBufferQuad();
	}

	/**
	 * Render fbo quads with PP effects
	 */
	private void renderFrameBufferQuad () {
		Framework frame = Framework.getInstance();
		
		/* render full screen quad to screen */
		//int colorTexture = RENDER_TARGET.getColorTexture(0).getId();
		
		/* set up basic */
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, frame.getWindowWidth(), frame.getWindowHeight());
		
		/* set shader */
		SHADER.bind();
		
		/* get texture locations */
		//int colorLoc = GL20.glGetUniformLocation(SHADER.getProgram(), "colorTexture");
		//int resLoc = GL20.glGetUniformLocation(SHADER.getProgram(), "resolution");
		
		/* upload time */
		//int time = GL20.glGetUniformLocation(SHADER.getProgram(), "time");
		SHADER.uniform1f("time", Framework.getInstance().getLocalTime() * 0.001f);
		//GL20.glUniform1f(time, Framework.getInstance().getLocalTime() * 0.001f);
		
		/* bind uniforms */
		//GL20.glUniform1i(colorLoc, 0);
		SHADER.uniform1i("colorTexture", 0);
		SHADER.uniform2f("resolution", frame.getWindowWidth(), frame.getWindowHeight());
		//GL20.glUniform2f(resLoc, frame.getWindowWidth(), frame.getWindowHeight());

		/* bind textures */
		RENDER_TARGET.getColorTexture(0).bindTo(0);
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		
		/* render vbo */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Return the root object of the scene.
	 * Everything displayable is related to this
	 * root object,
	 * @return game object root
	 */
	public GameNode getRoot () {
		return root;
	}
	
	public Texture getDepthTexture () {
		return GBUFFER_TARGET.getDepthTexture();
	}
	
	public Texture getSceneTexture () {
		return GBUFFER_TARGET.getColorTexture(0);
	}
	
	public Texture getDataTexture () {
		return GBUFFER_TARGET.getColorTexture(1);
	}
	
	/**
	 * Return the root object of the flat scene.
	 * Everything displayable is related to this
	 * root object,
	 * @return game object root
	 */
	public GameNode getFlatRoot () {
		return flatRoot;
	}
	
	/**
	 * Set the camera that will be used
	 * @param camera new camera
	 */
	public void setUsedCamera (CameraNode camera) {
		if (camera == null)
			throw new IllegalArgumentException();
		this.usedCamera = camera;
	}
	
	/**
	 * @return camera being used
	 */
	public CameraNode getUsedCamera () {
		return usedCamera;
	}
	
	/**
	 * set the size of the pixels
	 * @param scale new pixel size
	 */
	public void setPixelScale (int scale) {
		if (!started)
			this.scale = scale;
		else {
			String err = "You can't set the pixel scale after the scene's been created";
			Framework.getInstance().err(err);
		}
	}
	
	/**
	 * get the pixel scale
	 * @return pixel scale in pixels
	 */
	public int getPixelScale () {
		return scale;
	}

	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		started = true;
		/* create frame buffers */
		int width = Framework.getInstance().getWindowWidth();
		int height = Framework.getInstance().getWindowHeight();
		
		/* create objects */
		RENDER_TARGET = new FrameBuffer(width/scale, height/scale, 1);
		GBUFFER_TARGET = new FrameBuffer(width/scale, height/scale, 2);
		int shadowMapWidth = 512;	// medium
		int shadowMapHeight = 512;
		// determine shadow-map resolution
		switch (shadowQuality) {
			case MEDIUM:
				// don't touch
			default:
				break;
			case VERY_LOW:
				shadowMapWidth >>= 2;
				shadowMapHeight >>= 2;
				break;
			case LOW:
				shadowMapWidth >>= 1;
				shadowMapHeight >>= 1;
				break;
			case HIGH:
				shadowMapWidth <<= 1;
				shadowMapHeight <<= 1;
				break;
			case VERY_HIGH:
				shadowMapWidth <<= 2;
				shadowMapHeight <<= 2;
				break;
		}
		SHADOW_TARGET = new FrameBuffer(shadowMapWidth, shadowMapHeight, 1);

		/* shadow camera position */
		FLAT_CAMERA = new OrthogonalCamera("flat_camera", 0, RENDER_TARGET.getWidth(), RENDER_TARGET.getHeight(), 0, -1, 1);
		float asp = Framework.getInstance().getWindowAspectRatio();
		float size = 64;
		SHADOW_CAMERA = new OrthogonalCamera("shadow_map_camera", -asp*size, asp*size, -size, size, -100, 100);
		
		/* create full quad vbo */
		vbo = GL15.glGenBuffers();
		FloatBuffer buff = BufferUtils.createFloatBuffer(2*3*2);
		buff.put(new float[] {
			-1, -1,
			1, -1,
			1, 1,
			-1, -1,
			1, 1,
			-1, 1,
		});
		buff.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buff, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		buff.clear();
		
		/* return success status */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
	}
	
	//
	// END
	//
}
