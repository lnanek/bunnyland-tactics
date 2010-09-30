package name.nanek.gdwprototype.client.controller.support;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;

/**
 * Music that can be looped and plays at half the volume of other sounds.
 * 
 * @author Lance Nanek
 *
 */
public class Music {
	
	private static final int MUSIC_VOLUME = 50;
	
	private boolean started;
	
	private final SoundHandler looper;
	
	private final Sound sound;

	public Music(Sound sound, boolean looped) {
		this.sound = sound;
		sound.setVolume(MUSIC_VOLUME);
		if ( looped ) {
			looper = new ManualSoundLooper(sound);
		} else {
			looper= null;
		}
	}
	
	public void start() {
		if ( started ) {
			return;
		}
		started = true;
		if ( null != looper ) {
			sound.addEventHandler(looper);
		}
		sound.play();				
	}
	
	public void stop() {
		started = false;
		if ( null != looper ) {
			sound.removeEventHandler(looper);
		}
		sound.stop();
	}
}