
package IA;
import IA.evaluation.*;
import Modele.*;
import Modele.*;

/**
 * Classe de base abstraite pour toutes les IA du jeu Onitama.
 * Elle fournit une fabrique et définit l’interface à implémenter.
 */
public abstract class IA {

    /** Référence vers l’objet « moteur » de la partie. */
    protected Jeu jeu;

    /**
     * Indique si l’IA contrôle le camp bleu.
     * Vaut <code>false</code> si elle joue rouge.
     */

    /* ------------------------------------------------------------------ */
    /* Factory : sélectionne l’IA en fonction d’un « niveau »             */
    /* ------------------------------------------------------------------ */

    public static IA creerIA(Jeu jeu, int niveau, boolean bleu) {
        switch (niveau) {
            case 1:
                return new IAAleatoire(jeu, bleu);
            case 2:
                return new IAMinimaxAlphaBetaIntermediaire(jeu, bleu, new EvaluateurIntermediaire(bleu));
            case 3:
                return new IAMinimaxAlphaBetaDifficile(jeu, bleu, new EvaluateurDifficile(bleu));
            default:
                throw new IllegalArgumentException("Niveau inconnu : " + niveau);
        }
    }


    /* ------------------------------------------------------------------ */
    /* Interface à implémenter par les sous‑classes                       */
    /* ------------------------------------------------------------------ */

    /**
     * Demande à l’IA de proposer un coup légal pour la position courante.
     * Le format est [fromX, fromY, toX, toY, indexCarte].
     * @return le coup choisi
     */
    public abstract int[] proposerCoup();
}
