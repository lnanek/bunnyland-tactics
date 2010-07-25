package name.nanek.gdwprototype.client.controller;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;

/**
 * Plays sounds.
 * 
 * @author Lance Nanek
 *
 */
public class SoundPlayer {
    //TODO for sound effects, set the balance according to where on the board they happen?

	//private static final String MIME_TYPE_AUDIO_OGG_VORBIS = "audio/ogg; codecs=vorbis";

	private static final int BACKGROUND_VOLUME = 50;
	
	private static final class ManualSoundLooper implements SoundHandler {
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

	private final SoundController soundController = new SoundController();
    
    private final Sound sound = soundController.createSound(
    		Sound.MIME_TYPE_AUDIO_MPEG, "sound/menu_background_music.mp3");
			//OGG worked, but onPlaybackCompelte not supported.
			//MIME_TYPE_AUDIO_OGG_VORBIS, "sound/menu_background_music.ogg");
    {
    	sound.setVolume(BACKGROUND_VOLUME);
    }
    
    private final SoundHandler manualLooper = new ManualSoundLooper(sound);
	
    //TODO having individual screens call these results in music stopping and restarting
    //even when the same song is played. look into way to continue playing it, maybe even
    //fade out old song and fade in new song when it changes.
	public void startMenuBackgroundMusic() {
		sound.addEventHandler(manualLooper);
	    sound.play();		
	}
	
	public void stopMenuBackgroundMusic() {		
		sound.removeEventHandler(manualLooper);
		sound.stop();
	}
}
