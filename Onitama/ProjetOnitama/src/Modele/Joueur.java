package Modele;

import Global.UCC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Class permettant de définir un Joueur
 **/
public class Joueur implements Serializable {
    private final CarteJeu[] cartes; // liste des deux cartes de jouer
    private Couleur couleur_joueur;  // rouge ou blue ?

    /**
     * Constructeur Joueur
     * @param couleur La couleur du joueur {@link Couleur}.
     */
    public Joueur( Couleur couleur) {
        this.cartes = new CarteJeu[2];
        this.couleur_joueur = couleur;
    }

    /**
     * Permet d'obtenir la main du joueur
     * @return La main du joueur
     */
    public CarteJeu[] getCartes() {
        return cartes;
    }

    /**
     * Permet d'obtenir la position d'une carte dans la main du joueur.
     * @param carte La carte à trouver dans la main {@link CarteJeu}.
     * @return un entier 0 ou 1 si la carte est dans la main ou -1 si la carte n'est pas dans la main.
     */
    public int getpositionCarte(CarteJeu carte) {
        if(carte==null){
            return -1;
        } else if (cartes[0].equals(carte)) {
            return 0;
        } else if (cartes[1].equals(carte)) {
            return 1;
        }else {return -1;}
    }

    /**
     * Permet d'obtenir une carte de la main du joueur.
     * @param carte entier position de la carte dans la main du joueur.
     * @return Renvoie la carte obtenu {@link CarteJeu}.
     * @throws ArrayIndexOutOfBoundsException Si l'entier n'est pas 0 ou 1 alors, il renvoie null.
     */
    public CarteJeu getCarte(int carte) throws ArrayIndexOutOfBoundsException {
        try {
            return cartes[carte];
        }catch (ArrayIndexOutOfBoundsException e){
            UCC.erreur("Index out of bounds in Joueur->cartes");
            return null;
        }

    }

    /**
     * Permet d'obtenir la couleur du joueur.
     * @return couleur
     */
    @SuppressWarnings("unused")
    public Couleur getCouleur() {
        return couleur_joueur;
    }

    /**
     * Permet de placer une carte dans la main
     * @param carte prend une carte {@link CarteJeu}
     * @param position Une position dans la main.
     */
    public void setCarte(CarteJeu carte,int position) {
        try {
            cartes[position] = carte;
        }catch (ArrayIndexOutOfBoundsException e){
            UCC.erreur("Index out of bounds in Joueur->cartes");
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void Sauvegarder(DataOutputStream out) throws IOException {
        try {
            out.writeUTF(couleur_joueur.toString());
            cartes[0].Sauvegarder(out);
            cartes[1].Sauvegarder(out);
        }catch(IOException e){
            UCC.erreur("Erreur lors de la sauvegarde du joueur : " + e.getMessage());
        }
    }

    public void Charger(DataInputStream in) throws IOException {
        try {
            couleur_joueur=Couleur.toCouleur(in.readUTF());
            cartes[0]=new CarteJeu(5,5);
            cartes[0].Charger(in);
            cartes[1]=new CarteJeu(5,5);
            cartes[1].Charger(in);
        }catch (Exception e){
            UCC.erreur("Erreur lors de charger du joueur : " + e.getMessage());
        }
    }

    public void copyFrom(Joueur other) {
        this.cartes[0] = other.cartes[0];
        this.cartes[1] = other.cartes[1];
        // Ajouter d’autres champs si besoin
    }

}

