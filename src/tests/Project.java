package tests;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import engine.*;
import engine.framework.*;
import engine.graphics.*;
import engine.gui.Button;
import engine.gui.GuiController;
import engine.gui.IActionListener;
import engine.gui.RootWidget;
import engine.gui.Widget;
import engine.nodes.BirdNode;
import engine.nodes.CameraNode;
import engine.nodes.DecalNode;
import engine.nodes.ModelNode;
import engine.nodes.PerspectiveCamera;
import engine.nodes.SoundSourceNode;
import engine.nodes.TerrainNode;
import engine.resources.Resources;

public class Project {
	
	private int width = 640;
	private int height = 375;
	private CameraNode camera;
	private PolyModel model;
	private ModelNode figure;
	
	public Project() {
		Scene.getInstance().getRoot().addGameUpdater(new IGameUpdater () {
			float acum = 0.0f;
			int count = 0;
			float max = Float.MIN_VALUE;
			float min = Float.MAX_VALUE;
			@Override
			public void update(GameNode object) {
				if (Time.getDeltaTime() > 0.0)
					acum += 1.0f / Time.getDeltaTime();
				else return;
				++count;
				if (count == 60) {
					//Framework.getInstance().setWindowTitle(acum/128+" FPS");
					float fps = acum/60;
					max = Math.max(fps, max);
					min = Math.min(fps, min);
					Framework.getInstance().log("FPS ["+fps+"] - MAX ["+max+"] - MIN ["+min+"]");
					count = 0;
					acum = 0;
				}
			}
		});
		
		setUp();
		Framework.getInstance().setWindowSize(width, height);
		Framework.getInstance().start();
	}
	
	private void setUp () {
		model = new PolyModel("res/scene.obj");
		PolyModel smooth = new PolyModel("res/scene_smooth.obj");
		figure = new ModelNode("3dmodel", model, smooth, Texture.fromFile("res/scene_uv.png"));
		camera = new PerspectiveCamera("camera", (float)Math.toRadians(55), (float)width/height, 0.1f, 200);
		TerrainNode terrain = new TerrainNode();
		
		camera.addGameUpdater(new IGameUpdater () {
			float a = 0.0f;
			float b = 0.0f;
			float r = 75;
			float x = 0;
			float y = 0;
			float z = 0;
			float offs = 0;
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
				} else {
					//a += Time.getDeltaTime() * 0.25f;
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
				
				cx += Math.cos(offs);
				cz += Math.sin(offs*0.5);
				offs += dt * Math.PI * 0.5;
				camera.setPosition(cx, cy, cz);
				Scene.getInstance().getShadowCamera().setPosition(x, y, z);
				camera.setRotation(b, -a, 0);
			}
		});
		WorldGlobals.FOG_COLOR = 0x726A5F;
		// 0xe7e2da
		WorldGlobals.AMBIENT_COLOR = 0xe7e2da; // 0xa3a3f4
		WorldGlobals.FOG_START = 8.0f;
		WorldGlobals.FOG_DENSITY = 0.03f;
		Scene scene = Scene.getInstance();
		
		for (int i = 0; i < 8; ++i) {
			BirdNode bird = new BirdNode();
			float a = (float) (Math.PI*2*Math.random());
			float r = 32 * (float) Math.random();
			bird.setPosition(r*(float)Math.cos(a), 16, r*(float)Math.sin(a));
			bird.setRotation(0, (float)Math.random()*6.282f, 0);
			bird.setRotationSmooth(16);
			scene.getRoot().addChild(bird);
			bird.addGameUpdater(new IGameUpdater () {

				@Override
				public void update(GameNode b) {
					Vector3f pos = b.getPosition();
					float dt = Time.getDeltaTime();
					pos.x += Math.sin(b.getRotation().y) * 8 * dt;
					pos.z += Math.cos(b.getRotation().y) * 8 * dt;
					b.setPosition(pos);
					
					if (Math.random() < 0.25) {
						Vector3f rot = b.getRotation();
						rot.y += 0.25*(Math.random()*2-1);
						b.setRotation(rot);
					}
				}
				
			});
		}
		
		/* gui */
		RootWidget root = new RootWidget();
		Button bt = new Button();
		Button bt2 = new Button();
		Scene.getInstance().getRoot().addGameUpdater(new GuiController(root));
		scene.getFlatRoot().addChild(root);
		root.addChild(bt2);
		root.addChild(bt);
		bt.setSize(32, 32);
		bt2.setSize(64, 32);
		bt.setPosition(128, 64, 0);
		bt2.setPosition(16, 16, 0);
		bt.addListener(new IActionListener () {
			@Override
			public void action(Widget who) {
				System.out.println("CLICK!");
			}
		});
		bt2.addListener(new IActionListener () {
			@Override
			public void action(Widget who) {
				System.out.println("CLICK! 2");
			}
		});
		
		scene.setShadowQuality(Quality.HIGH);
		scene.setRenderShadows(true);
		scene.getRoot().addChild(terrain);
		scene.getRoot().addChild(camera);
		scene.getRoot().addChild(figure);
		scene.setUsedCamera(camera);
		scene.setPixelScale(1);
		scene.setBackground(0x726A5F);
		figure.setHighlighted(true);
		figure.setPosition(0,-1,0);
		
		SoundSource ambient = new SoundSource(Resources.get("ambient.wav"), true, true);
		SoundSourceNode ambientNode = new SoundSourceNode("birds", ambient);
		scene.getRoot().addChild(ambientNode);
		ambientNode.play();
		
		Decal graffity = new Decal("decals/graffity.decal");
		DecalNode gra = new DecalNode(graffity);
		gra.setSize(12, 10, 12);
		gra.setPosition(-4, -1, 8);
		gra.setRotation(0, 1, 0);
		terrain.addChild(gra);
		
		Decal sewer = new Decal("decals/sewer.decal");
		DecalNode swe = new DecalNode(sewer);
		swe.setSize(4, 4, 4);
		swe.setPosition(-16, 0, 16);
		terrain.addChild(swe);
		
		SoundSource loopSound = new SoundSource(Resources.get("tone.wav"), false, true);
		SoundSourceNode lobj = new SoundSourceNode("loop", loopSound);
		swe.addChild(lobj);
		//lobj.play();
		
		Decal cebra = new Decal("decals/cebra.decal");
		DecalNode ceb = new DecalNode(cebra);
		ceb.setSize(32, 4, 16);
		ceb.setPosition(37.847725f, 0.0f, -38.863556f);
		ceb.setRotation(0,-0.1f,0);
		terrain.addChild(ceb);

		Decal cats = new Decal("decals/cats.decal");
		DecalNode cat = new DecalNode(cats);
		cat.setSize(12, 4, 6);
		cat.setPosition(43.037216f, 6.2567043f, -9.978825f);
		cat.setRotation(1.6000001f, 0.0f, 0.0f);
		cat.setOpacity(0.75f);
		terrain.addChild(cat);
		
		DecalNode ceb1 = new DecalNode(cebra);
		ceb1.setSize(32, 4, 16);
		ceb1.setPosition(20.488823f, 0.0f, 88.46739f);
		ceb1.setRotation(0.0f, -0.20000002f, 0.0f);
		terrain.addChild(ceb1);
		
		DecalNode ceb2 = new DecalNode(cebra);
		ceb2.setSize(32, 8, 16);
		ceb2.setPosition(2.892229f, 2.0f, 158.33842f);
		ceb2.setRotation(0.0f, -0.3f, 0.0f);
		terrain.addChild(ceb2);
		
		DecalNode ceb3 = new DecalNode(cebra);
		ceb3.setSize(32, 8, 16);
		ceb3.setPosition(44.23833f, 0.0f, 135.59244f);
		ceb3.setRotation(0, 1.2f, 0.0f);
		terrain.addChild(ceb3);
		
		DecalNode ceb4 = new DecalNode(cebra);
		ceb4.setSize(32, 8, 16);
		ceb4.setPosition(-19.423662f, 0.0f, 110.558784f);
		ceb4.setRotation(0.0f, -1.9000003f, 0.0f);
		terrain.addChild(ceb4);
		
		Framework.getInstance().addKeyboardListener(new IKeyboardListener () {
			boolean s = false;
			boolean d = true;
			@Override
			public void keyDown(char ascii, int key) {
				if (key == Input.KEY_F1) {
					scene.setRenderShadows(s);
					s = !s;
				} else if (key == Input.KEY_F2) {
					swe.setDebug(d);
					d = !d;
				}
			}
			
		});
		
		scene.getRoot().addGameUpdater(new IGameUpdater () {
			char fix = 'Y';
			DecalNode mod = swe;
			@Override
			public void update(GameNode object) {
				//System.out.println(mod.getPosition()+"  "+mod.getRotation());
				if (Input.isKeyDown(Input.KEY_X)) fix = 'X';
				if (Input.isKeyDown(Input.KEY_Y)) fix = 'Y';
				if (Input.isKeyDown(Input.KEY_Z)) fix = 'Z';
				if (Input.isMouseDown(0) && swe.isDebug()) {
					int x = Input.getMouseX();
					int y = Input.getMouseY();
					Vector3f[] ray = LinearUtils.unProject(x, y);
					Vector3f pos = mod.getPosition();
					float lamda;
					switch (fix) {
						case 'X':
							lamda = (pos.x-ray[0].x) / ray[1].x;
							pos.y = ray[0].y + lamda * ray[1].y;
							pos.z = ray[0].z + lamda * ray[1].z;
							break;
						case 'Y':
							lamda = (pos.y-ray[0].y) / ray[1].y;
							pos.x = ray[0].x + lamda * ray[1].x;
							pos.z = ray[0].z + lamda * ray[1].z;
							break;
						case 'Z':
							lamda = (pos.z-ray[0].z) / ray[1].z;
							pos.x = ray[0].x + lamda * ray[1].x;
							pos.y = ray[0].y + lamda * ray[1].y;
							break;
					}
					mod.setPosition(pos);
				}
				int dw = Input.getMouseDWheel();
				if (dw != 0) {
					dw = dw / Math.abs(dw);
					Vector3f rot = mod.getRotation();
					switch (fix) {
						case 'X':
							rot.x += dw * 0.1f;
							break;
						case 'Y':
							rot.y += dw * 0.1f;
							break;
						case 'Z':
							rot.z += dw * 0.1f;
							break;
					}
					mod.setRotation(rot);
				}
			}
		});
		
		Decal splat = new Decal("decals/blood.decal");
		//Decal explos = new Decal("decals/explosion.decal");
		SoundSource bloodSound = new SoundSource(Resources.get("blood.wav"), false, false);
		Framework.getInstance().addMouseListener(new IMouseListener() {

			@Override
			public void mouseDown(int button) {
				if (button != 2) return;
				int mx = Input.getMouseX();
				int my = Input.getMouseY();
				Vector3f[] ray = LinearUtils.unProject(mx, my);
				float lambda = -ray[0].y / ray[1].y;
 				float x = ray[0].x+lambda*ray[1].x; 
 				float z = ray[0].z+lambda*ray[1].z; 
				DecalNode dec = new DecalNode(splat);
				dec.setPosition(x, 0, z);
				dec.setRotation((float) (Math.PI*Math.random()) * 0.25f, (float) (Math.PI*Math.random()) * 2, 0);
				dec.setSize(20,20,20);
				terrain.addChild(dec);
				SoundSourceNode sobj = new SoundSourceNode("splat", bloodSound);
				dec.addChild(sobj);
				sobj.play();
				dec.addGameUpdater(new IGameUpdater () {
					float op = 1;
					long time = -1;
					@Override
					public void update(GameNode object) {
						if (time == -1)
							time = Time.getLocalTime() + 3000;
						if (Time.getLocalTime() > time) {
							dec.setOpacity(op);
							op -= 0.25 * Time.getDeltaTime();
							if (op < 0) dec.isolate();
						}
					}
					
				});
			}
			
		});
		
		for (int i = -8; i < 8; ++i) {
			for (int x = -8; x < 8; ++x) {
				if (i == 0 && x == 0) continue;
				DecalNode dec = new DecalNode(splat);
				dec.setPosition(32*i, 0, 32*x);
				dec.setRotation((float) (Math.PI*Math.random())*0.25f, (float) (Math.PI*Math.random())*0.25f, (float) (Math.PI*Math.random())*0.25f);
				dec.setSize(20,20,20);
				terrain.addChild(dec);
			}
		}
		
	}
	
	public static void main (String[] args) {
		new Project();
	}

}
