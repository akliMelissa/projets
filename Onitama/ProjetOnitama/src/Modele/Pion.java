package Modele;
import Global.UCC;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Cette classe gére le Pion (ROI / PION) sur le Plateau
 */
public class Pion implements Serializable {

    private Role role;
    private Couleur couleur;
    private Point position;

    /**
     * Le bool actif (True) si la Piece est sur le plateau, et (False) une fois capturé
     */
    private boolean actif;

    /* Constructeur */

    /**
     * Constructeur de pion
     * @param role le role joué par le pion (Roi ou Pion).
     * @param couleur la couleur du pion (ROUGE et BLEU).
     * @param position La position du pion sur le plateau.
     */
    public Pion(Role role, Couleur couleur, Point position) {
        this.role = role;
        this.couleur = couleur;
        this.position = position;
        this.actif = true;
    }

    public Pion(Pion other) {
        this.role = other.role;
        this.couleur = other.couleur;
        this.position = new Point(other.position);
        this.actif = other.actif;
    }

    /**
     * Getter du Role de la Piece.
     * @return Renvoie un Role (ROI ou PION) du pion.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Getter de la Couleur de la Piece.
     * @return Renvoie la couleur (ROUGE ou BLEU) du pion.
     */
    public Couleur getCouleur() {
        return couleur;
    }

    /*  */

    /**
     * Getter de la Position de la Piece.
     * @return Renvoie la position de la Pièce.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Indiqué si la Piece est actif ou pas
     * @return Renvoie si la pièce est sur le plateau (True) ou si la pièce a été mangé (False).
     */
    public boolean estActif() {
        return actif;
    }

    /**
     * Mise à jour d'actif a false si la piéce à ete capturé
     */
    public void capturer() {
        this.actif = false;
    }

    /**
     * Mise à jour d'actif a true.
     */
    public void setActif() {
        this.actif = true;
    }

    /**
     * Déplacer la piece vers une nouvelle position.
     * @param x la nouvelle position de la pièce sur l'accès des ordonnées.
     * @param y la nouvelle position de la pièce sur l'accès des abscisses.
     */
    public void deplacer(int x ,int y) {
        this.position.move(x,y);
    }

    /**
     * Calcule la distance entre la piece et une autre Position
     * fonction pour IA ??
     * @param autre prend une position à comparer avec la pièce
     * @return renvoie la distance entre le point choisie et la pièce
     */
    @SuppressWarnings("unused")
    public int calculerDistance(Point autre) {
        return (int) (Math.abs(position.getY() - autre.getY()) +
                Math.abs(position.getX() - autre.getX()));
    }

    public void Sauvegarde(DataOutputStream out)throws IOException {
        try{
            out.writeUTF(role.toString());
            out.writeUTF(couleur.toString());
            out.writeInt(position.x);
            out.writeInt(position.y);
            out.writeBoolean(actif);

        }catch (IOException e){
            UCC.erreur("Erreur lors de la sauvegarde du pion : " + e.getMessage());
        }
    }

    public void charger(DataInputStream in) throws IOException {
        try{
            role=Role.toRole(in.readUTF());
            couleur=Couleur.toCouleur(in.readUTF());
            position=new Point(in.readInt(),in.readInt());
            actif=in.readBoolean();
        } catch (Exception e) {
            UCC.erreur("Erreur lors du chargement du pion : " + e.getMessage());
        }
    }

    /**
     * Représentation textuelle de la pièce
     * @return Renvoie les informations de la pièce.
     */
    @Override
    public String toString() {
        return role + " " + couleur + " à " + position +
                (actif ? "" : " (capturé)");
    }
}
