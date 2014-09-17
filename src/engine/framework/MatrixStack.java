package engine.framework;

import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author germangb
 *
 */
public class MatrixStack {
	
	/**
	 * stacked matrix
	 */
	private Stack<Matrix4f> stack;
	
	/**
	 * Default constructor
	 */
	public MatrixStack () {
		this.stack = new Stack<Matrix4f> ();
		this.stack.push(new Matrix4f());
	}
	
	/**
	 * Set identity matrix to the peek of
	 * the stack
	 */
	public void loadIdentity () {
		this.stack.peek().setIdentity();
	}
	
	/**
	 * @param mat multiply the top of the stack
	 */
	public void transform (Matrix4f mat) {
		Matrix4f peek = stack.peek();
		Matrix4f.mul(peek, mat, peek);
	}
	
	/**
	 * @param u
	 */
	public void translate (Vector3f u) {
		Matrix4f trans = new Matrix4f();
		trans.translate(u);
		transform(trans);
	}
	
	/**
	 * @param rad
	 * @param axis
	 */
	public void rotate (float rad, Vector3f axis) {
		Matrix4f trans = new Matrix4f();
		trans.rotate(rad, axis);
		transform(trans);
	}
	
	/**
	 * @param u
	 */
	public void scale (Vector3f u) {
		Matrix4f trans = new Matrix4f();
		trans.scale(u);
		transform(trans);
	}
	
	/**
	 * push a new matrix to the stack
	 */
	public void push () {
		stack.push(new Matrix4f(stack.peek()));
	}
	
	/**
	 * @return return poped matrix
	 */
	public Matrix4f pop () {
		return stack.pop();
	}
	
	/**
	 * @return return top of the stack
	 */
	public Matrix4f getTop () {
		return stack.peek();
	}
	
}
