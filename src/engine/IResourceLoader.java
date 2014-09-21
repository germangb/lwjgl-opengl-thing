package engine;

/**
 * @author germangb
 * 
 * Some stuff needs a context prior to its
 * creation. This framework loads stuff when the required
 * contexts have been created. The system will load
 * OpenGL objects and OpenAL sources primarily
 */
public interface IResourceLoader {
	
	/**
	 * Called by the resource manager when
	 * resources are ready to be created
	 */
	public boolean loadResources ();
	
	/**
	 * Called when at the end of the program
	 * to clean up all the used resources
	 */
	public void cleanResources ();
	
}
