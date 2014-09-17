package engine.nodes;

import org.lwjgl.util.vector.Matrix4f;

import engine.framework.LinearUtils;

/**
 * @author germangb
 *
 */
public class OrthogonalCamera extends CameraNode {
	
	/*
	 * Camera properties
	 * */

	private Matrix4f matrix;
	private float near;
	private float far;
	
	/**
	 * @param keyName GameObject name
	 * @param fov Field Of View
	 * @param aspect Aspect Ratio
	 * @param near Near clipping plane
	 * @param far Far clipping plane
	 */
	public OrthogonalCamera(String keyName, float left, float right, float down, float up, float near, float far) {
		super(keyName);
		this.matrix = LinearUtils.ortho(left, right, down, up, near, far);
		this.near = near;
		this.far = far;
	}
	
	public float near () {
		return near;
	}
	
	public float far () {
		return far;
	}
	
	//
	// Camera abstract implementation
	//

	@Override
	public Matrix4f getProjectionMatrix() {
		return matrix;
	}
	
	//
	// END
	//
}
