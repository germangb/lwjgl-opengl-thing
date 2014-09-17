package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import resources.Resources;
import engine.GameObject;
import engine.IGameRenderer;
import engine.IGameUpdater;
import engine.SoundSource;
import engine.SoundSourceObject;
import engine.VoidObject;
import framework.Framework;
import framework.IMouseListener;
import framework.Input;
import graphics.Texture;
import gui.GuiController;
import gui.IActionListener;
import gui.Widget;

/**
 * @author germangb
 *
 */
public class Panel extends GameObject implements IMouseListener, IGameRenderer, IGameUpdater, IActionListener {
	
	private static SoundSource BUP_SOUND = new SoundSource(Resources.get("tab.wav"), true);
	
	/* textures */
	private static Texture PH_TEXTURE = Texture
			.fromFile(
					"res/ph.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private static Texture INVENTORY_TEXTURE = Texture
			.fromFile(
					"res/panel.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private static Texture USER_TEXTURE = Texture
			.fromFile(
					"res/user_panel.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private static Texture INVENTORY_HIGHLIGHT = Texture
			.fromFile(
					"res/inventory_selected.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private static Texture HEALTH_BAR = Texture
			.fromFile(
					"res/health.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private enum State {
		USER_PANEL,
		INVENTORY_PANEL,
	}
	
	private Texture texture;
	private Tab userTab;
	private State state;
	private Tab close;
	
	/* user tab */
	private Player player;
	
	/* inventory tab */
	private SoundSourceObject bup;
	private Button next, previous;
	private Inventory inventory;
	private Tab inventoryTab;
	private Item pick;
	private int page;
	private int row;
	private int col;
	
	/**
	 * Default constructor
	 */
	public Panel(Inventory inventory, Player player) {
		super("side_panel");
		bup = new SoundSourceObject("bup_sound", BUP_SOUND, false);
		Framework.getInstance().addMouseListener(this);
		this.close = new Tab();
		this.close.dimension().x = 16;
		this.close.position().y = 170-36;
		this.close.position().x = 172 - 16 - 4;
		this.close.addListener(this);
		this.previous = new Button();
		previous.position().x = 68;
		previous.position().y = 8;
		previous.dimension().x = 16;
		previous.enabled = false;
		this.next = new Button();
		next.position().x = previous.position().x + previous.dimension().x + 4;
		next.position().y = 8;
		next.dimension().x = 16;
		next.addListener(this);
		previous.addListener(this);
		this.inventory = inventory;
		this.player = player;
		texture = USER_TEXTURE;
		VoidObject root = new VoidObject("gui_container");
		root.addChild(next);
		root.addChild(previous);
		root.addChild(close);
		root.position().x = Game.WIDTH/2 - 172;
		userTab = new Tab();
		userTab.addListener(this);
		userTab.selected = true;
		userTab.position().x = 8;
		userTab.position().y = 170-36;
		inventoryTab = new Tab();
		inventoryTab.addListener(this);
		inventoryTab.position().x = userTab.position().x + userTab.dimension().x + 2;
		inventoryTab.position().y = 170-36;
		root.addChild(userTab);
		root.addChild(inventoryTab);
		row = col = -1;
		page = 0;
		pick = null;
		
		addChild(root);
		addChild(bup);
		addGameUpdater(new GuiController(root));
		setVisible(false);
		addGameUpdater(this);
		addGameRenderer(this);
		setState(State.INVENTORY_PANEL);
	}
	
	public void setState (State state) {
		unsetState();
		if (state == State.USER_PANEL) {
			texture = USER_TEXTURE;
			userTab.selected = true;
			inventoryTab.selected = false;
		} else if (state == State.INVENTORY_PANEL) {
			texture = INVENTORY_TEXTURE;
			userTab.selected = false;
			inventoryTab.selected = true;
			next.setVisible(true);
			previous.setVisible(true);
		}
		this.state = state;
	}
	
	private void unsetState () {
		if (state == State.USER_PANEL) {
			userTab.selected = false;
		} else if (state == State.INVENTORY_PANEL) {
			row = col = -1;
			inventoryTab.selected = false;
			next.setVisible(false);
			previous.setVisible(false);
		}
	}
	
	//
	// IActionListener interface implementation
	//

	@Override
	public void action(Widget widget) {
		if (widget == close) {
			setVisible(false);
			GameController.getInstance().setState(GameState.WORLD);
		} else if (widget == userTab) {
			setState(State.USER_PANEL);
		} else if (widget == inventoryTab) {
			setState(State.INVENTORY_PANEL);
		} else if (widget == next) {
			previous.enabled = true;
			next.enabled = true;
			page++;
			if (page >= inventory.totalPages()-1) {
				page = inventory.totalPages()-1;
				next.enabled = false;
			}
		} else if (widget == previous) {
			previous.enabled = true;
			next.enabled = true;
			page--;
			if (page <= 0) {
				page = 0;
				previous.enabled = false;
			}
		}
	}
	
	//
	// IGameRenderer implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		int program = Game.SHADER.getProgram();
		GL20.glUseProgram(program);
		int mvpLoc = GL20.glGetUniformLocation(program, "mvp");
		int texLoc = GL20.glGetUniformLocation(program, "texture");
		int transLoc = GL20.glGetUniformLocation(program, "translate");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		mvp.store(mvpBuffer);
		mvpBuffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, mvpBuffer);
		GL20.glUniform1i(texLoc, 0);
		GL20.glUniform2f(transLoc, 0, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);	GL11.glVertex2f(Game.WIDTH/2-173, 0);
			GL11.glTexCoord2f(1, 1);	GL11.glVertex2f(Game.WIDTH/2, 0);
			GL11.glTexCoord2f(1, 0);	GL11.glVertex2f(Game.WIDTH/2, 170);
			GL11.glTexCoord2f(0, 0);	GL11.glVertex2f(Game.WIDTH/2-173, 170);
		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, INVENTORY_HIGHLIGHT.getId());
		
		/* render inventory */
		if (state == State.INVENTORY_PANEL) {
			if (row != -1 && col != -1) {
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 1);	GL11.glVertex2f(Game.WIDTH/2-156 + 0 + col*24, 98 + 0 - row*24);
					GL11.glTexCoord2f(1, 1);	GL11.glVertex2f(Game.WIDTH/2-156 + 20 + col*24, 98 + 0 - row*24);
					GL11.glTexCoord2f(1, 0);	GL11.glVertex2f(Game.WIDTH/2-156 + 20 + col*24, 98 + 20 - row*24);
					GL11.glTexCoord2f(0, 0);	GL11.glVertex2f(Game.WIDTH/2-156 + 0 + col*24, 98 + 20 - row*24);
				GL11.glEnd();
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, PH_TEXTURE.getId());
			
			for (int i = 0; i < 3; ++i) {
				for (int x = 0; x < 6; ++x) {
					if (!inventory.freeSpace(i, x, page)) {
						Item it = inventory.itemAt(i, x, page);
						GL11.glColor3f(it.r, it.g, it.b);
						GL11.glBegin(GL11.GL_QUADS);
						GL11.glTexCoord2f(0, 1);	GL11.glVertex2f(Game.WIDTH/2-156 + 2 + 0 + 24*x, 98+18 - 0 - 24*i);
						GL11.glTexCoord2f(0, 0);	GL11.glVertex2f(Game.WIDTH/2-156 + 2 + 16 + 24*x, 98+18 - 0 - 24*i);
						GL11.glTexCoord2f(1, 0);	GL11.glVertex2f(Game.WIDTH/2-156 + 2 + 16 + 24*x, 98+18 - 16 - 24*i);
						GL11.glTexCoord2f(1, 1);	GL11.glVertex2f(Game.WIDTH/2-156 + 2 + 0 + 24*x, 98+18 - 16 - 24*i);
						GL11.glEnd();
					}
				}
			}
			
			if (pick != null) {
				int mx = Input.getMouseX()/2;
				int my = Input.getMouseY()/2;
				GL11.glColor3f(pick.r, pick.g, pick.b);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex3f(mx+-8, my+-8, -0.5f);
					GL11.glVertex3f(mx+8, my+-8, -0.5f);
					GL11.glVertex3f(mx+8, my+8, -0.5f);
					GL11.glVertex3f(mx+-8, my+8, -0.5f);
				GL11.glEnd();
			}
		} else if (state == State.USER_PANEL) {
			/* render health bar */
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, HEALTH_BAR.getId());
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 1); GL11.glVertex2f(Game.WIDTH/2 - 88 - 75, 35+0);
				GL11.glTexCoord2f(1, 1); GL11.glVertex2f(Game.WIDTH/2 - 88 - 0, 35+0);
				GL11.glTexCoord2f(1, 0); GL11.glVertex2f(Game.WIDTH/2 - 88 - 0, 35+90);
				GL11.glTexCoord2f(0, 0); GL11.glVertex2f(Game.WIDTH/2 - 88 - 75, 35+90);
			GL11.glEnd();
		}
		GL20.glUseProgram(0);
	}
	
	//
	// IGameUpdater implementation
	//

	@Override
	public void update(GameObject object) {
		if (state == State.INVENTORY_PANEL) {
			int mx = Input.getMouseX()/2;
			int my = Input.getMouseY()/2;
			if (mx > Game.WIDTH/2-156 && my < 98+19) {
				mx -= Game.WIDTH/2-156+1;
				my -= 98+19-1;
				my = -my;
				row = my/24;
				col = mx/24;
				if (my%24 > 19 || row >= 3) row = -1;
				if (mx%24 > 19 || col >= 6) col = -1;
			} else row = col = -1;
		}
	}
	
	//
	// IMouseListener interface
	//

	@Override
	public void mouseDown(int button) {
		if (GameController.getInstance().getState() == GameState.PANEL) {
			if (button == 0) {
				if (row != -1 && col != -1) {
					bup.play();
					Item it = inventory.grab(row, col, page);
					inventory.put(row, col, page, pick);
					pick = it;
				}
			}
		}
	}
	
	//
	// END
	//

}
