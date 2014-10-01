package engine.gui;

public class Button extends Widget {

	private TextLabel lab;
	
	public Button() {
		super("test_button", 32, 32);
		lab = new TextLabel("Button");
		lab.setPosition(0, 6, 0);
		lab.setAlignment(Widget.Alignment.MIDDLE);
		addChild(lab);
		debugRenderer = new WidgetQuadRenderer(this, true);
	}
	
	public void setText (String text) {
		lab.setText(text);
	}

}
