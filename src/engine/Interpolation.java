package engine;

/**
 * @author germangb
 *
 */
public interface Interpolation {

	/**
	 * Linear interpolation
	 */
	public static Interpolation LINEAR = new Interpolation () {

		@Override
		public float apply(float from, float to, float t) {
			return from * (1.0f - t) + to * t;
		}
		
	};
	
	/**
	 * Smooth interpolation
	 */
	public static Interpolation SMOOTH = new Interpolation () {

		@Override
		public float apply(float from, float to, float t) {
			return LINEAR.apply(from, to, 3*t*t-2*t*t*t);
		}
		
	};
	
	/**
	 * Ease in interpolation
	 */
	public static Interpolation EASEIN = new Interpolation () {

		@Override
		public float apply(float from, float to, float t) {
			return LINEAR.apply(from, to, t*t);
		}
		
	};
	
	/**
	 * Ease out interpolation
	 */
	public static Interpolation EASEOUT = new Interpolation () {

		@Override
		public float apply(float from, float to, float t) {
			return LINEAR.apply(from, to, 2*t-t*t);
		}
		
	};

	/**
	 * Apply interpolation between 2 given
	 * values.
	 * @param from
	 * @param to
	 * @param t
	 * @return
	 */
	public float apply (float from, float to, float t);
	
}
