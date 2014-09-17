package game;

import resources.Resources;
import engine.GameObject;
import engine.IGameUpdater;
import engine.SoundSource;
import engine.SoundSourceObject;
import framework.Framework;
import framework.IMouseListener;
import framework.Input;

public class Controller implements IGameUpdater, IMouseListener{

	private enum GameState {
		CRAFTING,
		JOBS,
		MESSAGES
	}
	
	private static SoundSource CLICK_SOUND = new SoundSource(Resources.get("tab.wav"), true);
	//private static SoundSource CASH_SOUND = new SoundSource(Resources.get("cash.wav"), true);
	
	private GameState state;
	private boolean mouseDown;
	//private PlayerModel player;
	private InventoryModel inventory;
	private CraftModel craft;
	private Item handledItem;
	private SoundSourceObject click;
	//private SoundSourceObject cash;
	private InspectorModel inspector;
	private GameObject inspectorView;
	
	public Controller(PlayerModel player, InventoryModel inventory, CraftModel craft, InspectorModel inspector, GameObject inspectorView) {
		Framework.getInstance().addMouseListener(this);
		click = new SoundSourceObject("click", CLICK_SOUND, false);
		//cash = new SoundSourceObject("grab", CASH_SOUND, false);
		this.inventory = inventory;
		this.craft = craft;
		//this.player = player;
		this.inspectorView = inspectorView;
		inspectorView.setVisible(false);
		this.inspector = inspector;
		this.handledItem = null;
		this.mouseDown = false;
		this.state = GameState.CRAFTING;
	}
	
	//
	// IGameUpdater interface implementation
	//
	@Override
	public void update(GameObject object) {
		if (Input.isKeyDown(Input.KEY_LCONTROL)) {
			inspectorView.setVisible(true);
			inspectorView.position().x = Input.getMouseX()/4;
			inspectorView.position().y = Input.getMouseY()/4;
			if (inspectorView.position().y > Game.HEIGHT/4 - 18)
				inspectorView.position().y = Input.getMouseY()/4 - 18;
			if (inspectorView.position().x > Game.WIDTH/4 - 15)
				inspectorView.position().x = Input.getMouseX()/4 - 15;
		} else inspectorView.setVisible(false);
		
		if (mouseDown) {
			mouseDown = false;
			/* swap items */
			if (inventory.pointerBounded()) {
				Item grab = inventory.getPointed();
				if (grab != null || handledItem != null)
					click.play();
				inventory.setPointed(handledItem);
				handledItem = grab;
			}
			
			if (state == GameState.CRAFTING) {
				if (craft.pointerBounded()) {
					Item grab = craft.getPointed();
					if (grab != null || handledItem != null)
						click.play();
					craft.setPointed(handledItem);
					handledItem = grab;
				} else if (craft.pointer == 2) {
					/* grab the result */
					if (handledItem == null && craft.crafted() != null) {
						click.play();
						handledItem = craft.crafted();
						craft.pointer = 0;
						craft.setPointed(null);
						craft.pointer = 1;
						craft.setPointed(null);
						craft.pointer = 2;
					}
				}
			}
		}
		
		/* set the pointer from inventory */
		int x = Input.getMouseX()/4 - 100;
		int y = Input.getMouseY()/4 - 2;
		int col = x / 10;
		int row = inventory.rows() - y / 10 - 1;
		if (x >= 0 && y >= 0 && x % 10 < 8 && y % 10 < 8) {
			inventory.col = col;
			inventory.row = row;
			if (col < 0 ||
				col >= inventory.cols() ||
				row < 0 ||
				row >= inventory.rows()) {
				inventory.col = -1;
				inventory.row = -1;
			} else {
				inspector.inspected = inventory.getPointed();
			}
		} else {
			inventory.col = -1;
			inventory.row = -1;
		}
		
		if (state == GameState.CRAFTING) {
			/* try crafting instead! */
			if (inventory.col == -1 || inventory.row == -1) {
				x = Input.getMouseX()/4 - 30;
				y = Input.getMouseY()/4 - 38;
				col = x / 10;
				row = y / 10;
				if (row == 0) {
					if (x > 0 && y > 0 && (col == 0 || col == 1) && x % 10 < 8 && y % 10 < 8) {
						craft.pointer = col;
						inspector.inspected = col == 0 ? craft.getU() : craft.getV();
					} else {
						// point to the result
						x -= 2;
						col = x/10;
						if (x > 0 && y > 0 && col == 3 && x % 10 < 8 && y % 10 < 8) {
							craft.pointer = 2;
							inspector.inspected = craft.crafted();
						} else {
							craft.pointer = -1;
							inspector.inspected = null;
						}
					}
				} else {
					craft.pointer = -1;
					inspector.inspected = null;
				}
			}
		}
	}
	
	//
	// IMouseListener interface implementation
	//

	@Override
	public void mouseDown(int button) {
		mouseDown = button == 0;
	}
	
	//
	// END
	//

}
