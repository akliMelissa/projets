package Multijoueur;

import java.io.Serializable;

public class FinDeConnexionCmd implements Command, Serializable {
    private final String raison;


    public FinDeConnexionCmd(String raison) {
        this.raison = raison;
    }

    @Override
    public String toString() {
        return "FinDeConnexionCmd[raison=" + raison + "]";
    }
}
