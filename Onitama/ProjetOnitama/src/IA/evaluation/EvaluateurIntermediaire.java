package IA.evaluation;

import Modele.*;
import java.io.Serializable;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;


/**
 * Évaluateur amélioré pour Onitama avec une pondération stratégiquement optimisée
 */
public class EvaluateurIntermediaire implements Evaluateur, Serializable {
    private static final long serialVersionUID = 1L;

    /** true si l’IA joue les pièces bleues, false si elle joue les rouges */
    private final boolean iaBleue;

    /**
     * Constructeur.
     * @param iaBleue indique si l’IA contrôle les pièces bleues (true) ou rouges (false)
     */
    public EvaluateurIntermediaire(boolean iaBleue) {
        this.iaBleue = iaBleue;
    }

    /**
     * Calcule la valeur heuristique d’une configuration de jeu.
     * @param s la configuration compacte du plateau
     * @return un score double, plus élevé = meilleur pour l’IA
     */
    @Override
    public double evaluate(ConfigurationCompacte s) {
        // 1) Distance au but
        int dist        = distRoiTemple(s, iaBleue);
        int distanceBut = 10 - dist;

        // 2) Pions capturés
        int pionRestantsAdv = nbPionsRestants(s, !iaBleue);
        int pionsCaptures  = 4 - pionRestantsAdv;

        // 3) Différence de pions restants
        int mesPionsRestants    = nbPionsRestants(s, iaBleue);
        int diffPions  = mesPionsRestants - pionRestantsAdv;

        // 4) Danger (roi menacé)
        int danger = roiMenace(s, iaBleue)
                ? -200_000
                : 0;

        // 5) roiMort ← bonus/malus sur l’arrivée au temple
        int arriveTemple = 0;
        if (arriveTemple(s, iaBleue))      arriveTemple =  +100;
        if (arriveTemple(s, !iaBleue))     arriveTemple =  -10_000;

        // 6) arriveTemple ← bonus/malus sur la capture du roi adverse
        int  roiMort= 0;
        if (roiMort(s, iaBleue))     roiMort   =  +100;
        if (roiMort(s, !iaBleue))    roiMort  =  -10_000;

        // Somme pondérée (danger et roiMort partagent IDX_DANGER)
        return distanceBut  
                + pionsCaptures 
                + diffPions     
                + danger        
                + roiMort     
                + arriveTemple;
    }

    // ——————————— Méthodes utilitaires ———————————
    /**
     * Calcule la distance de Manhattan entre le roi et le centre du temple adverse.
     */
    private int distRoiTemple(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu
                ? ConfigurationCompacte.ROI_BLEU
                : ConfigurationCompacte.ROI_ROUGE;
        int rx = -1, ry = -1;
        for (int y = 0; y < 5 && rx < 0; y++)
            for (int x = 0; x < 5; x++)
                if (s.getCase(x, y) == codeRoi) {
                    rx = x; ry = y; break;
                }
        if (rx < 0) return 10;
        int templeY = bleu ? 0 : 4;
        return Math.abs(rx - 2) + Math.abs(ry - templeY);
    }

    /**
     * Compte le nombre de pions (y compris le roi) d’une couleur donnée.
     */
    private int nbPionsRestants(ConfigurationCompacte s, boolean bleu) {
        byte pion = bleu
                ? ConfigurationCompacte.PION_BLEU
                : ConfigurationCompacte.PION_ROUGE;
        byte roi  = bleu
                ? ConfigurationCompacte.ROI_BLEU
                : ConfigurationCompacte.ROI_ROUGE;
        int cnt = 0;
        for (int y = 0; y < 5; y++)
            for (int x = 0; x < 5; x++) {
                byte c = s.getCase(x, y);
                if (c == pion || c == roi) cnt++;
            }
        return cnt;
    }

    /**
     * Détermine si le roi d’une couleur est menacé par un déplacement adverse.
     */
    private boolean roiMenace(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu
                ? ConfigurationCompacte.ROI_BLEU
                : ConfigurationCompacte.ROI_ROUGE;
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 5 && kingX < 0; y++)
            for (int x = 0; x < 5; x++)
                if (s.getCase(x, y) == codeRoi) {
                    kingX = x; kingY = y; break;
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

        byte pionAdv = bleu
                ? ConfigurationCompacte.PION_ROUGE
                : ConfigurationCompacte.PION_BLEU;
        byte roiAdv  = bleu
                ? ConfigurationCompacte.ROI_ROUGE
                : ConfigurationCompacte.ROI_BLEU;

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                byte c = s.getCase(x, y);
                if (c != pionAdv && c != roiAdv) continue;
                boolean pieceEstRouge = (c == ConfigurationCompacte.PION_ROUGE
                        || c == ConfigurationCompacte.ROI_ROUGE);

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
     * Détermine si le roi adverse a été capturé.
     */
    private boolean roiMort(ConfigurationCompacte s, boolean bleu) {
        byte advRoi = bleu
                ? ConfigurationCompacte.ROI_ROUGE
                : ConfigurationCompacte.ROI_BLEU;
        boolean advAlive = false;
        for (int y = 0; y < 5 && !advAlive; y++)
            for (int x = 0; x < 5; x++)
                if (s.getCase(x, y) == advRoi) {
                    advAlive = true; break;
                }

        byte ourRoi = bleu
                ? ConfigurationCompacte.ROI_BLEU
                : ConfigurationCompacte.ROI_ROUGE;
        boolean ourAlive = false;
        for (int y = 0; y < 5 && !ourAlive; y++)
            for (int x = 0; x < 5; x++)
                if (s.getCase(x, y) == ourRoi) {
                    ourAlive = true; break;
                }

        return ourAlive && !advAlive;
    }

    /**
     * Vérifie si le roi a atteint la case centrale du temple adverse.
     */
    private boolean arriveTemple(ConfigurationCompacte s, boolean bleu) {
        byte codeRoi = bleu
                ? ConfigurationCompacte.ROI_BLEU
                : ConfigurationCompacte.ROI_ROUGE;
        int templeY = bleu ? 0 : 4;
        return s.getCase(2, templeY) == codeRoi;
    }
}
