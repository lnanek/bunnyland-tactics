/**
 * 
 */
package name.nanek.gdwprototype.client.controller.support;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;

public class ManualSoundLooper implements SoundHandler {
	//TODO this looping method leaves an audible sound gap
	//look into wrappers for SoundManager2, a flash sound player with explicit loop option
	
	private final Sound sound;
	
	public ManualSoundLooper(Sound sound) {
		this.sound = sound;
	}
	
	@Override
	public void onPlaybackComplete(PlaybackCompleteEvent event) {
		sound.play();
	}
	
	@Override
	public void onSoundLoadStateChange(SoundLoadStateChangeEvent event) {
		//Do nothing.
	}
}