package Vue.Adaptateurs;

import Vue.CollecteurEvenements;
import java.awt.event.*;

public class Adaptateur_bouton_difficile_rouge implements ActionListener {
    private final CollecteurEvenements collecteur;
    public Adaptateur_bouton_difficile_rouge(CollecteurEvenements c) { this.collecteur = c; }
    @Override public void actionPerformed(ActionEvent e) {
        collecteur.clic_bouton_difficile_rouge();
    }
}



