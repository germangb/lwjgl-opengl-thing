package lab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author germangb
 *
 */
public class Skeleton {

	/**
	 * @author germangb
	 *
	 */
	public class SkeletonBuilder {
		
		/**
		 * skeleton being built
		 */
		private Skeleton skel;
		
		/**
		 * Default constructor
		 */
		public SkeletonBuilder() {
			this.skel = new Skeleton();
		}
		
		/**
		 * add a new bone to the skeleton
		 * being built
		 * @param bone bone reference
		 */
		public Skeleton addBone (Bone bone) {
			skel.addBone(bone);
			return skel;
		}
		
		/**
		 * get skeleton reference
		 * @return
		 */
		public Skeleton build () {
			return skel;
		}

	}
	
	/**
	 * List of bones
	 */
	public List<Bone> bones;
	
	/**
	 * Default constructor
	 */
	private Skeleton () {
		this.bones = new ArrayList<Bone>();
	}
	
	/**
	 * add a new bone
	 * @param bone
	 */
	private void addBone (Bone bone) {
		bones.add(bone);
	}
	
	/**
	 * get bone given its index number
	 * if the index exceeds the bone list, a
	 * IndexOutOfBounds exception will be thrown
	 * 
	 * @param index index number
	 * @return bone reference
	 */
	public Bone getBone (int index) {
		if (index >= bones.size())
			throw new IndexOutOfBoundsException();
		return bones.get(index);
	}

}
