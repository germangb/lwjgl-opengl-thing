package game;

import engine.GameObject;

public class Inventory extends GameObject {

	private InventoryModel inventory;
	
	public Inventory(InventoryModel inventory) {
		super("inventory");
		this.inventory = inventory;
		addGameRenderer(new InventoryView(this.inventory));
	}

}
