package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Adaptateur_bouton_facile implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_facile(CollecteurEvenements collecteur_evenements) {
        this.collecteur_evenements = collecteur_evenements;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_facile();
    }
}
