package game;

import java.util.Random;

public class InventoryModel {

	private Item[][] items;
	
	/* pointer */
	public int row;
	public int col;
	
	public InventoryModel() {
		this.items = new Item[8][6];
		this.row = -1;
		this.col = -1;
		
		/* text item */
		for (int i = 0; i < 8; ++i) {
			for (int x = 0; x < 6; ++x) {
				if (Math.random() < 0.5)
					items[i][x] = new Item(new Random().nextInt() & 0xFFFFFF);
			}
		}
	}
	
	public boolean pointerBounded () {
		return row >= 0 && row < rows() &&
				col >= 0 && col < cols();
	}
	
	public Item getPointed () {
		if (pointerBounded ())
			return items[row][col];
		return null;
	}
	
	public void setPointed (Item item) {
		if (pointerBounded ())
			items[row][col] = item;
	}

	public int rows () {
		return items.length;
	}
	
	public int cols () {
		return items[0].length;
	}
	
	public Item itemAt (int row, int col) {
		return items[row][col];
	}
	
	public void setItem (int row, int col, Item item) {
		items[row][col] = item;
	}

}
