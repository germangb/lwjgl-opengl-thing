package gui;

import java.util.List;
import java.util.Stack;

import engine.GameObject;
import engine.IGameUpdater;
import framework.Framework;
import framework.IMouseListener;
import framework.Input;

public class GuiController implements IGameUpdater, IMouseListener {

	private GameObject root;
	private Widget active;
	private boolean mousePressed;
	
	public GuiController(GameObject root) {
		this.root = root;
		this.active = null;
		Framework.getInstance().addMouseListener(this);
	}
	
	//
	// IGameUpdater interface implementation
	//

	@Override
	public void update(GameObject object) {
		/* mouse position */
		int mx = Input.getMouseX()/2;
		int my = Input.getMouseY()/2;
		if (!Input.isMouseDown(0) && mousePressed) {
			if (active != null && active.enabled)
				active.action();
			mousePressed = false;
			Stack<GameObject> w = new Stack<GameObject>();
			w.push(root);
			while (!w.isEmpty()) {
				GameObject p = w.pop();
				if (p instanceof Widget)
					((Widget)p).active = false;
				List<GameObject> chi = ((GameObject)p).getChildren();
				for (GameObject cc : chi)
					w.push(cc);
			}
		}
		
		if (mousePressed) {
			Stack<GameObject> w = new Stack<GameObject>();
			w.push(root);
			if (active != null)
				active.active = false;
			active = null;
			while (!w.isEmpty()) {
				GameObject p = w.pop();
				if (p instanceof Widget)
				if (((Widget)p).contains(mx, my))
					active = (Widget)p;
				List<GameObject> chi = p.getChildren();
				for (GameObject cc : chi)
					w.push(cc);
			}
			if (active != null)
				active.active = true;
		}
	}
	
	//
	// IMouseListener interface implementation
	//

	@Override
	public void mouseDown(int button) {
		if (button == 0)
			mousePressed = true;
	}
	
	//
	// END
	//

}
