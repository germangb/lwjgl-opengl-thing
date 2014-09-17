package engine;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import engine.framework.IResourceLoader;
import engine.framework.ResourceManager;
import engine.graphics.Texture;

/**
 * @author germangb
 *
 */
public class Decal implements IResourceLoader {

	private String path;
	private Texture texture;
	
	/**
	 * create a decal given its
	 * xml file
	 * @param path xml file path
	 */
	public Decal(String path) {
		this.path = path;
		ResourceManager.addResources(this);
	}
	
	/**
	 * decal texture
	 * @return texture instance
	 */
	public Texture getTexture () {
		return texture;
	}
	
	//
	// IResourceLoader interface implementation
	//

	@Override
	public boolean loadResources() {
		File xml = new File(this.path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();
			Element decal = (Element) doc.getElementsByTagName("decal").item(0);
			String p = decal.getElementsByTagName("texture").item(0).getTextContent();
			texture = Texture
					.fromFile(p)
					.setFilter(GL11.GL_LINEAR, GL11.GL_LINEAR);
		} catch (Exception e) {
			/* some error, so return false report */
			return false;
		}
				
		/* Everything went just fine! */
		return true;
	}

	@Override
	public void cleanResources() {
		// do nothing here
	}
	
	//
	//
	//

}
