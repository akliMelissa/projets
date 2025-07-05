package Modele;


import Global.UCC;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

/**
 * Class permettant de définir l'historique
 * <P>
 *    Permet de retenir coup joué dans le jeu.
 * </P>
 */
public class Historique implements Serializable {
    private final Stack<Integer> coup_joue;
    private final Stack<Integer> coup_annuler;

    /**
     * Constructeur d'Historique.
     */
    public Historique(){
        this.coup_joue = new Stack<>();
        this.coup_annuler = new Stack<>();
    }

    /**
     * Methode pour ajouter un coup joué.
     * @param coup le coup joue est un entier avec 6 bit pour la position initiale et 6 bit pour la position final , 1bit pour la carte sélectionné et un bit pour le joueur qui joue.
     */
    public void coup_jouer(int coup){
        coup_joue.add(coup);
        coup_annuler.clear();
    }

    /**
     * Méthode pour savoir si on peut annuler
     * @return vraie si on peut annuler, faux sinon
     */
    public boolean peut_annuler(){
        return !coup_joue.isEmpty();
    }

    /**
     * Méthode pour savoir si on peut refaire un coup.
     * @return vraie si on peut refaire, faux sinon
     */
    public boolean peut_refaire(){
        return !coup_annuler.isEmpty();
    }

    /**
     * Pour obtenir le dernier coup annuler
     * @return un coup
     */
    @SuppressWarnings("unused")
    public int get_top_annuler(){
        return coup_annuler.peek();
    }

    /**
     * Pour obtenir le dernier coup jouer
     * @return un coup
     */
    @SuppressWarnings("unused")
    public int get_top_joue(){
        return coup_joue.peek();
    }

    /**
     * Permet d'annuler un coup
     * @return le dernier coup.
     */
    public int annuler_coup(){
        int value=coup_joue.pop();
        coup_annuler.push(value);
        return value;
    }

    /**
     * Permet de refaire un coup.
     * @return un coup qui a été refait.
     */
    public int refaire_coup(){
        int value=coup_annuler.pop();
        coup_joue.push(value);
        return value;
    }

    public void Sauvegarder(DataOutputStream out) throws IOException {
        try {
            int nb_joue = coup_joue.size();
            int nb_annuler = coup_annuler.size();
            out.writeInt(nb_joue);
            for (int i = 0; i < nb_joue; i++) {
                out.writeInt(coup_joue.get(i));
            }
            out.writeInt(nb_annuler);
            for (int i = 0; i < nb_annuler; i++) {
                out.writeInt(coup_annuler.get(i));
            }
        }catch (IOException e){
            UCC.erreur("Erreur lors de la sauvegarde de l'Historique : " + e.getMessage());
        }
    }

    public void charger(DataInputStream in) throws IOException {
        try {
            int nb_joue = in.readInt();
            for (int i = 0; i < nb_joue; i++) {
                coup_joue.add(in.readInt());
            }
            int nb_annuler = in.readInt();
            for (int i = 0; i < nb_annuler; i++) {
                coup_annuler.add(in.readInt());
            }
        }catch (IOException e){
            UCC.erreur("Erreur lors de charger du joueur : " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        String resultat;
        resultat="Pile coup_joue\n";
        for(int i=0; i<coup_joue.size(); i++){
            resultat+=coup_joue.get(i)+"\n";
        }
        resultat+="\nPile coup_annuler\n";
        for(int i=0; i<coup_annuler.size(); i++){
            resultat+=coup_annuler.get(i);
        }
        return resultat;
    }

    public void copyFrom(Historique other) {
        this.coup_joue.clear();
        this.coup_joue.addAll(other.coup_joue);
        this.coup_annuler.clear();
        this.coup_annuler.addAll(other.coup_annuler);
    }
}

