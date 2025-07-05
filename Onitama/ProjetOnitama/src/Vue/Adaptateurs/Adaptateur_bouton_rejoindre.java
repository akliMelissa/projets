package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/** Adaptateur pour le bouton « Rejoindre ». */
public class Adaptateur_bouton_rejoindre implements ActionListener {
    private final CollecteurEvenements collecteur;

    public Adaptateur_bouton_rejoindre(CollecteurEvenements c) { this.collecteur = c; }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur.clic_bouton_rejoindre();
    }
}