package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Adaptateur_bouton_continuer implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_continuer(CollecteurEvenements collecteur) {
        this.collecteur_evenements = collecteur;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_continuer();
    }
}