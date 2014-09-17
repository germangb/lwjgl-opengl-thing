package game;

import engine.GameObject;

public class Player extends GameObject {

	private PlayerModel player;
	
	public Player(PlayerModel player) {
		super("inventory");
		this.player = player;
		addGameRenderer(new PlayerView(this.player));
	}

}
