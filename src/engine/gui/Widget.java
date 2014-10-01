package engine.gui;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Vector3f;

import engine.GameNode;
import engine.framework.Framework;
import engine.framework.Input;

/**
 * @author germangb
 *
 */
public abstract class Widget extends GameNode {

	/**
	 * @author germangb
	 *
	 */
	public static enum Alignment {
		LEFT, MIDDLE, RIGHT
	}
	
	/**
	 * reference to the hovered widget
	 */
	private static Widget HOVERED_WIDGET = null;
	
	/**
	 * reference to the active widget
	 */
	private static Widget ACTIVE_WIDGET = null;

	/**
	 * return the widget being hovered
	 * in this tree
	 * @return widget reference (might be null)
	 */
	public static Widget getHoveredWidget () {
		return HOVERED_WIDGET;
	}
	
	/**
	 * return the active widget
	 * @return widget reference (might be null)
	 */
	public static Widget getActiveWidget () {
		return ACTIVE_WIDGET;
	}
	
	private Alignment align;
	private Dimension dim;
	private Set<IActionListener> listeners;
	
	/**
	 * @param key
	 */
	public Widget (String key) {
		this(key, 0, 0);
	}
	
	/**
	 * @param key
	 * @param width widget width
	 * @param height widget height
	 */
	public Widget(String key, float width, float height) {
		super(key);
		this.align = Alignment.LEFT;
		this.dim = new Dimension();
		setSize(width, height);
		this.listeners = new HashSet<IActionListener>();
		this.listeners = new HashSet<IActionListener>();
		this.debugRenderer = new WidgetQuadRenderer(this, false);
	}
	
	/**
	 * @param key
	 * @param dim
	 */
	public Widget(String key, Dimension dim) {
		super(key);
		this.dim = dim;
		this.listeners = new HashSet<IActionListener>();
	}
	
	/**
	 * Add a new listener to the widget
	 * @param listener
	 */
	public void addListener (IActionListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Add a new listener to the widget
	 * @param listener
	 */
	public void removeListener (IActionListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Set widget alignment relative
	 * to parent
	 * @param align
	 */
	public void setAlignment (Alignment align) {
		this.align = align;
	}
	
	/**
	 * set widget size
	 * @param width new width
	 * @param height new height
	 */
	public void setSize (float width, float height) {
		this.dim.setSize(width, height);
	}
	
	/**
	 * return widget's dimension
	 * @return dimension reference
	 */
	public Dimension getSize () {
		return dim;
	}
	
	/**
	 * Set this widget as activated
	 */
	public void activate () {
		ACTIVE_WIDGET = this;
	}
	
	public void action () {
		for (IActionListener list : listeners) {
			list.action(this);
		}
	}
	
	/**
	 * update widget states
	 * @param widget position in local world space
	 * @param widget position in local world space
	 */
	public void updateTree () {
		/* reset hovered widget */
		if ( !(getParent() instanceof Widget) )
			HOVERED_WIDGET = null;
		
		int h = Framework.getInstance().getWindowHeight();
		int mx = Input.getMouseX();
		int my = h-Input.getMouseY();
		Vector3f widgetPos = getWorldPosition();
		if (mx > widgetPos.x && mx <= widgetPos.x+dim.width &&
			my > widgetPos.y && my <= widgetPos.y+dim.height ||
			dim.getWidth() == -1 ||
			dim.getHeight() == -1) {
			/* the mouse could be hovering this widget */
			/* so we set the static variable */
			HOVERED_WIDGET = this;
			/* do the same to all children */
			List<GameNode> children = getChildren();
			for (GameNode child : children) {
				/* it's been ensured that all the children */
				/* are are widgets */
				Widget w = (Widget) child;
				w.updateTree();
			}
		}
	}

	/**
	 * return if the widget is being
	 * hovered by the mouse cursor
	 * @return
	 */
	public boolean isHovered () {
		return HOVERED_WIDGET == this;
	}
	
	//
	// GameNode re-implementation
	//
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void update () {
		Vector3f pos = this.getPosition();
		switch (align) {
			case MIDDLE:
				/* modify x position */
				/* to match the alignment */
				if ( (getParent() == null) || !(getParent() instanceof Widget) )
					throw new RuntimeException();
				int pw = ((Widget)getParent()).getSize().width;
				pos.x = pw/2 - dim.width/2;
				setPosition(pos);
				break;
			case RIGHT:
				/* modify x position */
				/* to match the alignment */
				if ( (getParent() == null) || !(getParent() instanceof Widget) )
					throw new RuntimeException();
				pw = ((Widget)getParent()).getSize().width;
				pos.x = pw - dim.width;
				setPosition(pos);
				break;
		}
		super.update();
	}

	@Override
	public void addChild (GameNode child) {
		/* only allow widgets to be related */
		/* to this type of game node */
		if (child instanceof GameNode) {
			super.addChild(child);
		} else
			throw new IllegalArgumentException("child has to be a Widget type");
	}
	
	//
	// END
	//
}
