package engine;

import org.lwjgl.util.vector.Matrix4f;

/**
 * @author germangb
 *
 */
public interface IGameRenderer {

	/**
	 * Called to render game object
	 * @param mvp ModelViewProjection matrix
	 * @param mv ModelView matrix
	 * @param v View matrix
	 */
	public void render (Matrix4f mvp, Matrix4f mv, Matrix4f v);
	
}
