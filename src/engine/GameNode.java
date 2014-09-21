package engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import engine.framework.Framework;
import engine.framework.LinearUtils;
import engine.framework.MatrixStack;
import engine.nodes.CameraNode;

/**
 * @author germangb
 *
 */
public abstract class GameNode {

	/* GameObject */
	private static MatrixStack MATRIX_STACK = new MatrixStack();
	private final String keyName;
	private GameNode parent;
	private List<GameNode> children;
	private Set<IGameUpdater> updaters;
	private Set<IGameRenderer> renderers;
	private Set<IMessageListener> messageListeners;
	protected IGameRenderer debugRenderer = null;
	
	/* properties */
	private boolean visible;
	private boolean debug;
	protected boolean writeShadow;
	protected boolean writeGbuffer;
	private Vector3f position;
	private Vector3f rotationAxis;
	
	/* used for interpolation */
	private Vector3f dposition;
	private Vector3f drotationAxis;
	
	/* interpolation smoothing factors */
	private int transSmooth;
	private int rotSmooth;
	
	/**
	 * creates a game object and assigns it a new
	 * key name
	 * @param keyName unique name
	 */
	public GameNode(String keyName) {
		this.keyName = keyName;
		this.parent = null;
		this.writeShadow = true;
		this.writeGbuffer = true;
		this.children = new ArrayList<GameNode>();
		this.updaters = new HashSet<IGameUpdater>();
		this.renderers = new HashSet<IGameRenderer>();
		this.messageListeners = new HashSet<IMessageListener>();
		this.debugRenderer = null;
		
		/* set properties */
		this.position = new Vector3f();
		this.rotationAxis = new Vector3f();
		
		/* smooth interpolations */
		this.dposition = new Vector3f();
		this.drotationAxis = new Vector3f();
		
		/* visibility */
		this.visible = true;
		this.debug = false;
		
		/* smoothing */
		transSmooth = -1;
		rotSmooth = -1;
	}

	/**
	 * prints the hierarchy tree
	 * @return tree string
	 */
	public String tree () {
		return recursiveTree(this, 0, false);
	}
	
	/**
	 * static variable used in the tree pretty print
	 */
	private static long bitSet = 0L;
			
	/**
	 * Internal method to create a fancy toString
	 * @param eval evaluated node
	 * @param level	recursion level
	 * @param last last object in the list
	 * @return pretty print
	 */
	private String recursiveTree (GameNode eval, int level, boolean last) {		
		bitSet |= 1 << level;
		String line = eval.keyName;
		String offset = "";
		for (int i = 0; i <= level; ++i) {
			String sep = "|";
			if (i == level) sep = "|>";
			if ( (bitSet & (1 << i)) != 0) offset += " "+sep+" ";
			else offset += "   ";
		}
		line = offset+line;
		if (last) bitSet &= ~(1 << level); 
		int i = eval.children.size();
		if (last && i == 0)
			line = line + "\n"+offset.substring(0, offset.length()-3);
		for (GameNode child : eval.children)
			if (child.parent == eval)
				line += "\n"+recursiveTree(child, level+1, --i <= 0);
		return line;
	}

	public void setTranslateSmooth (int trans) {
		this.transSmooth = trans;
	}
	
	public void setRotationSmooth (int rot) {
		this.rotSmooth = rot;
	}
	
	/**
	 * Add a new message listener
	 * @param listener message listener implementation
	 */
	public void addMessageListener (IMessageListener listener) {
		this.messageListeners.add(listener);
	}
	
	/**
	 * Add a game renderer to this object
	 * @param renderer renderer to be added
	 */
	public void addGameRenderer (IGameRenderer renderer) {
		this.renderers.add(renderer);
	}
	
	/**
	 * Add a game updater
	 * @param updater new game updater to be added
	 */
	public void addGameUpdater (IGameUpdater updater) {
		this.updaters.add(updater);
	}
	
	/**
	 * VERY POTENTIALLY SLOW SINCE THE OBJECTS ARE
	 * RANDOM AT RUNTIME. A FULL SEARCH MUST BE
	 * PERFORMED IN ORDER TO PROPERLY SEARCH THE TREES
	 * IT IS HIGHLY RECOMMENDED TO CALL THIS METHOD AS LITTLE
	 * TIMES AS POSSIBLE AND ONLY IF IT IS STRICTLY NECESSARY
	 *
	 * Performs a depth search of the gameObjects
	 * in this object and returns a list with those
	 * whose name matched the keyName param

	 * @param keyName search key
	 * @return list of objects with given keyname
	 */
	public List<GameNode> findGameObjects (String keyName) {
		List<GameNode> objects = new ArrayList<GameNode>();
		Stack<GameNode> stack = new Stack<GameNode>();
		stack.push(this);
		while (!stack.isEmpty()) {
			GameNode eval = stack.pop();
			if (eval.getKeyName().equals(keyName))
				objects.add(eval);
			for (GameNode obj : eval.children)
				stack.push(obj);
		}
		return objects;
	}
	
	/**
	 * get the squared distance between two game objects in
	 * world space
	 * @param object
	 * @return distance
	 */
	public float distanceSquared (GameNode object) {
		Vector3f a = getWorldPosition();
		Vector3f b = object.getWorldPosition();
		return new Vector3f(a.x-b.x, a.y-b.y, a.z-b.z).lengthSquared();
	}
	
	/**
	 * VERY SLOW :(
	 * @return Model transformation matrix
	 */
	public Matrix4f getModelTransformation () {
		Matrix4f local = getLocalModelTransformation();
		Matrix4f ret = new Matrix4f();
		
		/* recursive multiplication */
		if (parent == null) ret = local;
		else Matrix4f.mul(parent.getModelTransformation(), local, ret);
		return ret;
	}
	
	/**
	 * return matrix (ROTATION * TRANSLATION) in
	 * local model space
	 * 
	 * @return local model view matrix
	 */
	public Matrix4f getLocalModelTransformation () {
		Matrix4f model = new Matrix4f();

		/* apply transformations */
		model.translate(new org.lwjgl.util.vector.Vector3f(dposition.x, dposition.y, dposition.z));
		model.rotate(drotationAxis.x, new org.lwjgl.util.vector.Vector3f(1,0,0));
		model.rotate(drotationAxis.y, new org.lwjgl.util.vector.Vector3f(0,1,0));
		model.rotate(drotationAxis.z, new org.lwjgl.util.vector.Vector3f(0,0,1));
		return model;
	}
	
	/**
	 * returns the world position with all translations
	 * applied. This method doesn't require matrix multiplication
	 * so it should be cheaper to execute. Still, slow
	 * 
	 * P(object) = object.position;					if parent == null
	 * P(object) = object.position + P(parent);		otherwise
	 * 
	 * @return 3d world position
	 */
	public Vector3f getWorldPosition () {
		/* position */
		Vector4f pos = new Vector4f();
		
		/* apply transformation */
		Matrix4f.transform(getModelTransformation(), new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), pos);
		
		/* return vec3 */
		return new Vector3f(pos.x, pos.y, pos.z);
	}
	
	/**
	 * Send a message to this game object
	 * @param message message being sent
	 */
	public void sendMessage (Object message) {
		/* communicate to all the listeners */
		for (IMessageListener list : messageListeners)
			list.messageReceived(message);
	}
	
	/**
	 * @param child to be added
	 */
	public void addChild (GameNode child) {
		child.isolate();
		this.children.add(child);
		child.parent = this;
	}

	/**
	 * @param children to be removed
	 */
	public void removeChild (GameNode child) {
		if (child == null || child.parent != this)
			throw new IllegalArgumentException();
		this.children.remove(child);
		child.parent = null;
	}
	
	/**
	 * This method breaks the relationship between
	 * this GameObject and its parent.
	 * What happens is:
	 * 
	 * 1. parent loses his child
	 * 2. GameObject loses his parent
	 * 
	 * Tragic, isn't it?
	 */
	public void isolate () {
		if (this.parent != null) {
			this.parent.removeChild(this);
			this.parent = null;
		}
	}
	
	/**
	 * Change the visibility of the object
	 * @param b new state
	 */
	public void setVisible (boolean b) {
		this.visible = b;
	}
	
	/**
	 * set the debug value
	 * @param b new state
	 */
	public void setDebug (boolean b) {
		this.debug = b;
	}
	
	/**
	 * return debug state
	 * @return
	 */
	public boolean isDebug () {
		return debug;
	}
	
	/**
	 * gets the total number of children for this
	 * GameObject node
	 * 
	 * @return children count
	 */
	public int getChildrenCount () {
		return children.size();
	}
	
	public List<GameNode> getChildren () {
		return new ArrayList<GameNode>(children);
	}
	
	/**
	 * JAVA8 - Return a stream of the children
	 * of this game object
	 * 
	 * @return Stream of children
	 */
	public Stream<GameNode> getChildrenStream () {
		return children.stream().filter(object -> object.parent == this);
	}
	
	/**
	 * @return Parent object (might be null)
	 */
	public GameNode getParent () {
		return parent;
	}
	
	/**
	 * returns this GameObject's keyName
	 * @return key name
	 */
	public String getKeyName () {
		return keyName;
	}
	
	/**
	 * Set Node position in local space
	 * @param x new x position
	 * @param y new y position
	 * @param z new z position
	 */
	public void setPosition (float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	/**
	 * @param pos
	 */
	public void setPosition (Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	/**
	 * get position
	 * @return vector3f vector
	 */
	public Vector3f getPosition () {
		return new Vector3f(position);
	}

	/**
	 * Set Node rotation in local space
	 * @param x new x rotation
	 * @param y new y rotation
	 * @param z new z rotation
	 */
	public void setRotation (float x, float y, float z) {
		rotationAxis.x = x;
		rotationAxis.y = y;
		rotationAxis.z = z;
	}
	
	/**
	 * @param rot
	 */
	public void setRotation (Vector3f rot) {
		setRotation(rot.x, rot.y, rot.z);
	}
	
	/**
	 * get rotation
	 * @return vector3f vector
	 */
	public Vector3f getRotation () {
		return new Vector3f(rotationAxis);
	}
	
	/**
	 * return the visibility of the
	 * game object
	 * @return visibility
	 */
	public boolean isVisible () {
		return visible;
	}
	
	public void writesShadow (boolean b) {
		writeShadow = b;
	}
	
	/**
	 * Called once every cycle of the
	 * main loop. Only visible objects are
	 * updated
	 */
	public void update () {
		if (!visible)
			return;
		
		try {
			/* interpolate stuff */
			float dt = Framework.getInstance().getDeltaTime();
			
			/* smooth translation */
			if (transSmooth > 0) {
				dposition.x += (position.x - dposition.x) * transSmooth * dt;
				dposition.y += (position.y - dposition.y) * transSmooth * dt;
				dposition.z += (position.z - dposition.z) * transSmooth * dt;
			} else {
				dposition.x = position.x;
				dposition.y = position.y;
				dposition.z = position.z;
			}
			
			/* smooth translation */
			if (rotSmooth > 0) {
				float drx = rotationAxis.x - drotationAxis.x;
				float dry = rotationAxis.y - drotationAxis.y;
				float drz = rotationAxis.z - drotationAxis.z;
				
				/* hard steps */
				while (dry > Math.PI) {
					rotationAxis.y -= 2*Math.PI;
					dry = rotationAxis.y - drotationAxis.y;
				}
				while (dry < -Math.PI) {
					rotationAxis.y += 2*Math.PI;
					dry = rotationAxis.y - drotationAxis.y;
				}
				while (drx > Math.PI) {
					rotationAxis.x -= 2*Math.PI;
					drx = rotationAxis.x - drotationAxis.x;
				}
				while (drx < -Math.PI) {
					rotationAxis.x += 2*Math.PI;
					drx = rotationAxis.x - drotationAxis.x;
				}
				while (drz > Math.PI) {
					rotationAxis.z -= 2*Math.PI;
					drz = rotationAxis.z - drotationAxis.z;
				}
				while (drz < -Math.PI) {
					rotationAxis.z += 2*Math.PI;
					drz = rotationAxis.z - drotationAxis.z;
				}
				
				/* update position */
				drotationAxis.x += drx * rotSmooth * dt;
				drotationAxis.y += dry * rotSmooth * dt;
				drotationAxis.z += drz * rotSmooth * dt;
			} else {
				drotationAxis.x = rotationAxis.x;
				drotationAxis.y = rotationAxis.y;
				drotationAxis.z = rotationAxis.z;
			}
			
			/* update game controllers */
			List<IGameUpdater> updatersSafe = new ArrayList<IGameUpdater>(updaters);
			for (IGameUpdater controller : updatersSafe)
				controller.update(this);
			
			/* update children objects */
			List<GameNode> objs = new ArrayList<GameNode>(children);
			for (GameNode obj : objs)
				if (obj.parent == this)
					obj.update();
			
			/* clear list */
			objs.clear();
			updatersSafe.clear();
		} catch (Exception e) {
			// do nothing here
			e.printStackTrace();
		}
	}
	
	/**
	 * Called once every frame to render
	 * the game object.
	 * If the game object is not visible, it is
	 * not renderer.
	 */
	public void render (boolean shadowPass, boolean gPass) {
		// render only the children whose parent is still
		// this one. The usage of the Stream API and lambda
		// expressions is probably not appropriate
		
		/*
		 * if this GameObject is not visible, then avoid 
		 * it and all of its children right away
		 * */
		
		if (!visible || (shadowPass && !writeShadow))
			return;

		try {
			Scene scene = Scene.getInstance();
			CameraNode camera = scene.getUsedCamera();
			
			/* push a new matrix to the model stack */
			MATRIX_STACK.push();
			Matrix4f localModel = getLocalModelTransformation();
			MATRIX_STACK.transform(localModel);
			
			/* if there is a renderer then calculare everything */
			if ((!renderers.isEmpty() || debug && debugRenderer != null) && ( gPass && writeGbuffer || !gPass )) {
				
				/* view matrix */
				Matrix4f viewMatrix = new Matrix4f();
				if (camera != null)
					viewMatrix = camera.getViewMatrix();
					
				/* model matrix */
				Matrix4f modelMatrix = MATRIX_STACK.getTop();
				
				/* model view projection matrix */
				Matrix4f viewProj = camera.getViewProjectionMatrix();
				Matrix4f modelViewProj = new Matrix4f();//LinearUtils.getModelViewProjection(projMatrix, viewMatrix, modelMatrix);
				Matrix4f.mul(viewProj, modelMatrix, modelViewProj);
				
				/* model view matrix */
				Matrix4f modelView = LinearUtils.getModelViewMatrix(modelMatrix, viewMatrix);
				
				/* debug render */
				if (debug && !shadowPass && debugRenderer != null) {
					/* push attributes to avoid problems */
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					debugRenderer.render(modelViewProj, modelView, viewMatrix);
					/* pop attributes */
					GL11.glPopAttrib();
				}
				
				if (!renderers.isEmpty()) {
					/* render renderers */
					List<IGameRenderer> safeRenderers = new ArrayList<IGameRenderer>(renderers);
					for (IGameRenderer render : safeRenderers) {
						/* push attributes to avoid problems */
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						render.render(modelViewProj, modelView, viewMatrix);
						/* pop attributes */
						GL11.glPopAttrib();
					}
					safeRenderers.clear();
				}
			}
			
			// copy everything in a new list to avoid
			// concurrent modification exceptions
			List<GameNode> objs = new ArrayList<GameNode>(children);
			for (GameNode obj : objs)
				if (obj.parent == this)
					obj.render(shadowPass, gPass);
			objs.clear();
			
			/* pop matrix from stack */
			MATRIX_STACK.pop();
		} catch (Exception e) {
			// do nothing here
			e.printStackTrace();
		}
	}
}
