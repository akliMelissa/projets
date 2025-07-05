package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Adaptateur pour le bouton « Héberger ». */
public class Adaptateur_bouton_heberger implements ActionListener {
    private final CollecteurEvenements collecteur;

    public Adaptateur_bouton_heberger(CollecteurEvenements c) { this.collecteur = c; }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur.clic_bouton_heberger();
    }
}
