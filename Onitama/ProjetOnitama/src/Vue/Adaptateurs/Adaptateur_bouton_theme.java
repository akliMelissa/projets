package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Adaptateur_bouton_theme implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_theme(CollecteurEvenements collecteur_evenements) {
        this.collecteur_evenements = collecteur_evenements;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_theme();
    }
}