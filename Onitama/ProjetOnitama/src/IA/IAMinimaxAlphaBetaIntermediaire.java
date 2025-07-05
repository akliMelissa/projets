package IA;
import IA.evaluation.*;
import Modele.*;

import java.util.ArrayList;
import java.util.List;

/**
     * Utilise un Evaluateur fourni.
 */
public class IAMinimaxAlphaBetaIntermediaire extends IA {

    private final boolean estBleu;  // True si l'IA joue les bleus
    private static final int TIME_LIMIT_MS = 2000; //Temps de reflexion
    private static final int MAX_SINGULAR_EXTENSIONS = 5;
    private Evaluateur eval;              

    public IAMinimaxAlphaBetaIntermediaire(Jeu jeu, boolean estBleu, Evaluateur eval) {
        this.jeu = jeu;
        this.estBleu = estBleu;
        this.eval = eval;
    }

    @Override
    public int[] proposerCoup() {
        // On utilise toujours le point de vue de NOTRE joueur (estBleu)
        // mais on doit vérifier si c'est bien notre tour de jouer
        boolean joueurCourant = this.jeu.getjoueurCourant();

        // Si ce n'est pas notre tour, on ne devrait pas être appelé
        if (joueurCourant != estBleu) {
            throw new IllegalStateException("L'IA " + (estBleu ? "bleue" : "rouge") +
                    " a été appelée pendant le tour du joueur " +
                    (joueurCourant ? "bleu" : "rouge"));
        }

        // Nous recréons la configuration compacte depuis notre point de vue
        ConfigurationCompacte root = ConfigurationCompacte.fromJeu(this.jeu, estBleu);
        long start = System.currentTimeMillis();
        int depth = 1;
        int[] best = null;

        // itération jusqu'à la limite de temps
        while (System.currentTimeMillis() - start < TIME_LIMIT_MS) {
            ResultatMinimax r = this.minimax(
                    root,
                    depth,
                    -Double.MAX_VALUE,
                    Double.MAX_VALUE,
                    true, // on commence toujours par maximiser au niveau 0 (notre tour)
                    start,
                    0
            );
            if (r != null && r.coup != null) {
                best = r.coup;
            }
            depth++;
        }
        System.out.println(depth);

        if (best == null) {
            // on force la défausse pour **notre** IA (estBleu)
            int carteDefausse = defausseIntelligente(this.jeu, this.estBleu, this.eval, 2);
            return new int[]{-1, -1, -1, -1, carteDefausse};
        }
        return best;
    }

 

    public ResultatMinimax getMinimaxResultat(ConfigurationCompacte conf, int prof, double alpha, double beta,
                                              boolean maximizingPlayer) {
        return minimax(conf, prof, alpha, beta, maximizingPlayer, 0L, 0); // 0L = aucune limite de temps
    }

    // -------------------------------------------------
    // --- Internals Minimax α-β + extensions ---
    // -------------------------------------------------
    public static class ResultatMinimax {
        public final double score;
        public final int[] coup;
        public ResultatMinimax(double score, int[] coup) {
            this.score = score;
            this.coup = coup;
        }
    }

    private ResultatMinimax minimax(ConfigurationCompacte conf,
                                    int prof, double alpha, double beta,
                                    boolean maximizingPlayer, // true = MAX, false = MIN
                                    long startTime,
                                    List<Double> prevScores,
                                    int extensions) {

        // Vérification du temps écoulé
        if (startTime > 0 && System.currentTimeMillis() - startTime > TIME_LIMIT_MS) return null;

        // Position terminale
        if (conf.estTerminee()) {
            double sc = eval.evaluate(conf);
            prevScores.add(sc);
            // Note: PAS besoin d'inverser le score ici car l'evaluateur tient déjà compte de la vue (bleu/rouge)
            return new ResultatMinimax(sc, null);
        }

        // Profondeur maximale atteinte
        if (prof == 0) {
            double sc = eval.evaluate(conf);
            prevScores.add(sc);

            // Extension singulière si score suspect
            if (extensions < MAX_SINGULAR_EXTENSIONS && scoreTropEcarte(sc, prevScores)) {
                ResultatMinimax ext = minimax(
                        conf, 1, alpha, beta, maximizingPlayer,
                        startTime, new ArrayList<>(), extensions + 1
                );
                if (ext != null) sc = ext.score;
            }
            return new ResultatMinimax(sc, null);
        }

        // Préparation de l'exploration des coups
        double bestScore = maximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        int[] bestMove = null;

        // Exploration des coups possibles
        for (int[] mv : conf.genererCoupsPossibles()) {
            ConfigurationCompacte next = conf.appliquerCoup(mv);

            // IMPORTANT: Après l'application du coup, on change de joueur
            // donc l'évaluateur voit la position du point de vue opposé
            // C'est pourquoi on inverse maximizingPlayer
            ResultatMinimax sub = minimax(
                    next, prof - 1,
                    alpha, beta,
                    !maximizingPlayer, // Alternance MAX/MIN
                    startTime, prevScores, extensions
            );

            double sc = (sub != null) ? sub.score : eval.evaluate(next);

            // Mise à jour du meilleur score selon le joueur (MAX ou MIN)
            if (maximizingPlayer) {
                if (sc > bestScore) {
                    bestScore = sc;
                    bestMove = mv;
                }
                alpha = Math.max(alpha, sc);
            } else {
                if (sc < bestScore) {
                    bestScore = sc;
                    bestMove = mv;
                }
                beta = Math.min(beta, sc);
            }

            // Élagage alpha-beta
            if (alpha >= beta) {
                break;
            }
        }

        return new ResultatMinimax(bestScore, bestMove);
    }

    // Wrapper pour préserver les appels à 7 arguments
    private ResultatMinimax minimax(ConfigurationCompacte conf,
                                    int prof, double alpha, double beta,
                                    boolean maximizingPlayer,
                                    long startTime,
                                    int extensions) {
        return minimax(conf, prof, alpha, beta, maximizingPlayer,
                startTime, new ArrayList<Double>(), extensions);
    }

    // Utilitaire : détection des scores aberrants (pour l'extension singulière)
    private boolean scoreTropEcarte(double score, List<Double> list) {
        if (list.size() < 2) return false;
        double sum = 0;
        for (double s : list) sum += s;
        double mean = sum / list.size();
        double var = 0;
        for (double s : list) var += (s - mean) * (s - mean);
        var /= list.size();
        double sd = Math.sqrt(var);
        return Math.abs(score - mean) > 2 * sd;
    }

    /**
     * Teste chacune des deux cartes de la main, simule la défausse, évalue,
     * et renvoie l'indice local (0 ou 1) qui maximise l'évaluation.
     */
    private static int defausseIntelligente(Jeu jeu,
                                            boolean estBleu,
                                            Evaluateur eval,
                                            int depth) {
        // Nous voulons maximiser le score du point de vue de notre IA
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestIdxLocal = 0;

        for (int idxLocal = 0; idxLocal < 2; idxLocal++) {
            // 1) clone + swap
            Jeu jeuClone = jeu.clone();
            CarteJeu carteToDrop = estBleu
                    ? jeuClone.getJoueur2().getCarte(idxLocal)
                    : jeuClone.getJoueur1().getCarte(idxLocal);
            jeuClone.swapCartes(carteToDrop);

            // 2) évaluation
            double score;
            // On récupère le joueur courant après le swap
            boolean joueurCourant = jeuClone.getjoueurCourant();

            // Si le jeu peut continuer, on simule la suite
            if (jeuClone.tour_est_jouable()) {
                // On crée une configuration du point de vue du joueur courant
                ConfigurationCompacte conf = ConfigurationCompacte.fromJeu(jeuClone, estBleu);

                // On commence par maximiser si c'est notre tour
                boolean estNotreTour = (joueurCourant == estBleu);

                IAMinimaxAlphaBetaIntermediaire ia = new IAMinimaxAlphaBetaIntermediaire(jeuClone, estBleu, eval);
                ResultatMinimax res = ia.getMinimaxResultat(conf, depth,
                        -1_000_000, 1_000_000,
                        estNotreTour);

                score = (res != null) ? res.score : eval.evaluate(conf);
            } else {
                // Si le jeu est terminé, on évalue simplement l'état actuel
                ConfigurationCompacte conf = ConfigurationCompacte.fromJeu(jeuClone, estBleu);
                score = eval.evaluate(conf);
            }

            // 3) on retient l'indice local qui donne le meilleur score
            if (score > bestScore) {
                bestScore = score;
                bestIdxLocal = idxLocal;
            }
        }

        return bestIdxLocal;
    }
}
