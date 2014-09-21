package engine;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * @author germangb
 *
 */
public class SoundSource implements IResourceLoader {

	/**
	 * Sets the listener position
	 * @param x (1, 0, 0)
	 * @param y (0, 1, 0)
	 * @param z (0, 0, 1)
	 */
	public static void setListenerPosition (float x, float y, float z) {
		FloatBuffer buff = BufferUtils.createFloatBuffer(3);
		buff.put(x).put(y).put(z);
		buff.flip();
		AL10.alListener(AL10.AL_POSITION, buff);
		buff.clear();
	}
	
	/**
	 * Sets the listener position
	 * @param x (1, 0, 0)
	 * @param y (0, 1, 0)
	 * @param z (0, 0, 1)
	 */
	public static void setListenerOrientation (float lx, float ly, float lz, float upx, float upy, float upz) {
		FloatBuffer buff = BufferUtils.createFloatBuffer(6);
		buff.put(lx).put(ly).put(lz);
		buff.put(upx).put(upy).put(upz);
		buff.flip();
		AL10.alListener(AL10.AL_ORIENTATION, buff);
		buff.clear();
	}
	
	/* buffer that holds sound data */
	private IntBuffer buffer;
	private URL url;
	private boolean background, loop;
	
	/* Sources emitter */
	private int source;
	
	public SoundSource(URL url, boolean background, boolean loop) {
		ResourceManager.addResources(this);
		this.url = url;
		this.loop = loop;
		
		/* allocate buffers */
		this.buffer = BufferUtils.createIntBuffer(1);
		
		/* properties */
		this.background = background;
	}
	
	/**
	 * Returns the source that holds the sound
	 * data.
	 * 
	 * @return source sound
	 */
	public int getSource () {
		return source;
	}
	
	/**
	 * Set the position of the sound source
	 * @param x (1, 0, 0)
	 * @param y (0, 1, 0)
	 * @param z (0, 0, 1)
	 */
	public void setPosition (float x, float y, float z) {
		FloatBuffer buff = BufferUtils.createFloatBuffer(3);
		buff.put(x).put(y).put(z);
		buff.flip();
		AL10.alSource(source, AL10.AL_POSITION, buff);
		buff.clear();
	}

	//
	// BEGIN IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* reset error */
		AL10.alGetError();
		
		/* load wav file */
		WaveData wavFile = WaveData.create(url);
		AL10.alGenBuffers(buffer);
		AL10.alBufferData(buffer.get(0), wavFile.format, wavFile.data, wavFile.samplerate);
		wavFile.dispose();
		
		/* error check */
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return false;
		
		/* set up AL thing */
		source = AL10.alGenSources();
		AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
		AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, background ? 0.0f : 16.0f);
		AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 50);
		//AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, 100);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
		AL10.alSourcef(source, AL10.AL_MAX_GAIN, 1.0f);
		AL10.alSourcef(source, AL10.AL_MIN_GAIN, 0.0f);
		AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		
		/* final check */
		return AL10.alGetError() == AL10.AL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}
	
	//
	// END
	//

}
