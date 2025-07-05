package IA;

import Modele.*;
import java.util.*;

public class IAAleatoire extends IA {
    private final boolean estBleu;
    private final Jeu jeu;
    private final Random rand = new Random();

    public IAAleatoire(Jeu jeu, boolean estBleu) {
        this.jeu = jeu;
        this.estBleu = estBleu;
    }

    @Override
    public int[] proposerCoup() {
        Joueur j = estBleu ? jeu.getJoueur2() : jeu.getJoueur1();
        List<int[]> coups = coups_possible(j, estBleu);
        List<int[]> coups_meilleurs=new ArrayList<>();

        if (coups.isEmpty()) {
            // Aucun coup possible → choisir une carte à swapper
            int n = rand.nextInt(2);
            return new int[]{-1, -1, -1, -1, n};
        } else {
            // Choisir un coup aléatoire
            List<Pion> pions= !estBleu ? jeu.getPion_Bleu() : jeu.getPion_Rouge();
            int[] temple=!estBleu ? new int[]{2,4} :new int[]{2,0};
            Pion Roi=estBleu ? jeu.getROIBleu() : jeu.getROIRouge();


            for (Pion p : pions){

                for (int[] coup :coups){

                    int x = (int) p.getPosition().getX();
                    int y = (int) p.getPosition().getY();
                    if (Roi.getPosition().getX()==x && Roi.getPosition().getY()==y &&(temple[0]==coup[2]) && (temple[1]==coup[3]))
                        return coup;

                    if ((p.getRole()==Role.ROI) && (x==coup[2]) && (y==coup[3])){
                        return coup;
                    }
                    if ((x==coup[2]) && (y==coup[3])){
                        coups_meilleurs.add(coup);
                    }
                }
            }
            if (coups_meilleurs.isEmpty()){
                return coups.get(rand.nextInt(coups.size()));

            }else{
                return coups_meilleurs.get(rand.nextInt(coups_meilleurs.size()));
            }
        }
    }

    public List<int[]> coups_possible(Joueur j, boolean estBleu) {
        List<int[]> coups = new ArrayList<>();
        List<Pion> pions = estBleu ? jeu.getPion_Bleu() : jeu.getPion_Rouge();
        Couleur couleur = estBleu ? Couleur.BLEU : Couleur.ROUGE;
        int coeff = estBleu ? 1 : -1;
        int bl=estBleu ? 2 : 0;

        CarteJeu carte1 = j.getCarte(0);
        CarteJeu carte2 = j.getCarte(1);

        for (Pion pion : pions) {
            if (!pion.estActif()) continue;

            int x = (int) pion.getPosition().getX();
            int y = (int) pion.getPosition().getY();

            for (int[] move : carte1.getDeplacement()) {
                int newX = x + move[0] * coeff;
                int newY = y + move[1] * coeff;
                if (jeu.estPositionValide(newX, newY, couleur)) {
                    coups.add(new int[]{x, y, newX, newY, 0+bl});
                }
            }

            for (int[] move : carte2.getDeplacement()) {
                int newX = x + move[0] * coeff;
                int newY = y + move[1] * coeff;
                if (jeu.estPositionValide(newX, newY, couleur)) {
                    coups.add(new int[]{x, y, newX, newY, 1+bl});
                }
            }
        }

        return coups;
    }
}
