package engine.resources;

import java.io.InputStream;
import java.net.URL;

/**
 * @author germangb
 *
 */
public class Resources {

	/**
	 * Get resource from "resources" package
	 * @param res file
	 * @return resource url
	 */
	public static URL get (String res) {
		return Resources.class.getResource(res);
	}
	
	/**
	 * Get resource from "resources" package
	 * @param res resource file
	 * @return input stream resource
	 */
	public static InputStream getAsInputStream (String res) {
		return Resources.class.getResourceAsStream(res);
	}
	
}
