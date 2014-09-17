package engine.nodes;

import org.lwjgl.util.vector.Matrix4f;

import engine.framework.LinearUtils;

/**
 * @author germangb
 *
 */
public class PerspectiveCamera extends CameraNode {
	
	/*
	 * Camera properties
	 * */
	private Matrix4f matrix;
	private float fov;
	private float aspect;
	private float near;
	private float far;
	
	/**
	 * @param keyName GameObject name
	 * @param fov Field Of View
	 * @param aspect Aspect Ratio
	 * @param near Near clipping plane
	 * @param far Far clipping plane
	 */
	public PerspectiveCamera(String keyName, float fov,
			float aspect, float near, float far) {
		super(keyName);
		this.matrix = LinearUtils.perspective(fov, aspect, near, far);
		this.near = near;
		this.far = far;
		this.aspect = aspect;
		this.fov = fov;
	}
	
	public float fov () {
		return fov;
	}
	
	public float aspectRatio () {
		return aspect;
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
