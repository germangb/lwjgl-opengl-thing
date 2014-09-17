package framework.tags;

import java.util.HashSet;
import java.util.Set;

/**
 * @author germangb
 *
 */
public abstract class Tag <T> {

	/**
	 * Tag value
	 */
	public T tag;
	
	/**
	 * Set of listeners
	 */
	public Set<ITagListener<T> > listeners;
	
	/**
	 * Initializes the tag
	 * @param value initial value
	 */
	public Tag (T value) {
		this.tag = value;
		this.listeners = new HashSet<ITagListener<T> >();
	}
	
	/**
	 * Add a new listener to the tag
	 * @param listener new listener
	 */
	public void addListener (ITagListener<T> listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes a listener from the tag
	 * @param listener removed listener
	 */
	public void removeListener (ITagListener<T> listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Modify this tag
	 * @param value new value
	 */
	public void modify (T value) {
		/* call listeners */
		for (ITagListener<T> list : listeners)
			list.modified(this, tag, value);
		
		/* set new value */
		this.tag = value;
	}
	
	/**
	 * Return the tag balue
	 * @return tag value
	 */
	public T getValue () {
		return tag;
	}
}
