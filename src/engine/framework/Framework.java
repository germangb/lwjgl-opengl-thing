package engine.framework;

import java.awt.Canvas;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * @author germangb
 *
 */
public final class Framework {

	/**
	 * Full screen bit
	 */
	public static final int FULLSCREEN_BIT = 1 << 0;

	/**
	 * framework signature
	 */
	private static final String DEFAULT_WIN_TITLE = "germangb framework ~ alpha";	
	
	/**
	 * singleton instance
	 */
	private static Framework instance = null;
	
	/**
	 * @return singleton Framework instance
	 */
	public static Framework getInstance () {
		if (instance == null)
			instance = new Framework ();
		return instance;
	}
	
	/* properties and attributes */
	private int windowWidth;
	private int windowHeight;
	private String windowTitle;
	private long delta;
	private long time;
	private boolean[] mouseButtonState;
	private int flags;
	private boolean notStopped;
	private Canvas parentCanvas;
	
	/* output streams */
	private PrintStream logStream;
	private PrintStream errStream;
	
	/* mouse */
	private int mouseX, mouseY;
	private int mouseDX, mouseDY, mouseDWheel;
	private int ticks;
	
	/* input listeners */
	private Set<IKeyboardListener> keyListeners;
	private Set<IMouseListener> mouseListeners;
	
	/**
	 * Default constructor
	 */
	private Framework () {
		/* initialize */
		this.flags = 0;
		this.ticks = 0;
		this.mouseX = mouseY = 0;
		this.mouseDX = mouseDY = mouseDWheel = 0;
		this.windowWidth = 0;
		this.windowHeight = 0;
		this.windowTitle = DEFAULT_WIN_TITLE;
		this.delta = 0;
		this.time = -1;
		this.mouseButtonState = new boolean[8];
		this.keyListeners = new HashSet<IKeyboardListener>();
		this.mouseListeners = new HashSet<IMouseListener>();
		this.logStream = System.out;
		this.errStream = System.err;
		this.notStopped = true;
	}
	
	public void setParentCanvas (Canvas canvas) {
		this.parentCanvas = canvas;
	}
	
	/**
	 * @param listener to be added
	 */
	public void addKeyboardListener (IKeyboardListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		this.keyListeners.add(listener);
	}
	
	/**
	 * @param listener to be removed
	 */
	public void removeKeyboardListener (IKeyboardListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		this.keyListeners.remove(listener);
	}
	
	/**
	 * @param listener to be added
	 */
	public void addMouseListener (IMouseListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		this.mouseListeners.add(listener);
	}
	
	/**
	 * @param listener to be removed
	 */
	public void removeMouseListener (IMouseListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		this.mouseListeners.remove(listener);
	}
	
	/**
	 * set framework flags
	 * @param flags bits
	 */
	public void setFlags (int flags) {
		this.flags = flags;
	}
	
	/**
	 * get flag bits
	 * @return bits
	 */
	public int getFlags () {
		return flags;
	}
	
	/**
	 * Process input from the keyboard and call the listeners
	 */
	private void keyboardInput () {
		while (Keyboard.next()) {
			char ascii = Keyboard.getEventCharacter();
			int key = Keyboard.getEventKey();
			if (Keyboard.getEventKeyState()) {
				for (IKeyboardListener list : keyListeners)
					list.keyDown(ascii, key);
			}
		}
	}
	
	/**
	 * Process input from the mouse and call the listeners
	 */
	private void mouseInput () {
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		mouseDX = Mouse.getDX();
		mouseDY = Mouse.getDY();
		if (Mouse.hasWheel())
			mouseDWheel = Mouse.getDWheel();
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (button < 0) continue;
			if (Mouse.getEventButtonState()) {
				mouseButtonState[button] = true;
				for (IMouseListener list : mouseListeners)
					list.mouseDown(button);
			}
		}
	}
	
	/**
	 * Stop the framework
	 */
	public void stop () {
		notStopped = false;
	}
	
	/**
	 * Start the main loop
	 */
	public void start () {
		/* start display */
		try {
			DisplayMode[] dms = Display.getAvailableDisplayModes();
			DisplayMode dm = new DisplayMode(windowWidth, windowHeight);
			for (int i = 0; i < dms.length; ++i) {
				boolean cond = dms[i].getWidth() == windowWidth &&
							   dms[i].getHeight() == windowHeight;
				if ((flags & FULLSCREEN_BIT) != 0)
					cond = cond && dms[i].isFullscreenCapable();
				if (cond) {
					dm = dms[i];
					if ((flags & FULLSCREEN_BIT) != 0)
						Display.setFullscreen(true);
					break;
				}
			}
			Display.setDisplayMode(dm);
			Display.setParent(parentCanvas);
			Display.setTitle(windowTitle);
			Display.setResizable(false);
			Display.setVSyncEnabled(true);
			Display.create();
			AL.create();
			Keyboard.create();
			Keyboard.enableRepeatEvents(false);
			Mouse.create();
			
			/* clear error */
			GL11.glGetError();
			AL10.alGetError();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		/* set up opengl rendering */
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		long auxTime = getGlobalTime();
		Scene scene = Scene.getInstance();
		while (!Display.isCloseRequested() && notStopped) {
			long now = getGlobalTime();
			this.delta = now-auxTime;
			auxTime = now;
			if (Display.getWidth() != windowWidth)
				this.windowWidth = Display.getWidth();
			if (Display.getHeight() != windowHeight)
				this.windowHeight = Display.getHeight();
			if (!Display.getTitle().equals(windowTitle))
				Display.setTitle(windowTitle);
			keyboardInput();
			mouseInput();
			ResourceManager.loadResources();
			scene.update();
			ticks = (++ticks) % 100007;
			GL11.glFlush();
			Display.update();
			Display.sync(60);
		}
		GL11.glPopAttrib();
		/* clean resources */
		ResourceManager.cleanResources();
		Display.destroy();
		AL.destroy();
	}
	
	public int getTicks () {
		return ticks;
	}
	
	/**
	 * @param width window size
	 * @param height window height
	 */
	public void setWindowSize (int width, int height) {
		this.windowWidth = width;
		this.windowHeight = height;
	}
	
	/**
	 * @param title window title
	 */
	public void setWindowTitle (String title) {
		this.windowTitle = title;
	}
	
	/**
	 * @return delta time in seconds
	 */
	public float getDeltaTime () {
		return (float) (delta) * 0.001f;
	}

	/**
	 * @return current local time in milliseconds (since the framework started)
	 */
	public long getLocalTime () {
		long now = getGlobalTime();
		if (time < 0.0) time = now;
		return now-time;
	}
	
	/**
	 * @return current time in miliseconds
	 */
	public long getGlobalTime () {
		long now = (long) (Sys.getTime() * 1000 / Sys.getTimerResolution());
		return now;
	}
	
	/**
	 * @return window resolution width
	 */
	public int getWindowWidth () {
		return windowWidth;
	}
	
	/**
	 * @return window resolution height
	 */
	public int getWindowHeight () {
		return windowHeight;
	}
	
	/**
	 * @return window aspect ratio
	 */
	public float getWindowAspectRatio () {
		return (float) windowWidth / windowHeight;
	}
	
	/**
	 * @return window title
	 */
	public String getWindowTitle () {
		return windowTitle;
	}
	
	/**
	 * @return mouse X position
	 */
	public int getMouseX () {
		return mouseX;
	}
	
	/**
	 * @return mouse Y position
	 */
	public int getMouseY () {
		return mouseY;
	}
	
	/**
	 * @return mouse X movement
	 */
	public int getMouseDX () {
		return mouseDX;
	}
	
	/**
	 * @return mouse Y movement
	 */
	public int getMouseDY () {
		return mouseDY;
	}
	
	/**
	 * @return mouse wheel movement
	 */
	public int getMouseDWheel () {
		return mouseDWheel;
	}
	
	/**
	 * @param log new log stream to be set
	 */
	public void setLogStream (PrintStream log) {
		if (log == null)
			throw new IllegalArgumentException();
		this.logStream = log;
	}
	
	/**
	 * @return log print stream
	 */
	public PrintStream getLogStream () {
		return this.logStream;
	}
	
	/**
	 * Adds a line to the log stream
	 * @param log log string
	 */
	public void log (Object log) {
		this.logStream.println(log);
	}
	
	/**
	 * Adds a line to the error stream
	 * @param log log string
	 */
	public void err (Object err) {
		this.errStream.println(err);
	}
	
	/**
	 * @param err new error stream to be set
	 */
	public void setErrStream (PrintStream err) {
		if (err == null)
			throw new IllegalArgumentException();
		this.errStream = err;
	}
	
	/**
	 * @return error print stream
	 */
	public PrintStream getErrStream () {
		return this.errStream;
	}
	
}
