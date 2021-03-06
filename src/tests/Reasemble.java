package tests;

import javax.vecmath.Vector3f;

import engine.Decal;
import engine.GameNode;
import engine.IGameUpdater;
import engine.Input;
import engine.Scene;
import engine.SoundSource;
import engine.Time;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.framework.IMouseListener;
import engine.framework.LinearUtils;
import engine.graphics.PolyModel;
import engine.graphics.Texture;
import engine.gui.Button;
import engine.gui.Container;
import engine.gui.GreenButtonView;
import engine.gui.GuiController;
import engine.gui.IActionListener;
import engine.gui.RootWidget;
import engine.gui.TextLabel;
import engine.gui.Widget;
import engine.labs.ConvexNode;
import engine.labs.NavMeshNode;
import engine.nodes.BirdNode;
import engine.nodes.CameraNode;
import engine.nodes.DecalNode;
import engine.nodes.ModelNode;
import engine.nodes.PerspectiveCamera;
import engine.nodes.SoundSourceNode;
import engine.nodes.TerrainNode;
import engine.resources.Resources;

/**
 * @author germangb
 *
 */
public class Reasemble {

	private int width = 640;
	private int height = 350;
	
	/* nodes */
	private CameraNode camera;
	private TerrainNode terrain;
	private PolyModel modelPoly;
	private ModelNode model;
	
	public Reasemble() {
		setUp();
		gui();
		Framework.getInstance().setWindowSize(width, height);
		Framework.getInstance().setWindowTitle("Window");
		Framework.getInstance().start();
	}
	
	public void gui () {
		/* tree */
		Scene scene = Scene.getInstance();
		GameNode flatRoot = scene.getFlatRoot();
		
		TextLabel fpsText = new TextLabel("...");
		fpsText.setPosition(8, height-16 - 8, 0);
		RootWidget root = new RootWidget();
		flatRoot.addChild(root);
		root.setSize(width, height);
		root.addChild(fpsText);

		SoundSource clickSnd = new SoundSource(Resources.get("click.wav"), true, false);
		SoundSourceNode click = new SoundSourceNode("click", clickSnd);
		Scene.getInstance().getRoot().addChild(click);
		
		Scene.getInstance().getRoot().addGameUpdater(new GuiController(root));
		scene.getFlatRoot().addChild(root);
		
		Container container = new Container();
		TextLabel text = new TextLabel("<meaningful message here>\n\n.....\n ...\n\nNot implemented!");
		text.setColor(0xFFFFFF88);
		text.setPosition(8, 8, 0);
		container.setDebug(true);
		container.addChild(text);
		container.setSize(300, 170);
		container.setPosition(width/2-300/2, height/2-175/2,0);
		root.addChild(container);
		
		IActionListener closeDialog = new IActionListener () {

			@Override
			public void action(Widget who) {
				click.play();
				container.setVisible(false);
			}
			
		};
		
		IActionListener openDialog = new IActionListener () {

			@Override
			public void action(Widget who) {
				click.play();
				container.setVisible(true);
			}
			
		};
		
		for (int i = 0; i < 5; ++i) {
			int c = i%5;
			Button bt = new Button();
			bt.setText("Btn#"+i);
			root.addChild(bt);
			root.addChild(fpsText);
			bt.setSize(width/5, 32);
			bt.setPosition(width/5*c, 0, 0);
			bt.addListener(openDialog);
			bt.addGameRenderer(new GreenButtonView(bt, true));
		}
		
		Button close = new Button();
		container.addChild(close);
		close.addGameRenderer(new GreenButtonView(close, false));
		close.setPosition(0, container.getSize().height - 32 - 4, 0);
		close.setAlignment(Widget.Alignment.MIDDLE);
		close.setText("close");
		close.setSize(128, 32);
		close.addListener(closeDialog);
		
		scene.getRoot().addGameUpdater(new IGameUpdater () {
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
					float fps = acum/60;
					max = Math.max(fps, max);
					min = Math.min(fps, min);
					String log = "FPS: "+fps+" - MAX: "+max+" - MIN: "+min;
					fpsText.setText(log);
					count = 0;
					acum = 0;
				}
			}
		});
	}
	
	private void setUp () {
		/* ambient colors */
		WorldGlobals.FOG_COLOR = 0x726A5F;
		WorldGlobals.AMBIENT_COLOR = 0xe7e2da;
		WorldGlobals.FOG_START = 8.0f;
		WorldGlobals.FOG_DENSITY = 0.03f;
		
		/* tree */
		Scene scene = Scene.getInstance();
		GameNode root = scene.getRoot();
		
		TextLabel debug = new TextLabel("asd");
		debug.setPosition(8, 128, 0);
		scene.getFlatRoot().addChild(debug);
		
		/* model */
		modelPoly = new PolyModel("res/scene.obj");
		PolyModel modelSmooth = new PolyModel("res/scene_smooth.obj");
		model = new ModelNode("", modelPoly, modelSmooth, Texture.fromFile("res/scene_uv.png"));
		model.setHighlighted(true);
		model.setPosition(0, -1, 0);
		
		camera = new PerspectiveCamera("camera", (float)Math.toRadians(55), (float)width/height, 0.1f, 256);
		terrain = new TerrainNode();
		NavMeshNode navMesh = new NavMeshNode("test_navmesh", "maps/test/testNavMesh.navmesh");
		terrain.addChild(navMesh);
		camera.addGameUpdater(new CameraController(camera));
		
		/*root.addGameUpdater(new IGameUpdater () {

			@Override
			public void update(GameNode object) {
				int winX = Input.getMouseX();
				int winY = Input.getMouseY();
				Vector3f[] ray = LinearUtils.unProject(winX, winY);	// 0 -> point,  1-> direction
				float lambda = -ray[0].y / ray[1].y;
				float x = ray[0].x + lambda * ray[1].x;
				float z = ray[0].z + lambda * ray[1].z;
				debug.setText("X = "+x+" , Z = "+z);
			}
			
		});*/
		
		PolyModel spherePoly = new PolyModel("res/sphere.obj");
		ModelNode sphereModel = new ModelNode("test_sphere", spherePoly, spherePoly, Texture.fromFile("res/pixel.png"));
		sphereModel.setHighlighted(true);
		root.addChild(sphereModel);
		
		Framework.getInstance().addMouseListener(new IMouseListener () {

			@Override
			public void mouseDown(int button) {
				int winX = Input.getMouseX();
				int winY = Input.getMouseY();
				Vector3f[] ray = LinearUtils.unProject(winX, winY);	// 0 -> point,  1-> direction
				float lambda = -ray[0].y / ray[1].y;
				float x = ray[0].x + lambda * ray[1].x;
				float z = ray[0].z + lambda * ray[1].z;
				ConvexNode n = navMesh.getPositionNode(new Vector3f(x,0,z));
				if (n != null) {
					Vector3f mid = n.getMidPoint();
					sphereModel.setPosition(mid.x, mid.y+2, mid.z);
				}
			}
			
		});
		
		/* sound */
		SoundSource ambient = new SoundSource(Resources.get("ambient.wav"), true, true);
		SoundSourceNode ambientNode = new SoundSourceNode("birds", ambient);
		root.addChild(ambientNode);
		ambientNode.play();
		
		/* cebra */
		Decal cebra = new Decal("decals/cebra.decal");
		Decal sewer = new Decal("decals/sewer.decal");
		DecalNode ceb = new DecalNode(cebra);
		ceb.setSize(32, 4, 16);
		ceb.setPosition(37.847725f, 0.0f, -38.863556f);
		ceb.setRotation(0,-0.1f,0);
		terrain.addChild(ceb);
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
		DecalNode sew = new DecalNode(sewer);
		sew.setSize(5, 2, 5);
		sew.setPosition(0, 0, 0);
		terrain.addChild(sew);
		
		/* graffity */
		Decal cats = new Decal("decals/cats.decal");
		DecalNode cat = new DecalNode(cats);
		cat.setSize(12, 4, 6);
		cat.setPosition(43.037216f, 6.2567043f, -9.978825f);
		cat.setRotation(1.6000001f, 0.0f, 0.0f);
		cat.setOpacity(0.75f);
		terrain.addChild(cat);
		
		/* blood */
		Decal splat = new Decal("decals/blood.decal");
		for (int i = -8; i < 8; ++i) {
			for (int x = -8; x < 8; ++x) {
				if (i == 0 && x == 0 || Math.random() < 0.25) continue;
				DecalNode dec = new DecalNode(splat);
				dec.setPosition(32*i, 0, 32*x);
				dec.setRotation((float) (Math.PI*Math.random())*0.25f, (float) (Math.PI*Math.random())*2, (float) (Math.PI*Math.random())*0.25f);
				dec.setSize(32,8,32);
				terrain.addChild(dec);
			}
		}
		
		/* birds */
		for (int i = 0; i < 32; ++i) {
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
		
		root.addChild(camera);
		root.addChild(terrain);
		root.addChild(model);
		scene.setUsedCamera(camera);
		scene.setRenderShadows(true);
	}
	
	public static void main (String[] args) {
		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		new Reasemble();
	}

}
