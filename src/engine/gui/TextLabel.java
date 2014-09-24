package engine.gui;

public class TextLabel extends Widget {

	private TextRenderer render;
	
	public TextLabel(String text) {
		super("text_label");
		this.render = new TextRenderer(text);
		addGameRenderer(render);
	}
	
	public void setText (String text) {
		render.setText(text);
	}

}
