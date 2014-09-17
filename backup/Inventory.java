package game;

public class Inventory {

	private Item[][][] items;
	
	public Inventory(int pages) {
		items = new Item[3][6][pages];
		for (int i = 0; i < 3; ++i)
			for (int x = 0; x < 6; ++x)
				items[i][x][(int)(Math.random()*pages)] = new Item();
	}
	
	public void swap (int r0, int c0, int p0,  int r1, int c1, int p1) {
		/* swap items */
		Item aux = items[r0][c0][p0];
		items[r0][c0][p0] = items[r1][c1][p1];
		items[r1][c1][p1] = aux;
	}
	
	public int totalPages () {
		return items[0][0].length;
	}
	
	public Item itemAt (int row, int col, int page) {
		return items[row][col][page];
	}
	
	public Item grab (int row, int col, int page) {
		Item item = items[row][col][page];
		items[row][col][page] = null;
		return item;
	}
	
	public boolean freeSpace (int row, int col, int page) {
		return items[row][col][page] == null;
	}
	
	public void put (int row, int col, int page, Item item) {
		items[row][col][page] = item;
	}

}
