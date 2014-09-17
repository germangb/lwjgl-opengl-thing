package game;

public class PlayerModel {

	private long cash;
	
	public PlayerModel() {
		this.cash = 4089;
	}
	
	public void addCash (int amount) {
		this.cash += amount;
	}
	
	public void subCash (int amount) {
		this.cash -= amount;
	}
	
	public long getCash () {
		return cash;
	}

}
