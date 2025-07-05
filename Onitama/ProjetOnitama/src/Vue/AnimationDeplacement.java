package Vue;

import java.awt.*;
import javax.swing.Timer;

/**** Classe qui gère l'animation de déplacement d'un pion */
public class AnimationDeplacement {

    private static final int DUREE_ANIMATION = 1500; // Durée de l’animation
    private static final int INTERVALLE = 16;

    // Point de depart et d’arrivee du pion
    private final Point debut;
    private final Point fin;

    // Image de pion a deplacer
    private final Image imagePiece;
    private final PlateauGraphique plateau;
    private final Timer timer;
    private long tempsDebut;
    private float progression = 0f;


    //Constructeur
    public AnimationDeplacement(Point debut, Point fin, Image imagePiece, PlateauGraphique plateau) {
        this.debut = debut;
        this.fin = fin;
        this.imagePiece = imagePiece;
        this.plateau = plateau;
        this.timer = new Timer(INTERVALLE, e -> mettreAJourAnimation());
        this.timer.setRepeats(true);
    }

    //demarrage de l’animation
    public void start() {
        tempsDebut = System.currentTimeMillis();
        timer.start();
    }


    // progression temporelle de l’animation
    private void mettreAJourAnimation() {

        // calcul du temps ecoule depuis le demarrage
        long tempsEcoule = System.currentTimeMillis() - tempsDebut;
        progression = Math.min(1.0f, (float) tempsEcoule / DUREE_ANIMATION);

        // mise a jour du plateau graphique avec la nouvelle progression
        plateau.setAnimationProgress(debut, fin, imagePiece, progression);

        // fin d’animation
        if (progression >= 1.0f) {
            timer.stop();
            plateau.endAnimation();
        }
    }

   // arret d'animation
    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
