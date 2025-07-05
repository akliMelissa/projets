package Modele;
import Global.UCC;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Cette classe gére les Cartes
 */
public class CarteJeu implements Serializable {

    private String nom_carte;
    private final boolean[][] grilleDeplacements;
    private Couleur couleur;

    public CarteJeu(int i,int j){
        this.nom_carte="";
        this.grilleDeplacements=new boolean[i][j];
        couleur=null;

    }

    /**
     * Crée une Carte du jeu
     */
    public CarteJeu(String nom_carte, boolean[][] grilleDeplacements, Couleur couleur) {
        this.nom_carte = nom_carte;
        this.grilleDeplacements = grilleDeplacements;
        this.couleur = couleur;
    }

    /**
     * Récupère le nom de la Carte
     * @return le nom de la carte
     */
    public String getNomCarte() {
        return nom_carte;
    }

    /**
     * Récupère le nom de la Grille des Déplacements
     * @return une matrice des déplacements possible de la carte
     */
    public boolean[][] getGrilleDeplacements() {
        return grilleDeplacements;
    }

    /*  */

    /**
     * Vérifie si la carte est de couleur Rouge ou Bleu
     * @return un boolean si la carte et de couleur rouge.
     */
    public boolean estCouleurRouge() {
        return couleur ==Couleur.ROUGE;
    }

    /**
     * Vérifie si le Déplacement (dx,dy) est autorisé
     * @param dx position sur l'axe des abscisses du plateau de jeu.
     * @param dy position sur l'axe des ordonnées du plateau de jeu.
     * @return renvoie vraie si le déplacement est un déplacement proposé par la carte.
     */
    public boolean deplacementPossible(int dx, int dy) {
        // Calculer le point central
        int centreX = grilleDeplacements.length / 2;
        int centreY = grilleDeplacements[0].length / 2;

        int x = centreX + dx;
        int y = centreY + dy;

        if (x < 0 || x >= grilleDeplacements.length ||
                y < 0 || y >= grilleDeplacements[0].length) {
            return false;
        }

        // Vérifier si le déplacement est autorisé
        return grilleDeplacements[y][x];
    }



    public void Sauvegarder(DataOutputStream out)throws IOException {
        try {
            out.writeUTF(nom_carte);
            out.writeUTF(couleur.toString());
            for (boolean[] grilleDeplacement : grilleDeplacements) {
                for (boolean b : grilleDeplacement) {
                    out.writeBoolean(b);
                }
            }
        }catch (IOException e){
            UCC.erreur("Erreur lors de la sauvegarde de la carteJeu : "+e.getMessage());
        }
    }

    public void Charger(DataInputStream in) throws IOException {
        try {

            nom_carte=in.readUTF();
            couleur=Couleur.toCouleur(in.readUTF());
            for(int i =0; i< grilleDeplacements.length; i++){
                for (int j=0; j<grilleDeplacements[i].length; j++){
                    grilleDeplacements[i][j]=in.readBoolean();
                }
            }

        }catch (Exception e){
            UCC.erreur("Erreur lors du chargement de la carteJeu : " + e.getMessage());

        }
    }

    /**
     * Représentation Visuelle de la Carte
     * @return renvoie la carte sous format d'une chaine de caractère.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carte: ").append(nom_carte);
        sb.append(" (").append(couleur.toString()).append(")\n");

        // Afficher la grille
        for (int i = 0; i < grilleDeplacements.length; i++) {
            for (int j = 0; j < grilleDeplacements[i].length; j++) {

                if (i == grilleDeplacements.length/2 && j == grilleDeplacements[0].length/2) {
                    sb.append("O ");
                } else {
                    sb.append(grilleDeplacements[i][j] ? "X " : "· ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public List<Point> getDeplacementsRelatifs() {
        List<Point> deplacements = new ArrayList<>();
        int hauteur = grilleDeplacements.length;
        int largeur = grilleDeplacements[0].length;
        int centreX = largeur  / 2;
        int centreY = hauteur / 2;

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (grilleDeplacements[y][x]) {
                    int dx = x - centreX;
                    int dy = y - centreY;
                    deplacements.add(new Point(dx, dy));
                }
            }
        }
        return deplacements;
    }

    /**
     * Permet d'obtenir les coups possible d'une carte.
     * @return coups
     */
    public List<int[]> getDeplacement(){
        List<int[]> coups = new ArrayList<>();
        for (int y =0; y<5; y++){
            for(int x=0; x<5; x++){
                if (grilleDeplacements[y][x]){
                    coups.add(new int[]{x-2,y-2});
                }
            }
        }
        return coups;
    }

}
