package lab;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Matrix4f;

/**
 * @author germangb
 *
 */
public class Bone {
	
	/* hierarchy */
	private Bone parent;
	private List<Bone> children;
	
	/* local transformations */
	private Vector3f position;
	private Vector3f rotation;
	private Matrix4f local;
	private Matrix4f global;
	private boolean dirty;
	
	/**
	 * Default constructor
	 * Set everything to 0
	 * Instantiate all the resources
	 */
	public Bone () {
		this(0, 0, 0, 0, 0, 0);
		this.dirty = false;
	}
	
	/**
	 * @param x position X
	 * @param y position Y
	 * @param z position Z
	 * @param rx rotation along X
	 * @param ry rotation along Y
	 * @param rz rotation along Z
	 */
	public Bone (float x, float y, float z, float rx, float ry, float rz) {
		this.parent = null;
		this.children = new ArrayList<Bone>();
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.local = new Matrix4f();
		this.global = new Matrix4f();
		this.local.setIdentity();
		this.global.setIdentity();
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.rotation.x = rx;
		this.rotation.y = ry;
		this.rotation.z = rz;
		this.dirty = true;
	}
	
	/**
	 * add child to the hierarchy
	 * @param bone
	 */
	public void addChild (Bone bone) {
		bone.parent = this;
		this.children.add(bone);
	}
	
	/**
	 * set the bone position
	 * @param x new position X
	 * @param y new position Y
	 * @param z new position Z
	 */
	public void setPosition (float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.dirty = true;
	}
	
	/**
	 * increment bone position
	 * @param dx X increment
	 * @param dy Y increment
	 * @param dz Z increment
	 */
	public void translate (float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		this.dirty = true;
	}
	
	/**
	 * set the bone rotation along
	 * OX, OY and OZ
	 * @param x new rotation X
	 * @param y new rotation Y
	 * @param z new rotation Z
	 */
	public void setRotation (float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
		this.dirty = true;
	}
	
	/**
	 * increment bone rotation along OX, OY and OZ
	 * @param dx X increment
	 * @param dy Y increment
	 * @param dz Z increment
	 */
	public void rotate (float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
		this.dirty = true;
	}
	
	/**
	 * recalculate transform matrix
	 */
	public void recalculate () {
		dirty = true;
	}
	
	/**
	 * calculate transformation matrix
	 * given position and rotation
	 */
	private void updateTransformation () {
		if (dirty) {
			float x = position.x;
			float y = position.y;
			float z = position.z;
			local.translate(new org.lwjgl.util.vector.Vector3f(x, y, z));
			local.rotate(rotation.x, new org.lwjgl.util.vector.Vector3f(1, 0, 0));
			local.rotate(rotation.x, new org.lwjgl.util.vector.Vector3f(0, 1, 0));
			local.rotate(rotation.x, new org.lwjgl.util.vector.Vector3f(0, 0, 1));
			dirty = false;
		}
	}
	
	/**
	 * get the transformation matrix
	 * for this bone
	 * @return matrix reference
	 */
	public Matrix4f getLocalTransformation () {
		updateTransformation();
		return local;
	}
	
	/**
	 * WARNING: LACKS PROPER OPTIMIZATION
	 * get global transformation
	 * @return transformation matrix
	 */
	public Matrix4f getTransformation () {
		if (parent == null)
			return getLocalTransformation();
		Matrix4f mul = new Matrix4f();
		/* multiply hierarchy */
		Matrix4f.mul(
				getLocalTransformation(),
				parent.getTransformation(),
				mul);
		return mul;
	}

}
