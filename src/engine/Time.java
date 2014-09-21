package engine;

import engine.framework.Framework;

/**
 * @author germangb
 * Timing wrapper
 */
public class Time {

	/**
	 * Wrapper - get delta time (in seconds)
	 * @return delta time
	 */
	public static float getDeltaTime () {
		return Framework.getInstance().getDeltaTime();
	}
	
	/**
	 * Wrapper - get local time (in milliseconds)
	 * @return local time
	 */
	public static long getLocalTime () {
		return Framework.getInstance().getLocalTime();
	}
	
	/**
	 * Wrapper - get global time (in milliseconds)
	 * @return local time
	 */
	public static long getGlobalTime () {
		return Framework.getInstance().getGlobalTime();
	}

}
