package game;

import engine.GameObject;

public class Inspector extends GameObject {

	private InspectorModel inspector;
	
	public Inspector(InspectorModel inspector) {
		super("inspector");
		this.inspector = inspector;
		addGameRenderer(new InspectorView(this.inspector));
	}

}
