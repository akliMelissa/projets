package IA.evaluation;

import Modele.*;
import java.io.Serializable;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Évaluateur amélioré pour Onitama avec une pondération stratégiquement optimisée
 */
public class EvaluateurDifficile implements Evaluateur, Serializable {
    private static final long serialVersionUID = 1L;

    /** true si l'IA joue les pièces bleues, false sinon */
    private final boolean iaBleue;

    /** Tableau des poids pour chaque critère actif */
    private final double[] poids;

    // Indices correspondant aux critères dans le tableau poids
    private static final int IDX_DISTANCE_ROI = 0;    // Distance du roi au temple adverse
    private static final int IDX_SECURITE_ROI = 1;    // Sécurité du roi (menace immédiate)
    private static final int IDX_CONTROLE_CENTRE = 2; // Contrôle des cases centrales
    private static final int IDX_FORMATION = 3;       // Coordination et protection du roi

    // Valeurs absolues pour les états terminaux
    private static final double VICTOIRE_TEMPLE = 1000000.0;
    private static final double VICTOIRE_CAPTURE = 1000000.0;
    private static final double DEFAITE_TEMPLE = -1000000.0;
    private static final double DEFAITE_CAPTURE = -1000000.0;
    private static final double ROI_EN_DANGER = -50000.0;

    /**
     * Constructeur : initialise l'IA et les poids des critères.
     * @param iaBleue true si l'IA contrôle les pièces bleues
     */
    public EvaluateurDifficile(boolean iaBleue) {
        this.iaBleue = iaBleue;
        // Poids ajustés selon l'importance stratégique de chaque critère
        this.poids = new double[]{
                100.0,  // Distance roi vers temple
                200.0,  // Sécurité du roi
                50.0,   // Contrôle du centre
                40.0   // Formation et positionnement
        };
    }

    /**
     * Calcule le score heuristique d'une configuration de jeu.
     * Si l'état est terminal, renvoie un score extrême.
     */
    @Override
    public double evaluate(ConfigurationCompacte s) {
        // Vérifications des états terminaux
        double terminalScore = evaluateTerminalStates(s);
        if (Math.abs(terminalScore) > 500000) {
            return terminalScore;
        }

        double score = 0.0;

        // 1. Distance du roi vers le temple
        score += evaluateKingDistance(s) * poids[IDX_DISTANCE_ROI];

        // 2. Sécurité du roi
        score += evaluateKingSafety(s) * poids[IDX_SECURITE_ROI];

        // 3. Contrôle du centre
        score += evaluateCenterControl(s) * poids[IDX_CONTROLE_CENTRE];

        // 4. Formation et positionnement
        score += evaluateFormation(s) * poids[IDX_FORMATION];

        return score;
    }

    /**
     * Renvoie un score extrême si la partie est terminée :
     * arrivée au temple, capture du roi ou menace immédiate.
     */
    private double evaluateTerminalStates(ConfigurationCompacte s) {
        // Victoires/défaites par arrivée au temple
        if (arriveTemple(s, iaBleue)) return VICTOIRE_TEMPLE;
        if (arriveTemple(s, !iaBleue)) return DEFAITE_TEMPLE;

        // Victoires/défaites par capture du roi
        if (roiMort(s, iaBleue)) return VICTOIRE_CAPTURE;
        if (roiMort(s, !iaBleue)) return DEFAITE_CAPTURE;

        // Roi en danger immédiat
        if (isKingThreatened(s, iaBleue)) return ROI_EN_DANGER;

        return 0.0;
    }

    /**
     * Score lié à la proximité du roi par rapport au temple adverse.
     */
    private double evaluateKingDistance(ConfigurationCompacte s) {
        int distanceNous = distKingGoal(s, iaBleue);
        int distanceAdv = distKingGoal(s, !iaBleue);

        // Plus notre roi est proche du temple, mieux c'est
        // Plus le roi adverse est proche, moins c'est bien
        double scoreDistance = (10 - distanceNous) - (10 - distanceAdv) * 0.8;

        // Bonus si notre roi est très proche
        if (distanceNous <= 2) scoreDistance += 20;
        if (distanceNous == 1) scoreDistance += 30;

        return scoreDistance;
    }
    
    /**
     * Score de sécurité : pénalité si roi menacé, bonus si roi adverse menacé.
     */
    private double evaluateKingSafety(ConfigurationCompacte s) {
        double safety = 0.0;

        // Pénalité si le roi est menacé
        if (isKingThreatened(s, iaBleue)) {
            safety -= 100;
        }

        // Bonus si le roi adverse est menacé
        if (isKingThreatened(s, !iaBleue)) {
            safety += 80;
        }

       
        return safety;
    }

    /**
     * Mesure le contrôle des cases centrales du plateau.
     */
    private double evaluateCenterControl(ConfigurationCompacte s) {
        double centerControl = 0.0;

        // Cases centrales importantes (1,1), (2,1), (3,1), (1,2), (2,2), (3,2), (1,3), (2,3), (3,3)
        int[][] centerSquares = {{1,1}, {2,1}, {3,1}, {1,2}, {2,2}, {3,2}, {1,3}, {2,3}, {3,3}};

        for (int[] pos : centerSquares) {
            byte piece = s.getCase(pos[0], pos[1]);
            double weight = (pos[0] == 2 && pos[1] == 2) ? 3.0 : 1.0; // Centre plus important

            if (isPieceOfColor(piece, iaBleue)) {
                centerControl += weight;
            } else if (isPieceOfColor(piece, !iaBleue)) {
                centerControl -= weight * 0.8;
            }
        }

        return centerControl;
    }
    
  
    /**
     * Formation : coordination des pièces et protection du roi.
     */
    private double evaluateFormation(ConfigurationCompacte s) {
        double formation = 0.0;

        // Bonus pour garder les pièces groupées
        formation += evaluatePieceCoordination(s, iaBleue) * 5;
        formation -= evaluatePieceCoordination(s, !iaBleue) * 3;

        // Bonus pour une bonne protection du roi
        formation += evaluateKingProtection(s, iaBleue) * 8;

        return formation;
    }

  

    // ============ MÉTHODES UTILITAIRES ============

    /** Retourne la position du roi pour la couleur donnée. */
    private Point getKingPosition(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu ? ConfigurationCompacte.ROI_BLEU : ConfigurationCompacte.ROI_ROUGE;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == codeRoi) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }


    /** Vérifie si une pièce appartient à la couleur donnée. */
    private boolean isPieceOfColor(byte piece, boolean bleu) {
        if (bleu) {
            return piece == ConfigurationCompacte.PION_BLEU || piece == ConfigurationCompacte.ROI_BLEU;
        } else {
            return piece == ConfigurationCompacte.PION_ROUGE || piece == ConfigurationCompacte.ROI_ROUGE;
        }
    }

    /** Compte le nombre de coups possibles pour la couleur donnée. */
    private int countPossibleMoves(ConfigurationCompacte s, boolean bleu) {
        return s.genererCoupsPossibles().size(); 
    }

    /**
     * Calcule la coordination des pièces :
     * nombre de paires de pions amis dont la distance Manhattan <=2.
     */
    private int evaluatePieceCoordination(ConfigurationCompacte s, boolean bleu) {
        // Évaluer si les pièces travaillent ensemble
        int coordination = 0;
        byte pion = bleu ? ConfigurationCompacte.PION_BLEU : ConfigurationCompacte.PION_ROUGE;

        List<Point> pieces = new ArrayList<>();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == pion) {
                    pieces.add(new Point(x, y));
                }
            }
        }

        // Bonus pour les pièces proches les unes des autres
        for (int i = 0; i < pieces.size(); i++) {
            for (int j = i + 1; j < pieces.size(); j++) {
                Point p1 = pieces.get(i);
                Point p2 = pieces.get(j);
                int distance = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
                if (distance <= 2) {
                    coordination++;
                }
            }
        }

        return coordination;
    }

    /**
     * Évalue la protection du roi par le nombre de pions amis adjacents.
     * Parcourt les 8 cases entourant le roi pour compter les pions alliés.
     *
     */
    private int evaluateKingProtection(ConfigurationCompacte s, boolean bleu) {
        Point kingPos = getKingPosition(s, bleu);
        if (kingPos == null) return 0;

        int protection = 0;
        byte ourPion = bleu ? ConfigurationCompacte.PION_BLEU : ConfigurationCompacte.PION_ROUGE;

        // Compter les pions amis autour du roi
        int[][] directions = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
        for (int[] dir : directions) {
            int nx = kingPos.x + dir[0];
            int ny = kingPos.y + dir[1];

            if (nx >= 0 && nx < 5 && ny >= 0 && ny < 5) {
                if (s.getCase(nx, ny) == ourPion) {
                    protection++;
                }
            }
        }

        return protection;
    }

    /**
     * Calcule la distance de Manhattan entre le roi et le centre du temple adverse.
     */
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
     * Détermine si le roi est menacé par un déplacement adverse au prochain coup.
     */
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
                    for (Point delta : carte.getDeplacementsRelatifs()) {
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
    
    /**
     * Vérifie si le roi adverse a été capturé (absent du plateau).
     */
    private boolean roiMort(ConfigurationCompacte s, boolean bleu) {
        byte advRoi = bleu ? ConfigurationCompacte.ROI_ROUGE : ConfigurationCompacte.ROI_BLEU;
        boolean advAlive = false;
        for (int y = 0; y < 5 && !advAlive; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == advRoi) {
                    advAlive = true; break;
                }
            }
        }

        byte ourRoi = bleu ? ConfigurationCompacte.ROI_BLEU : ConfigurationCompacte.ROI_ROUGE;
        boolean ourAlive = false;
        for (int y = 0; y < 5 && !ourAlive; y++) {
            for (int x = 0; x < 5; x++) {
                if (s.getCase(x, y) == ourRoi) {
                    ourAlive = true; break;
                }
            }
        }

        return ourAlive && !advAlive;
    }
    
    /**
     * Vérifie si la case du temple est occupée par un roi (condition d'arrivée).
     */
    private boolean arriveTemple(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu ? ConfigurationCompacte.ROI_BLEU : ConfigurationCompacte.ROI_ROUGE;
        int templeY = bleu ? 0 : 4;
        return s.getCase(2, templeY) == codeRoi;
    }

}