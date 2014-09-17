package game;

import engine.GameObject;
import engine.IGameUpdater;
import framework.Framework;
import framework.IKeyboardListener;
import framework.Input;

/**
 * @author germangb
 *
 */
public class PanelController implements IGameUpdater, IKeyboardListener {

	/**
	 * Referenced panel
	 */
	private Panel panel;
	
	/**
	 * constructor
	 * @param panel reference to panel
	 */
	public PanelController(Panel panel) {
		this.panel = panel;
		Framework.getInstance().addKeyboardListener(this);
	}
	
	//
	// IKeyboardListener interface implementation
	//

	@Override
	public void keyDown(char ascii, int key) {
		GameState state = GameController.getInstance().getState();
		if (state != GameState.PANEL) {
			if (key == Input.KEY_P) {
				panel.setVisible(true);
				GameController.getInstance().setState(GameState.PANEL);
			}
		} else {
			if (key == Input.KEY_P || key == Input.KEY_ESCAPE) {
				panel.setVisible(false);
				GameController.getInstance().setState(GameState.WORLD);
			}
		}
	}
	
	//
	// IGameUpdater interface implementation
	//

	@Override
	public void update(GameObject object) {
		// TODO Auto-generated method stub
		
	}
	
	//
	// END
	//

}
