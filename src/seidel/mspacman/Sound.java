package seidel.mspacman;

import java.io.File;
import java.io.InputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Sound {

    static boolean mute;
    Clip clip;

    public Sound(String file) {
        try {
            String path = "/sounds/" + file;
            clip = AudioSystem.getClip();

            // read and open audio clip from resources
            InputStream inputStream = this.getClass().getResourceAsStream(path);
            clip.open(AudioSystem.getAudioInputStream(inputStream));
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