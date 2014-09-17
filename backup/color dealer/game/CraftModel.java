package game;

public class CraftModel {
	
	public int pointer;
	private Item u;
	private Item v;
	private Item crafted;
	
	public CraftModel() {
		u = null;
		v = null;
		crafted = null;
		this.pointer = 2;
	}
	
	public Item getU () {
		return u;
	}
	
	public Item getV () {
		return v;
	}
	
	public boolean pointerBounded () {
		return pointer == 0 || pointer == 1;
	}
	
	public Item getPointed () {
		if (pointer == 0) return u;
		if (pointer == 1) return v;
		return null;
	}
	
	private void craftLogic () {
		if (u != null && v != null) {
			int r = (u.getR()+v.getR())/2;
			int g = (u.getG()+v.getG())/2;
			int b = (u.getB()+v.getB())/2;
			crafted = new Item( (r << 16) | (g << 8) | b );
		} else if (u != null) {
			crafted = u;
		} else if (v != null) {
			crafted = v;
		} else {
			crafted = null;
		}
	}
	
	public Item crafted () {
		return crafted;
	}
	
	public void setPointed (Item it) {
		if (pointer == 0)
			this.u = it;
		else if (pointer == 1)
			this.v = it;
		
		// result logic goes here...
		craftLogic();
	}

}
