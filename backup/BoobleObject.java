package game.objects;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import framework.Framework;
import framework.engine.FlatObject;
import framework.engine.GameObject;
import framework.engine.IGameUpdater;
import framework.graphics.Texture;
import framework.math.LinearUtils;

public class BoobleObject extends FlatObject implements IGameUpdater {
	
	private static Texture texture = Texture.fromFile("res/booble.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	private GameObject attachment;
	private int width;
	private Vector3f pos = new Vector3f();
	private Vector3f dpos = new Vector3f();
	
	public BoobleObject(String key, GameObject attach) {
		super(key);
		this.attachment = attach;
		this.width = 28;
		addGameUpdater(this);
	}

	@Override
	public void renderFlat() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);	GL11.glVertex2f(pos.x+0, pos.y+0);
		GL11.glTexCoord2f(0.5f, 1);	GL11.glVertex2f(pos.x+16, pos.y+0);
		GL11.glTexCoord2f(0.5f, 0);	GL11.glVertex2f(pos.x+16, pos.y+32);
		GL11.glTexCoord2f(0, 0);	GL11.glVertex2f(pos.x+0, pos.y+32);
		
		GL11.glTexCoord2f(0.5f, 1);	GL11.glVertex2f(16+pos.x+0, pos.y+0);
		GL11.glTexCoord2f(0.75f, 1);	GL11.glVertex2f(width-8-16+pos.x+16, pos.y+0);
		GL11.glTexCoord2f(0.75f, 0);	GL11.glVertex2f(width-8-16+pos.x+16, pos.y+32);
		GL11.glTexCoord2f(0.5f, 0);	GL11.glVertex2f(16+pos.x+0, pos.y+32);
		
		GL11.glTexCoord2f(0.75f, 1);GL11.glVertex2f(width-8+pos.x+0, pos.y+0);
		GL11.glTexCoord2f(1, 1);	GL11.glVertex2f(width-8+pos.x+8, pos.y+0);
		GL11.glTexCoord2f(1, 0);	GL11.glVertex2f(width-8+pos.x+8, pos.y+32);
		GL11.glTexCoord2f(0.75f, 0);GL11.glVertex2f(width-8+pos.x+0, pos.y+32);
		GL11.glEnd();
	}

	@Override
	public void update(GameObject object) {
		dpos = LinearUtils.project(attachment.getWorldPosition());
		float dt = Framework.getInstance().getDeltaTime();
		dpos.y += 0;
		dpos.x += 0;
		pos.x += (dpos.x-pos.x) * 16.0f * dt;
		pos.y += (dpos.y-pos.y) * 16.0f * dt;
		pos.z += (dpos.z-pos.z) * 16.0f * dt;
	}

}
