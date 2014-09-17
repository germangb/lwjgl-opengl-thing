package engine.framework;

/**
 * @author germangb
 * 
 * Some stuff needs a context prior to its
 * creation. This framework allows a proper resource-loading
 * system that will load OpenGL objects and OpenAL sources
 * when the corresponding context have been created
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
