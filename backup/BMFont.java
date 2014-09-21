package engine.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.IResourceLoader;
import engine.ResourceManager;
import engine.graphics.*;

/**
 * @author germangb
 *
 */
public class BMFont implements IResourceLoader {
	
	/**
	 * Maximum amount of chars to be rendered at once.
	 * This will limit the vertex buffer size.
	 * */
	private static boolean renderLoaded = false;
	private Shader SHADER = Shader.fromFile("shaders/font.vert", "shaders/font.frag");
	private static int MAX_CHARS = 128;
	private static FloatBuffer renderBuffer;
	
	/**
	 * Ubuntu font
	 */
	public static final BMFont UBUNTU = new BMFont("fonts/ubuntu/ubuntu.fnt");

	private String location, path;
	private HashMap<String, String> info;
	private HashMap<String, String> common;
	private ArrayList<Texture> pages;
	private HashMap<Integer, CharData> chars;
	
	/**
	 * @param location
	 */
	private BMFont (String location) {
		ResourceManager.addResources(this);
		pages = new ArrayList<Texture>();
		info = new HashMap<String, String>();
		common = new HashMap<String, String>();
		chars = new HashMap<Integer, CharData>();
		String[] dir = location.split("/");
		path = location.split('/'+dir[dir.length-1])[0];
		this.location = location;
		
	}
	
	/**
	 * remove blank space that might break the parser
	 * @return
	 */
	private String cleanStringFormat (String str) {
		String[] sp = str.split("\"");
		String ret = "";
		for (int i = 0; i < sp.length; ++i)
			if (i%2==1) {
				ret += "\""+sp[i].replace(' ', '_')+"\"";
			} else ret += sp[i];
		return ret;
	}
	
	/**
	 * @param info
	 */
	private void scanInfo (String info) {
		Scanner scan = new Scanner(info);
		scan.next();
		while (scan.hasNext()) {
			String prop[] = scan.next().split("=");
			String name = prop[0];
			String value = prop[1];
			if (value.charAt(0) == '"')
				value = value.substring(1, value.length()-1);
			this.info.put(name, value);
		}
		scan.close();
	}
	
	/**
	 * @param common
	 */
	private void scanCommon (String common) {
		Scanner scan = new Scanner(common);
		scan.next();
		while (scan.hasNext()) {
			String prop[] = scan.next().split("=");
			String name = prop[0];
			String value = prop[1];
			if (value.charAt(0) == '"')
				value = value.substring(1, value.length()-1);
			this.common.put(name, value);
		}
		scan.close();
	}
	
	/**
	 * @param page
	 */
	private void scanPage (String page) {
		Scanner scan = new Scanner(page);
		scan.next();
		scan.next();
		String file = scan.next().split("=")[1];
		file = file.substring(1, file.length()-1);
		pages.add(Texture.fromFile(path+'/'+file).setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST));
		scan.close();
	}
	
	/**
	 * @param chara
	 */
	private void scanChar (String chara) {
		Scanner scan = new Scanner(chara);
		scan.next();
		int id = Integer.parseInt(scan.next().split("=")[1]);
		this.chars.put(id, new CharData(chara));
		scan.close();
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String info (String key) {
		return info.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String common (String key) {
		return common.get(key);
	}
	
	/**
	 * @param text
	 * @return
	 */
	public int lineWidth (String text) {
		int acum = 0;
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			CharData data = chars.get((int)c);
			if (data==null) data = chars.get((int)'?');
			int xAdv = data.property("xadvance");
			acum += xAdv;
		}
		return acum;
	}
	
	/**
	 * @param c
	 * @return
	 */
	public boolean contains (char c) {
		CharData data = chars.get((int)c);
		return data != null;
	}

	/**
	 * @param text
	 */
	public void renderLine (String text, Matrix4f mvp) {
		if (!renderLoaded) return;
		int program = SHADER.getProgram();
		GL20.glUseProgram(program);
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		mvp.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, buffer);
		buffer.clear();
		
		int left = 0;
		int top = 0;
		int currentPage = -1;
		float scaleW = Float.parseFloat(common.get("scaleW"));
		float scaleH = Float.parseFloat(common.get("scaleH"));
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		int charCount = 0;
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (c == '\n') {
				left = 0;
				top += Integer.parseInt(common.get("lineHeight"));
				continue;
			}
			CharData data = chars.get((int)c);
			if (data==null) data = chars.get((int)'?');
			int w = data.property("width");
			int h = data.property("height");
			int xOffs = data.property("xoffset");
			int yOffs = data.property("yoffset");
			int xAdv = data.property("xadvance");
			int x = data.property("x");
			int y = data.property("y");
			int page = data.property("page");
			
			if (page != currentPage) {
				currentPage = page;
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, pages.get(page).getId());
				charCount = 0;
				renderBuffer.position(0);
			}
			
			int lineHeight = Integer.parseInt(common.get("lineHeight"));
			float scale = 0.05f;
			
			renderBuffer.put(new float[]{ scale*(left+xOffs), scale*(lineHeight-yOffs-top) });
			renderBuffer.put(new float[]{ x/scaleW, y/scaleH });
		
			renderBuffer.put(new float[]{ scale*(left+xOffs), scale*(lineHeight-(h+yOffs)-top) });
			renderBuffer.put(new float[]{ x/scaleW, (y+h)/scaleH });
			
			renderBuffer.put(new float[]{ scale*(left+w+xOffs), scale*(lineHeight-(h+yOffs)-top) });
			renderBuffer.put(new float[]{ (x+w)/scaleW, (y+h)/scaleH });
			
			renderBuffer.put(new float[]{ scale*(left+xOffs), scale*(lineHeight-yOffs-top) });
			renderBuffer.put(new float[]{ x/scaleW, y/scaleH });
			
			renderBuffer.put(new float[]{ scale*(left+w+xOffs), scale*(lineHeight-(h+yOffs)-top) });
			renderBuffer.put(new float[]{ (x+w)/scaleW, (y+h)/scaleH });
			
			renderBuffer.put(new float[]{ scale*(left+w+xOffs), scale*(lineHeight-yOffs-top) });
			renderBuffer.put(new float[]{ (x+w)/scaleW, y/scaleH });
			
			charCount++;
			if (charCount == MAX_CHARS) {
				charCount = 0;
				renderBuffer.position(0);
			}
			
			left+=xAdv;
		}
		GL11.glPopAttrib();
		if (currentPage != -1)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glUseProgram(0);
	}
	/**
	 * @author germangb
	 *
	 */
	private class CharData {
		
		private HashMap<String, Integer> props;
		
		/**
		 * @param args
		 */
		public CharData(String args) {
			props = new HashMap<String, Integer>();
			Scanner scan = new Scanner(args);
			scan.next();
			scan.next();
			while (scan.hasNext()) {
				String[] props = scan.next().split("=");
				String name = props[0];
				int value = Integer.parseInt(props[1]);
				this.props.put(name, value);
				//System.out.println(name+" -> "+value);
			}
			scan.close();
		}
		
		/**
		 * @param key
		 * @return
		 */
		public int property (String key) {
			return props.get(key);
		}
	}
	
	//
	// IResourceLoader interface implementation
	//

	@Override
	public boolean loadResources() {
		/* load rendering resources */
		if (!renderLoaded) {
			renderLoaded = true;
			renderBuffer = BufferUtils.createFloatBuffer(3*(2+2)*2*MAX_CHARS);
		}
		
		try {
			File file = new File(location);
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.startsWith("info"))
					scanInfo(cleanStringFormat(line));
				else if (line.startsWith("common"))
					scanCommon(line);
				else if (line.startsWith("page"))
					scanPage(line);
				else if (line.startsWith("char"))
					scanChar(line);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		/* cool, no problem dude */
		return true;
	}

	@Override
	public void cleanResources() {
		if (renderLoaded) {
			renderLoaded = false;
			renderBuffer.clear();
		}
	}
	
	//
	// END
	//
	
}