package tests;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import engine.GameNode;
import engine.IGameUpdater;
import engine.Scene;
import engine.Time;
import engine.framework.Framework;
import engine.framework.Input;
import engine.nodes.CameraNode;

public class CameraController implements IGameUpdater {

	private CameraNode camera;
	float a = 0.0f;
	float b = 0.0f;
	float r = 78;
	float x = 0;
	float y = 0;
	float z = 0;
	float offs = 0;
	
	float mouseOffX = 0;
	float mouseOffZ = 0;
	float mouseOffXD = 0;
	float mouseOffZD = 0;
	
	public CameraController (CameraNode camera) {
		this.camera = camera;
	}
	
	@Override
	public void update(GameNode object) {
		int mx = Input.getMouseDX();
		int my = Input.getMouseDY();
		Vector3f camRot = Scene.getInstance().getShadowCamera().getRotation();
		camRot.x = (float) Math.PI/4f;
		camRot.y = (float) Math.PI * 3.25f;
		Scene.getInstance().getShadowCamera().setRotation(camRot);
		if (Input.isMouseDown(1)) {
			a -= mx * 0.0125f;
			b -= my * 0.0125f;
		}
		
		b = (float) Math.PI/3;
		
		Vector2f v = new Vector2f();
		float dt = Time.getDeltaTime();
		if (Input.isKeyDown(Input.KEY_W)) {
			v.x += Math.sin(-a);
			v.y -= Math.cos(-a);
		} else if (Input.isKeyDown(Input.KEY_S)) {
			v.x -= Math.sin(-a);
			v.y += Math.cos(-a);
		}
		
		if (Input.isKeyDown(Input.KEY_D)) {
			v.y -= Math.sin(a);
			v.x += Math.cos(a);
		} else if (Input.isKeyDown(Input.KEY_A)) {
			v.y += Math.sin(a);
			v.x -= Math.cos(a);
		}
		
		if (v.lengthSquared() > 1.0f)
			v.normalize();
		v.scale(dt * 64);
		
		x += v.x;
		z += v.y;
		
		float cx = x + (float) (Math.sin(a) * Math.cos(b) * r);
		float cy = y + (float) (Math.sin(b) * r);
		float cz = z + (float) (Math.cos(a) * Math.cos(b) * r);
		
		/* mouse position offset */
		float mouseX = (float)Input.getMouseX()/Framework.getInstance().getWindowWidth();
		float mouseY = (float)Input.getMouseY()/Framework.getInstance().getWindowHeight();
		mouseX = mouseX*2-1;
		mouseY = mouseY*2-1;
		
		mouseOffXD = 0;
		mouseOffZD = 0;
		
		// look sideways
		mouseOffXD += mouseX * Math.cos(a) * 8;
		mouseOffZD -= mouseX * Math.sin(a) * 8;
		// look front
		mouseOffXD -= mouseY * Math.sin(a) * 8;
		mouseOffZD -= mouseY * Math.cos(a) * 8;
		
		mouseOffX += (mouseOffXD - mouseOffX) * dt * 2;
		mouseOffZ += (mouseOffZD - mouseOffZ) * dt * 2;
		cx += mouseOffX;
		cz += mouseOffZ;
		cx += mouseOffX;
		cz += mouseOffZ;
		
		cx += Math.cos(offs)*2;
		cz += Math.sin(offs*0.5)*2;
		offs += dt * Math.PI * 0.5;
		camera.setPosition(cx, cy, cz);
		Scene.getInstance().getShadowCamera().setPosition(x, y, z);
		camera.setRotation(b, -a, 0);
	}

}
