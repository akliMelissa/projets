package Vue.Adaptateurs;

import Vue.CollecteurEvenements;
import java.awt.event.*;

// Boutons IA Rouge
public class Adaptateur_bouton_facile_rouge implements ActionListener {
    private final CollecteurEvenements collecteur;
    public Adaptateur_bouton_facile_rouge(CollecteurEvenements c) { this.collecteur = c; }
    @Override public void actionPerformed(ActionEvent e) {
        collecteur.clic_bouton_facile_rouge();
    }
}
