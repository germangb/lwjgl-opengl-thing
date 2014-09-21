package engine.gui;

import engine.GameNode;
import engine.IGameUpdater;
import engine.framework.Framework;
import engine.framework.IMouseListener;

public class GuiController implements IGameUpdater, IMouseListener {

	private Widget root;
	
	public GuiController(Widget root) {
		this.root = root;
		Framework.getInstance().addMouseListener(this);
	}
	
	//
	// IGameUpdater inetrface implementation
	//

	@Override
	public void update(GameNode object) {
		root.updateTree();
	}
	
	//
	// IMouseListener interface implementation
	//

	@Override
	public void mouseDown(int button) {
		if (button == 0) {
			Widget click = Widget.getHoveredWidget();
			/* call-backs */
			if (click != null)
				click.action();
		}
	}
	
	//
	// END
	//

}
