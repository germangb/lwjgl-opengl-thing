package gui;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import engine.GameObject;

/**
 * @author germangb
 *
 */
public abstract class Widget extends GameObject {
	
	/* widget dimensions */
	private Vector2f dimension;
	private Set<IActionListener> listeners;
	public boolean active;
	public boolean enabled;
	
	/**
	 * Default constructor
	 */
	public Widget() {
		super("gui");
		this.listeners = new HashSet<IActionListener>();
		this.dimension = new Vector2f();
		this.active = false;
		this.enabled = true;
	}
	
	/**
	 * add listener to the set
	 * @param listener
	 */
	public void addListener (IActionListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * remove listener from the set
	 * @param listener
	 */
	public void removeListener (IActionListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * test point in collision given
	 * in world coordinates
	 * @param x
	 * @param y
	 * @return test result
	 */
	public boolean contains (int x, int y) {
		Vector3f pos = getWorldPosition();
		return x >= pos.x &&
			   x <= pos.x+dimension.x &&
			   y >= pos.y &&
			   y <= pos.y+dimension.y;
	}
	
	/**
	 * test point in collision given
	 * in local coordinates
	 * @param x
	 * @param y
	 * @return test result
	 */
	public boolean containsLocal (int x, int y) {
		Vector3f pos = position();
		return x >= pos.x &&
			   x <= pos.x+dimension.x &&
			   y >= pos.y &&
			   y <= pos.y+dimension.y;
	}
	
	/**
	 * call action for this
	 * widget
	 */
	public void action () {
		for (IActionListener list : listeners)
			list.action(this);
	}

	/**
	 * get widget dimensions
	 * @return
	 */
	public Vector2f dimension () {
		return dimension;
	}

}
