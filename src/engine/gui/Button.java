package engine.gui;

public class Button extends Widget {

	public Button() {
		super("test_button", 32, 32);
		this.setDebug(true);
		TextLabel lab = new TextLabel("Button");
		lab.setPosition(8, 6, 0);
		addChild(lab);
	}

}
