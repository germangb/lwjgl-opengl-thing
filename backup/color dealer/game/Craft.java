package game;

import engine.GameObject;

public class Craft extends GameObject {

	private CraftModel craft;
	
	public Craft(CraftModel craft) {
		super("inventory");
		this.craft = craft;
		addGameRenderer(new CraftView(this.craft));
	}

}
