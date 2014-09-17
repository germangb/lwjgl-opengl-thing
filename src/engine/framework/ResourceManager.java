package engine.framework;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

/**
 * @author germangb
 *
 */
public class ResourceManager {

	/**
	 * Queue containing all the remaining resources
	 * that need to be loaded
	 */
	private static Queue<IResourceLoader> resources = new LinkedList<IResourceLoader>();
	private static Queue<IResourceLoader> toBeClean = new LinkedList<IResourceLoader>();
	
	/**
	 * Loads the resources from the queue
	 */
	public static void loadResources () {
		Framework frame = Framework.getInstance();
		while (!resources.isEmpty()) {
			/* loads resources and logs a message */
			IResourceLoader loader = resources.poll();
			toBeClean.add(loader);
			GL11.glGetError();	// clear error
			if (loader.loadResources())
				frame.getLogStream().print("[SUCCESSFUL] ");
			else
				frame.getLogStream().print("[FAILED] ");
			frame.getLogStream().println("Loading resource ... "+loader);
		}
	}
	
	/**
	 * Called when resources need to be released
	 * Textures, Shader, FrameBuffer, VertexBuffers...
	 */
	public static void cleanResources () {
		Framework frame = Framework.getInstance();
		while (!toBeClean.isEmpty()) {
			/* loads resources and logs a message */
			IResourceLoader clean = toBeClean.poll();
			frame.getLogStream().println("Cleaning resource ... "+clean);
			clean.cleanResources();
		}
	}
	
	/**
	 * Add a new resource loader to the resources
	 * queue to be loaded in the next frame
	 * @param res
	 */
	public static void addResources (IResourceLoader res) {
		resources.add(res);
	}

}
