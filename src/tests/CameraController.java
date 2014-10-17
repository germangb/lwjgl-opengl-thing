package tests;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import engine.GameNode;
import engine.IGameUpdater;
import engine.Input;
import engine.Scene;
import engine.Time;
import engine.framework.Framework;
import engine.nodes.CameraNode;

public class CameraController implements IGameUpdater {

	private CameraNode camera;
	float alpha = 0.0f;
	float alphaVelo = 0.0f;
	float radius = 78;
	Vector3f position;
	Vector3f positionD;
	float offs = 0;

	public CameraController (CameraNode camera) {
		this.camera = camera;
		this.position = new Vector3f();
		this.positionD = new Vector3f();
	}
	
	@Override
	public void update(GameNode object) {
		int mx = Input.getMouseDX();
		float dt = Time.getDeltaTime();

		Vector3f camRot = Scene.getInstance().getShadowCamera().getRotation();
		camRot.x = (float) Math.PI/4f;
		camRot.y = (float) Math.PI * 3.25f;
		Scene.getInstance().getShadowCamera().setRotation(camRot);
		if (Input.isMouseDown(0)) {
			alphaVelo = -mx*0.5f;
		} else {
			alphaVelo -= alphaVelo * 8 * dt;
		}
		
		alpha += alphaVelo * dt;
				
		Vector2f v = new Vector2f();
		if (Input.isKeyDown(Input.KEY_W)) {
			v.x += Math.sin(-alpha);
			v.y -= Math.cos(-alpha);
		} else if (Input.isKeyDown(Input.KEY_S)) {
			v.x -= Math.sin(-alpha);
			v.y += Math.cos(-alpha);
		}
		
		if (Input.isKeyDown(Input.KEY_D)) {
			v.y -= Math.sin(alpha);
			v.x += Math.cos(alpha);
		} else if (Input.isKeyDown(Input.KEY_A)) {
			v.y += Math.sin(alpha);
			v.x -= Math.cos(alpha);
		}
		
		if (v.lengthSquared() > 1.0f)
			v.normalize();
		
		positionD.x += v.x * dt * 64.0f;
		positionD.z += v.y * dt * 64.0f;
		
		/* update position */
		position.x += (positionD.x - position.x) * dt * 8.0f;
		position.y += (positionD.y - position.y) * dt * 8.0f;
		position.z += (positionD.z - position.z) * dt * 8.0f;
		
		/* new camera position */
		float tetha = (float) Math.PI/3.25f;
		float cx = position.x + (float) (Math.sin(alpha) * Math.cos(tetha) * radius);
		float cy = position.y + (float) (Math.sin(tetha) * radius);
		float cz = position.z + (float) (Math.cos(alpha) * Math.cos(tetha) * radius);
		
		/* mouse position offset */
		float mouseX = (float)Input.getMouseX()/Framework.getInstance().getWindowWidth();
		float mouseY = (float)Input.getMouseY()/Framework.getInstance().getWindowHeight();
		mouseX = mouseX*2-1;
		mouseY = mouseY*2-1;

		cx += Math.cos(offs)*2;
		cz += Math.sin(offs*0.5)*2;
		offs += dt * Math.PI * 0.5;
		camera.setPosition(cx, cy, cz);
		Scene.getInstance().getShadowCamera().setPosition(position);
		camera.setRotation(tetha, -alpha, 0);
	}

}
