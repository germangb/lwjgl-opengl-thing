package engine.graphics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import engine.framework.IResourceLoader;
import engine.framework.ResourceManager;

/**
 * @author germangb
 *
 */
public class Shader implements IResourceLoader {
	
	/**
	 * CONVENTIONS:
	 *   - position attribute is always in position 0
	 *   - position normal is always in position 1
	 *   - position tex coord (uv) is always in position 2
	 *   - color attribute is always in position 3
	 */
	
	public static final int POSITION_ATTRIB = 0;
	public static final int NORMAL_ATTRIB = 1;
	public static final int UV0_ATTRIB = 2;
	public static final int COLOR_ATTRIB = 3;
	
	/* last used program */
	/* avoid rebinding the same shader */
	private static int used = -1;
	
	/**
	 * Instantiate a shader given the path of the vertex and fragment
	 * files
	 * 
	 * @param vert Vertex shader path
	 * @param frag Fragment shader path
	 * @return Shader instance
	 */
	public static Shader fromFile (String vert, String frag) {
		Shader shader = new Shader(vert, frag);
		ResourceManager.addResources(shader);
		return shader;
	}
	
	public static Shader fromUrl (URL vert, URL frag) {
		System.out.println(vert+" - "+frag);
		Shader shader = new Shader(vert.getFile(), frag.getFile());
		ResourceManager.addResources(shader);
		return shader;
	}
	
	// shader attributes
	private int id;
	private int vertShader;
	private int fragShader;
	private String vertSource;
	private String fragSource;
	
	/**
	 * Constructs a shader given the source location
	 * 
	 * @param context
	 * @param vert Vertex source path
	 * @param frag Fragment source path
	 */
	public Shader(String vert, String frag) {
		vertSource = vert;
		fragSource = frag;
	}

	/**
	 * Utility method used only internally
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private String getFileContent (String file) throws FileNotFoundException {
		StringBuilder str = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Scanner scan = new Scanner(reader);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			str.append(line+'\n');
		}
		scan.close();
		return str.toString();
	}
	
	/**
	 * Bind program shader
	 */
	public void bind () {
		if (used != id) {
			used = id;
			GL20.glUseProgram(id);
		}
	}
	
	/**
	 * @return OpenGL program
	 */
	public int getProgram () {
		return id;
	}

	//
	// BEGIN IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* reset error */
		GL11.glGetError();
		
		vertShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		String vertSrc = null;
		String fragSrc = null;
		try {
			vertSrc = getFileContent(vertSource);
			fragSrc = getFileContent(fragSource);
		} catch (FileNotFoundException e) {
			//Framework game = Framework.getInstance();
			//PrintStream err = game.getErrStream();
			//err.println(e.getMessage());
			return false;
		}
		
		GL20.glShaderSource(vertShader, vertSrc);
		GL20.glCompileShader(vertShader);
		GL20.glShaderSource(fragShader, fragSrc);
		GL20.glCompileShader(fragShader);
		
		String vertLog = GL20.glGetShaderInfoLog(vertShader, 1024);
		String fragLog = GL20.glGetShaderInfoLog(fragShader, 1024);	
		
		if (vertLog.length() > 0)
			throw new RuntimeException("[GLSL-VERT-ERR]: "+vertLog);
		if (fragLog.length() > 0)
			throw new RuntimeException("[GLSL-FRAG-ERR]: "+fragLog);
		
		
		if (vertLog.length() > 0 || fragLog.length() > 0)
			return false;
			
		id = GL20.glCreateProgram();
		
		GL20.glAttachShader(id, vertShader);
		GL20.glAttachShader(id, fragShader);
		GL20.glBindAttribLocation(id, POSITION_ATTRIB, "vPosition");
		GL20.glBindAttribLocation(id, NORMAL_ATTRIB, "vNormal");
		GL20.glBindAttribLocation(id, UV0_ATTRIB, "vUv");
		GL20.glBindAttribLocation(id, COLOR_ATTRIB, "vColor");
		GL20.glLinkProgram(id);
		
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL20.glDetachShader(this.id, this.vertShader);
		GL20.glDetachShader(this.id, this.fragShader);
		GL20.glDeleteShader(this.vertShader);
		GL20.glDeleteShader(this.fragShader);
		GL20.glDeleteProgram(this.id);
	}
	
	//
	// END
	//
	
}
