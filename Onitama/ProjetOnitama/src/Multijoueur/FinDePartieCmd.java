package Multijoueur;

import Modele.Couleur;
import java.io.Serializable;

public class FinDePartieCmd implements Command, Serializable {
    public final Couleur gagnant;

    public FinDePartieCmd(Couleur gagnant) {
        this.gagnant = gagnant;
    }

    @Override
    public String toString() {
        return "FinDePartieCmd[gagnant=" + gagnant + "]";
    }
}
