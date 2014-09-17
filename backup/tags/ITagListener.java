package framework.tags;

/**
 * @author germangb
 *
 */
public interface ITagListener <T> {

	/**
	 * Called whenever a tag is modified
	 * @param tag reference to tag
	 * @param oldValue old value
	 * @param newValue new modified value
	 */
	public void modified (Tag <T> tag, T oldValue, T newValue);
	
}
