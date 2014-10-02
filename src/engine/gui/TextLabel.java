package engine.gui;

/**
 * @author germangb
 *
 */
public class TextLabel extends Widget {

	private TextRenderer render;
	
	/**
	 * @param text
	 */
	public TextLabel(String text) {
		super("text_label");
		this.render = new TextRenderer();
		setText(text);
		addGameRenderer(render);
	}
	
	/**
	 * @param rgba
	 */
	public void setColor (int rgba) {
		render.setTint(rgba);
	}
	
	/**
	 * @return
	 */
	public int getColor () {
		return render.getTint();
	}
	
	/**
	 * @param text
	 */
	public void setText (String text) {
		render.setText(text);
		/* modify size */
		setSize(render.getDimensionWidth(), render.getDimensionHeight());
	}
}
