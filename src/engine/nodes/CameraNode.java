package engine.nodes;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.SoundSource;
import engine.framework.Framework;
import engine.framework.Scene;

/**
 * @author germangb
 *
 */
public abstract class CameraNode extends GameNode {
	
	/* avoid calculating the VP matrix */
	/* this attributes will make sure */
	/* only one multiplication per frame is performed */
	private Matrix4f viewProjection = new Matrix4f();
	private int viewProjTick = -1;
	private boolean viewDirty;
	private Matrix4f viewMatrix;
	
	/**
	 * initializes a camera with a given keyName
	 * that is passed as the GameObject keyName
	 * 
	 * @param keyName name of the camera object
	 */
	public CameraNode (String keyName) {
		super(keyName);
		viewDirty = false;
		viewMatrix = new Matrix4f();
	}
	
	//
	// GameNode re-implementation
	//
	
	@Override
	public void setPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		viewDirty = true;
	}

	@Override
	public void setRotation(float x, float y, float z) {
		super.setRotation(x, y, z);
		viewDirty = true;
	}
	
	@Override
	public void update () {
		/* update sound listener */
		if (Scene.getInstance().getUsedCamera() == this) {
			Vector3f listenerPos = getWorldPosition();
			Vector3f look = getLook();
			Vector3f up = getUp();
			SoundSource.setListenerPosition(listenerPos.x, listenerPos.y, listenerPos.z);
			SoundSource.setListenerOrientation(look.x, look.y, look.z, up.x, up.y, up.z);
		}
		super.update();
	}
	
	//
	// END
	//

	/**
	 * @return
	 */
	public abstract Matrix4f getProjectionMatrix (); 
	
	/**
	 * calculate view projection matrix
	 * @return
	 */
	public Matrix4f getViewProjectionMatrix () {
		if (Framework.getInstance().getTicks() != viewProjTick) {
			Matrix4f.mul(getProjectionMatrix(), getViewMatrix(), viewProjection);
			viewProjTick = Framework.getInstance().getTicks();
		}
		return viewProjection;
	}
	
	/**
	 * @return
	 */
	public Matrix4f getViewMatrix () {
		if (viewDirty) {
			viewDirty = false;
			Vector3f position = getPosition();
			Vector3f rotation = getRotation();
			viewMatrix.setIdentity();
			viewMatrix.rotate(rotation.x, new org.lwjgl.util.vector.Vector3f(1,0,0));
			viewMatrix.rotate(rotation.y, new org.lwjgl.util.vector.Vector3f(0,1,0));
			viewMatrix.rotate(rotation.z, new org.lwjgl.util.vector.Vector3f(0,0,1));
			viewMatrix.translate(new org.lwjgl.util.vector.Vector3f(-position.x, -position.y, -position.z));
		}
		
		return viewMatrix;
	}
		
	/**
	 * get look direction
	 * @return
	 */
	public Vector3f getLook () {
		Vector3f rot = getRotation();
		float x = (float) (Math.sin(rot.y) * Math.cos(rot.x));
		float y = -(float) Math.sin(rot.x);
		float z = -(float) (Math.cos(rot.y) * Math.cos(rot.x));
		return new Vector3f(x, y, z);
	}
	
	/**
	 * get Up vector
	 * @return
	 */
	public Vector3f getUp () {
		Vector3f rot = getRotation();
		float x = (float) (Math.sin(rot.x)*Math.sin(rot.y));
		float y = (float) Math.cos(rot.x);
		float z = -(float) (Math.sin(rot.x)*Math.cos(rot.y));
		return new Vector3f(x, y, z);
	}
}
