package game;

import engine.VoidObject;
import framework.Framework;
import framework.Scene;
import graphics.Shader;

/**
 * @author germangb
 *
 */
public class Game {

	/* used shader */
	public static Shader SHADER = Shader
			.fromFile(
					"shaders/texture.vert",
					"shaders/texture.frag");
	
	/* framework settings */
	public static final int WIDTH = 160*4;
	public static final int HEIGHT = 82*4;
	
	/* components */
	private InspectorModel inspector;
	private CraftModel craft;
	private InventoryModel inventory;
	private PlayerModel player;
	private VoidObject craftContainer;
	private VoidObject inspectorContainer;
	
	/**
	 * Create the game
	 */
	private Game() {
		this.inventory = new InventoryModel();
		this.player = new PlayerModel();
		this.craft = new CraftModel();
		this.inspector = new InspectorModel();
		this.craftContainer = new VoidObject("craft_container");
		this.inspectorContainer = new VoidObject("inspector_container");
		this.setUp();
		Framework frame = Framework.getInstance();
		frame.setWindowSize(WIDTH, HEIGHT);
		frame.setWindowTitle("Color Dealer");
		frame.start();
	}
	
	/**
	 * Set up game
	 */
	private void setUp () {
		Inventory invent = new Inventory(inventory);
		Player pl = new Player(player);
		Craft cr = new Craft(craft);
		Inspector ins = new Inspector(inspector);
		cr.position().x = 50 - 25;
		cr.position().y = HEIGHT/8 - 8;
		pl.position().x = 2;
		pl.position().y = 1;
		Controller handling = new Controller(player, inventory, craft, inspector, inspectorContainer);
		invent.addGameUpdater(handling);
		invent.position().x = WIDTH/4 - 58 - 2;
		invent.position().y = 1;
		Scene scene = Scene.getInstance();
		
		craftContainer.addChild(cr);
		inspectorContainer.addChild(ins);
		
		scene.getFlatRoot().addChild(invent);
		scene.getFlatRoot().addChild(pl);
		scene.getFlatRoot().addChild(craftContainer);
		scene.getFlatRoot().addChild(inspectorContainer);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		new Game();
	}

}
