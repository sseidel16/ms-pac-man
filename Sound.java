import java.io.File;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Sound {

  static boolean mute;
  Clip clip;

  public Sound(String file) {
    try {
      clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(new File("Ms_Pac-man/" + file)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play() {
    stop();
    clip.setFramePosition(0);
    if (!mute) clip.start();
  }

  public void stop() {
    clip.stop();
  }

  public void loop() {
    stop();
    clip.setFramePosition(0);
    if (!mute) clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

}