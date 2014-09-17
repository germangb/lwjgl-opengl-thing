package game;

public class Item {

	private int color;
	
	public Item(int rgb) {
		this.color = rgb;
	}
	
	public int getRGB () {
		return color;
	}
	
	public int getR () { 
		return (color&0xff0000)>>16; 
	}
	
	public int getG () { 
		return (color&0xff00)>>8; 
	}
	
	public int getB () { 
		return color&0xff; 
	}

}
