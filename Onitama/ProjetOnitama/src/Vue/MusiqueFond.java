package Vue;

import Global.UCC;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;


/*** Pour la gestion de son (WAV) dans l'application **/
public class MusiqueFond {

    private Clip clipFond;   // le son fond
    private float gainFond = -15.0f;   // baisse le volume fond
    private float gainEffet = 6.0f;    // boost le son de l’effet

    /** Lancement de la musique à volume réduit */
    public void demarrer(String fichierWav) {

        if (clipFond != null && clipFond.isRunning()) return;
        try {
            InputStream audio_src=UCC.ouvre(fichierWav);
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(audio_src));
            clipFond = AudioSystem.getClip();
            clipFond.open(ais);
            setGain(clipFond, gainFond);  // réduire le volume du fond
            clipFond.loop(Clip.LOOP_CONTINUOUSLY);
            clipFond.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //Arret de la musique de fond
    public void arreter() {
        if (clipFond != null) {
            clipFond.stop();
            clipFond.close();
            clipFond = null;
        }
    }

     // pouer jouer un effet avec volume audible ( pour la selcetion de pion ....)
    public void jouerEffet(String fichierWav) {
        new Thread(() -> {
            try {
                InputStream audio_src=UCC.ouvre(fichierWav);
                AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(audio_src));
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setGain(clip, gainEffet);  // augmenter le volume de l’effet
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();  // fin de clip
                    }
                });
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }, "EffetSonore").start();
    }

    private void setGain(Clip clip, float decibels) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(decibels);
        }
    }

}
