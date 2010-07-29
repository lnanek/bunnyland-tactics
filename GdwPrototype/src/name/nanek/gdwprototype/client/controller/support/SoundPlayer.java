package name.nanek.gdwprototype.client.controller.support;


import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;

/**
 * Plays sounds.
 * 
 * @author Lance Nanek
 *
 */
public class SoundPlayer {
	//TODO advanced effects: fade in/out when changing music, adjust balance based on where on board something happens, player mute option
	
	//TODO some music files are really huge, like 5MB. make sure they can play streamed? or break them up into small chunks and play consecutively, or compress more?
	
	//TODO music stops when go to login page, show login page in a popup or something

	//private static final String MIME_TYPE_AUDIO_OGG_VORBIS = "audio/ogg; codecs=vorbis";
	
	private final SoundController soundController = new SoundController();

	private final Music menuScreenMusic = new Music(soundController.createSound(
    		Sound.MIME_TYPE_AUDIO_MPEG, "sound/menu_screen_music.mp3"), true);

    private final Music inGameMusic = new Music(soundController.createSound(
    		Sound.MIME_TYPE_AUDIO_MPEG, "sound/in-game_music.mp3"), true);

    private final Music winGameMusic = new Music(soundController.createSound(
    		Sound.MIME_TYPE_AUDIO_MPEG, "sound/win_game_music.mp3"), false);

    private final Music loseGameMusic = new Music(soundController.createSound(
    		Sound.MIME_TYPE_AUDIO_MPEG, "sound/lose_game_music.mp3"), false);
	
    //TODO just use enum argument instead of separate methods?
    
    //TODO have a collection of music instead of stopping each one explicitly
    
	public void playMenuScreenMusic() {
		inGameMusic.stop();
		loseGameMusic.stop();
		winGameMusic.stop();
		menuScreenMusic.start();
	}
	
	//public void stopMenuScreenMusic() {
	//	menuScreenMusic.stop();
	//}
	
	public void playInGameMusic() {
		menuScreenMusic.stop();
		loseGameMusic.stop();
		winGameMusic.stop();
		inGameMusic.start();
	}
	
	public void playWinGameMusic() {
		menuScreenMusic.stop();
		loseGameMusic.stop();
		inGameMusic.stop();
		winGameMusic.start();
	}
	
	public void playLoseGameMusic() {
		menuScreenMusic.stop();
		inGameMusic.stop();
		winGameMusic.stop();
		loseGameMusic.start();
	}
}
