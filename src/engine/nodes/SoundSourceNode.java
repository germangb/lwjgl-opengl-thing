package engine.nodes;

import java.io.PrintStream;

import javax.vecmath.Vector3f;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import engine.GameNode;
import engine.SoundSource;
import engine.framework.Framework;

/**
 * @author germangb
 *
 */
public class SoundSourceNode extends GameNode {

	private SoundSource sound;
	//private boolean loop;
	private boolean play;
	
	/**
	 * Creates a sound source object
	 * @param key game object key
	 * @param sound sound source reference
	 * @param loop loop sound
	 */
	public SoundSourceNode(String key, SoundSource sound) {
		super(key);
		this.sound = sound;
		//this.loop = false;
		this.play = false;
	}
	
	/**
	 * get a reference of the sound object
	 * @return sound reference
	 */
	public SoundSource getSound () {
		return sound;
	}
	
	/**
	 * Play the sound applying all the positioning stuff.
	 * Set the listener to the camera location and set
	 * the source to the object world position
	 */
	public void play () {
		play = true;
	}
	
	/**
	 * Stop the sound source
	 */
	public void stop () {
		/* make sure there is an OpenAL context */
		if (AL.isCreated()) {
			AL10.alSourceStop(sound.getSource());
		} else {
			/* no context is created */
			PrintStream err = Framework.getInstance().getErrStream();
			err.println("OpenAL context mussing...");
		}
	}

	//
	// GameObject re-implementation
	//
	
	@Override
	public void update() {
		/* listener properties */
		Vector3f sourcePos = this.getWorldPosition();
		sound.setPosition(sourcePos.x, sourcePos.y, sourcePos.z);
		/* check play */
		if (play) {
			play = false;
			if (AL.isCreated()) {
				AL10.alSourcePlay(sound.getSource());
			} else {
				/* no context is created */
				Framework.getInstance().err("OpenAL context mussing...");
			}
		}
		
		/* check looping */
		/*if (loop) {
			int state = AL10.alGetSourcei(sound.getSource(), AL10.AL_SOURCE_STATE);
			if (state == AL10.AL_STOPPED) {
				play();
			}
		}*/
	}
	
	//
	// END
	//
	
}
