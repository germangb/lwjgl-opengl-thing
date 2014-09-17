package engine.framework;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.nodes.CameraNode;

/**
 * @author germangb
 * 
 */
public class LinearUtils {

	/**
	 * return the reflected vector along a given normal
	 * @param u vector to be reflected
	 * @param normal normal vector
	 * @return reflected vector
	 */
	public static Vector3f reflect(Vector3f u, Vector3f normal) {
		Vector3f reflect = new Vector3f();
		Vector3f d = new Vector3f(-u.x, -u.y, -u.z);
		float aux = 2.0f * d.dot(normal);
		reflect.x = d.x - aux*normal.x;
		reflect.y = d.y - aux*normal.y;
		reflect.z = d.z - aux*normal.z;
		return reflect;
	}
	
	public static Vector3f[] unProject (int winX, int winY) {
		CameraNode camera = Scene.getInstance().getUsedCamera();
		int w = Framework.getInstance().getWindowWidth();
		int h = Framework.getInstance().getWindowHeight();
		FloatBuffer near = BufferUtils.createFloatBuffer(3);
		FloatBuffer far = BufferUtils.createFloatBuffer(3);
		FloatBuffer view = BufferUtils.createFloatBuffer(16);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		IntBuffer viewPort = BufferUtils.createIntBuffer(4);
		camera.getViewMatrix().store(view);
		camera.getProjectionMatrix().store(projection);
		viewPort.put(new int[] {0, 0, w, h});
		view.flip();
		projection.flip();
		viewPort.flip();
		GLU.gluUnProject(winX, winY, 0, view, projection, viewPort, near);
		GLU.gluUnProject(winX, winY, 1, view, projection, viewPort, far);
		Vector3f dir = new Vector3f();
		dir.x = far.get(0) - near.get(0);
		dir.y = far.get(1) - near.get(1);
		dir.z = far.get(2) - near.get(2);
		dir.normalize();
		Vector3f pos = new Vector3f(near.get(0), near.get(1), near.get(2));
		near.clear();
		far.clear();
		view.clear();
		projection.clear();
		viewPort.clear();
		return new Vector3f[]{pos, dir};
	}
	
	/**
	 * map object coordinates to window coordinates
	 * @param obj to be mapped
	 * @return mapped in window coords
	 */
	public static Vector3f project (Vector3f obj) {
		CameraNode camera = Scene.getInstance().getUsedCamera();
		int w = Framework.getInstance().getWindowWidth();
		int h = Framework.getInstance().getWindowHeight();
		Vector3f ret = new Vector3f();
		FloatBuffer view = BufferUtils.createFloatBuffer(16);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		IntBuffer viewPort = BufferUtils.createIntBuffer(4);
		camera.getViewMatrix().store(view);
		camera.getProjectionMatrix().store(projection);
		viewPort.put(new int[] {0, 0, w, h});	// normalized
		view.flip();
		projection.flip();
		viewPort.flip();
		FloatBuffer out = BufferUtils.createFloatBuffer(3);
		GLU.gluProject(obj.x, obj.y, obj.z, view, projection, viewPort, out);
		ret.x = out.get(0);
		ret.y = out.get(1);
		ret.z = out.get(2);
		view.clear();
		projection.clear();
		viewPort.clear();
		out.clear();
		return ret;
	}
	
	/**
	 * @param fov Field of view
	 * @param aspect Aspect ratio
	 * @param near near clipping plane
	 * @param far far clipping plane
	 * @return
	 */
	public static Matrix4f perspective(float fov, float aspect, float near,
			float far) {
		Matrix4f mat = new Matrix4f();
		mat.setZero();
		float cot = 1 / (float) Math.tan(fov / 2);
		mat.m00 = cot / aspect;
		mat.m11 = cot;
		mat.m22 = (near + far) / (near - far);
		mat.m23 = -1;
		mat.m32 = (2 * far * near) / (near - far);
		return mat;
	}

	/**
	 * @param left left
	 * @param right right
	 * @param bottom bottom
	 * @param top top
	 * @param near near clipping plane
	 * @param far far clipping plane
	 * @return
	 */
	public static Matrix4f ortho(float left, float right, float bottom,
			float top, float near, float far) {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		mat.m00 = 2 / (right - left);
		mat.m11 = 2 / (top - bottom);
		mat.m22 = -2 / (far - near);
		mat.m30 = -(right + left) / (right - left);
		mat.m31 = -(top + bottom) / (top - bottom);
		mat.m32 = -(far + near) / (far - near);
		return mat;
	}

	/**
	 * @param projection projection matrix
	 * @param view view matrix
	 * @param model model matrix
	 * @return returns ModelViewProjection matrix
	 */
	public static Matrix4f getModelViewProjection(Matrix4f projection, Matrix4f view, Matrix4f model) {
		Matrix4f mvp = new Matrix4f();
		Matrix4f mv = new Matrix4f();
		Matrix4f.mul(view, model, mv);
		Matrix4f.mul(projection, mv, mvp);
		return mvp;
	}
	
	public static Matrix4f getModelViewProjection(CameraNode camera, GameNode object) {
		Matrix4f vp = camera.getViewProjectionMatrix();
		Matrix4f mvp = new Matrix4f();
		Matrix4f.mul(vp, object.getModelTransformation(), mvp);
		return mvp;
	}
	
	/**
	 * @param projection projection matrix
	 * @param view view matrix
	 * @param model model matrix
	 * @return
	 */
	public static Matrix4f getMVP (Matrix4f projection,
			Matrix4f view, Matrix4f model) {
		return getModelViewProjection(projection, view, model);
	}

	/**
	 * @param model model matrix
	 * @param view view matrix
	 * @return model view matrix
	 */
	public static Matrix4f getModelViewMatrix(Matrix4f model, Matrix4f view) {
		Matrix4f mv = new Matrix4f();
		Matrix4f.mul(view, model, mv);
		return mv;
	}
}
