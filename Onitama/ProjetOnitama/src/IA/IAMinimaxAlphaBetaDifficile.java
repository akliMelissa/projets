package IA;
import IA.evaluation.*;
import Modele.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
/**
 * IAMinimaxAlphaBeta : implémentation d'une IA utilisant Minimax avec Alpha-Beta,
 * ordonnancement des coups et extensions ciblées.
 */
public class IAMinimaxAlphaBetaDifficile extends IA {

    /** true si l'IA contrôle les pièces bleues, false si rouges */
    private final boolean estBleu; 

    /** Temps maximal de réflexion (ms) */
    private static final int TIME_LIMIT_MS = 2000;

    /** Profondeur minimale et maximale pour Minimax */
    private static final int MAX_DEPTH = 15;
    private static final int MIN_DEPTH = 2;

    /** Nombre maximal d'extensions singulières */
    private static final int MAX_SINGULAR_EXTENSIONS = 5; 

    /** Évaluateur heuristique pour estimer les positions */
    private Evaluateur eval;

    /**
     * Initialise l'IA avec le jeu, la couleur et l'évaluateur.
     */
    public IAMinimaxAlphaBetaDifficile(Jeu jeu, boolean estBleu, Evaluateur eval) {
        this.jeu = jeu;
        this.estBleu = estBleu;
        this.eval = eval;
    }

    @Override
    public int[] proposerCoup() {
        boolean joueurCourant = this.jeu.getjoueurCourant();

        if (joueurCourant != estBleu) {
            throw new IllegalStateException("L'IA " + (estBleu ? "bleue" : "rouge") +
                    " a été appelée pendant le tour du joueur " +
                    (joueurCourant ? "bleu" : "rouge"));
        }

        ConfigurationCompacte root = ConfigurationCompacte.fromJeu(this.jeu, estBleu);
        long start = System.currentTimeMillis();
        int depth = MIN_DEPTH;

        // Vérification préalable : aucun coup possible ?
        List<int[]> coupsPossibles = root.genererCoupsPossibles();
        if (coupsPossibles.isEmpty()) {
            System.out.println("Aucun coup possible - on procède à une défausse intelligente");
            int carteDefausse = defausseIntelligente(this.jeu, this.estBleu, this.eval, 3);
            return new int[]{-1, -1, -1, -1, carteDefausse};
        }

        // Deepening progressif avec mémorisation du dernier résultat valide
        ResultatMinimax dernierValide = null;

        while (depth <= MAX_DEPTH && System.currentTimeMillis() - start < TIME_LIMIT_MS * 0.9) {
            ResultatMinimax r = minimaxWithMoveOrdering(
                    root, depth, -Double.MAX_VALUE, Double.MAX_VALUE,
                    true, start, 0
            );

            if (r != null && r.coup != null) {
                dernierValide = r;

                if (r.score > 500000) {
                    System.out.println("Coup gagnant trouvé à profondeur " + depth);
                    break;
                }
            }

            depth++;

        }

        System.out.println("Profondeur atteinte: " + (depth - 1));

        // Retour du meilleur coup trouvé si existant
        if (dernierValide != null) {
            return dernierValide.coup;
        }

        // Minimax a échoué, mais des coups existent → on joue le premier
        System.out.println("Minimax a échoué mais des coups sont disponibles, choix par défaut");
        return coupsPossibles.get(0);
    }

    /**
     * Minimax avec ordonnancement des coups pour améliorer l'élagage alpha-beta
     */
    private ResultatMinimax minimaxWithMoveOrdering(ConfigurationCompacte conf,
                                                    int prof, double alpha, double beta,
                                                    boolean maximizingPlayer,
                                                    long startTime, int extensions) {

        // Vérification du temps
        if (startTime > 0 && System.currentTimeMillis() - startTime > TIME_LIMIT_MS) {
            return null;
        }

        // Position terminale
        if (conf.estTerminee()) {
            double sc = eval.evaluate(conf);
            return new ResultatMinimax(sc, null);
        }

        // Profondeur maximale
        if (prof == 0) {
            double sc = eval.evaluate(conf);

            // Extension en cas de situation critique
            if (extensions < MAX_SINGULAR_EXTENSIONS && isCriticalPosition(conf)) {
                ResultatMinimax ext = minimaxWithMoveOrdering(
                        conf, 1, alpha, beta, maximizingPlayer,
                        startTime, extensions + 1
                );
                if (ext != null) sc = ext.score;
            }

            return new ResultatMinimax(sc, null);
        }
        // Génération et tri des coups
        List<CoupEvalue> coups = genererEtTrierCoups(conf, maximizingPlayer);
        double bestScore = maximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        int[] bestMove = null;
        
        for (CoupEvalue coupEval : coups) {
            ConfigurationCompacte next = conf.appliquerCoup(coupEval.coup);

            ResultatMinimax sub = minimaxWithMoveOrdering(
                    next, prof - 1, alpha, beta,
                    !maximizingPlayer, startTime, extensions
            );

            if (sub == null) return null; // Timeout

            double sc = sub.score;

            if (maximizingPlayer) {
                if (sc > bestScore) {
                    bestScore = sc;
                    bestMove = coupEval.coup;
                }
                alpha = Math.max(alpha, sc);
            } else {
                if (sc < bestScore) {
                    bestScore = sc;
                    bestMove = coupEval.coup;
                }
                beta = Math.min(beta, sc);
            }

            // Élagage alpha-beta
            if (alpha >= beta) {
                break; // Coupure
            }
        }
        return new ResultatMinimax(bestScore, bestMove);
    }


    /**
     * Génère et trie les coups par ordre d'intérêt (les meilleurs d'abord)
     */
    private List<CoupEvalue> genererEtTrierCoups(ConfigurationCompacte conf, boolean maximizing) {
        List<int[]> coupsPossibles = conf.genererCoupsPossibles();
        List<CoupEvalue> coupsEvalues = new ArrayList<>();

        for (int[] coup : coupsPossibles) {
            ConfigurationCompacte next = conf.appliquerCoup(coup);
            double score = evaluationRapide(next, coup);
            coupsEvalues.add(new CoupEvalue(coup, score));
        }

        // Tri décroissant si maximizing, croissant sinon
        if (maximizing) {
            Collections.sort(coupsEvalues, (a, b) -> Double.compare(b.evaluation, a.evaluation));
        } else {
            Collections.sort(coupsEvalues, (a, b) -> Double.compare(a.evaluation, b.evaluation));
        }

        return coupsEvalues;
    }


    /**
     * Évaluation rapide pour trier les coups (heuristiques simples)
     */
    private double evaluationRapide(ConfigurationCompacte conf, int[] coup) {
        double score = 0;

        // Bonus pour les coups qui menacent le roi adverse
        if (menaceRoiAdverse(conf, coup)) {
            score += 200;
        }

        // Pénalité pour les coups qui exposent notre roi
        if (exposeNotreRoi(conf, coup)) {
            score -= 150;
        }

        return score;
    }

    /**
     * Détecte les positions critiques nécessitant une extension
     */
    private boolean isCriticalPosition(ConfigurationCompacte conf) {
        // Extension si le roi est menacé
        if (isKingThreatened(conf, estBleu)) {
            return true;
        }

        // Extension si notre roi est très proche du temple
        if (distKingGoal(conf, estBleu) <= 1) {
            return true;
        }

        // Extension si le roi adverse est très proche du temple
        if (distKingGoal(conf, !estBleu) <= 1) {
            return true;
        }

        return false;
    }

    // ============ MÉTHODES UTILITAIRES POUR L'ÉVALUATION RAPIDE ============
    private boolean menaceRoiAdverse(ConfigurationCompacte conf, int[] coup) {
        // Vérifier si après ce coup, le roi adverse est menacé
        ConfigurationCompacte next = conf.appliquerCoup(coup);
        return isKingThreatened(next, !estBleu);
    }

    private boolean exposeNotreRoi(ConfigurationCompacte conf, int[] coup) {
        // Vérifier si le coup expose notre roi au danger
        ConfigurationCompacte next = conf.appliquerCoup(coup);
        return isKingThreatened(next, estBleu);
    }

    private boolean isKingThreatened(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu ? ConfigurationCompacte.ROI_BLEU : ConfigurationCompacte.ROI_ROUGE;
        int kingX = -1, kingY = -1;

        for (int y = 0; y < 5 && kingX < 0; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == codeRoi) {
                    kingX = x; kingY = y; break;
                }
            }
        }
        if (kingX < 0) return false;

        CarteJeu[] ref = ConfigurationCompacte.getReferenceCartes();
        List<CarteJeu> mainAdv = new ArrayList<>();
        if (bleu) {
            mainAdv.add(ref[s.getCarte(0)]);
            mainAdv.add(ref[s.getCarte(1)]);
        } else {
            mainAdv.add(ref[s.getCarte(2)]);
            mainAdv.add(ref[s.getCarte(3)]);
        }

        byte pionAdv = bleu ? ConfigurationCompacte.PION_ROUGE : ConfigurationCompacte.PION_BLEU;
        byte roiAdv = bleu ? ConfigurationCompacte.ROI_ROUGE : ConfigurationCompacte.ROI_BLEU;

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                byte c = s.getCase(x, y);
                if (c != pionAdv && c != roiAdv) continue;
                boolean pieceEstRouge = (c == ConfigurationCompacte.PION_ROUGE || c == ConfigurationCompacte.ROI_ROUGE);

                for (CarteJeu carte : mainAdv) {
                    for (java.awt.Point delta : carte.getDeplacementsRelatifs()) {
                        int dx = delta.x, dy = delta.y;
                        if (pieceEstRouge) {
                            dx = -dx; dy = -dy;
                        }
                        if (x + dx == kingX && y + dy == kingY) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int distKingGoal(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu ? ConfigurationCompacte.ROI_BLEU : ConfigurationCompacte.ROI_ROUGE;
        int rx = -1, ry = -1;
        for (int y = 0; y < 5 && rx < 0; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == codeRoi) {
                    rx = x; ry = y; break;
                }
            }
        }
        if (rx < 0) return 10;
        int templeY = bleu ? 0 : 4;
        return Math.abs(rx - 2) + Math.abs(ry - templeY);
    }

    /**
     * Classe pour associer un coup à son évaluation rapide
     */
    private static class CoupEvalue {
        final int[] coup;
        final double evaluation;

        CoupEvalue(int[] coup, double evaluation) {
            this.coup = coup;
            this.evaluation = evaluation;
        }
    }

    public static class ResultatMinimax {
        public final double score;
        public final int[] coup;

        public ResultatMinimax(double score, int[] coup) {
            this.score = score;
            this.coup = coup;
        }
    }

    /**
     * Défausse intelligente améliorée
     */
    private static int defausseIntelligente(Jeu jeu, boolean estBleu, Evaluateur eval, int depth) {
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestIdxLocal = 0;

        for (int idxLocal = 0; idxLocal < 2; idxLocal++) {
            try {
                // Clone et simulation de la défausse
                Jeu jeuClone = jeu.clone();
                CarteJeu carteToDrop = estBleu
                        ? jeuClone.getJoueur2().getCarte(idxLocal)
                        : jeuClone.getJoueur1().getCarte(idxLocal);
                jeuClone.swapCartes(carteToDrop);

                double score;
                boolean joueurCourant = jeuClone.getjoueurCourant();

                if (jeuClone.tour_est_jouable()) {
                    ConfigurationCompacte conf = ConfigurationCompacte.fromJeu(jeuClone, estBleu);
                    boolean estNotreTour = (joueurCourant == estBleu);

                    // Évaluation rapide pour la défausse
                    IAMinimaxAlphaBetaDifficile ia = new IAMinimaxAlphaBetaDifficile(jeuClone, estBleu, eval);
                    ResultatMinimax res = ia.minimaxWithMoveOrdering(conf, Math.max(depth, 2),
                            -100000, 100000, estNotreTour, 0L, 0);

                    score = (res != null) ? res.score : eval.evaluate(conf);
                } else {
                    ConfigurationCompacte conf = ConfigurationCompacte.fromJeu(jeuClone, estBleu);
                    score = eval.evaluate(conf);
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestIdxLocal = idxLocal;
                }
            } catch (Exception e) {
                // En cas d'erreur, on continue avec l'autre carte
                System.err.println("Erreur lors de l'évaluation de la défausse: " + e.getMessage());
            }
        }

        return bestIdxLocal;
    }
}