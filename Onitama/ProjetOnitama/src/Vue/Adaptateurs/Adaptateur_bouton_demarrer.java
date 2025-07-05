package Vue.Adaptateurs;
import Vue.CollecteurEvenements;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


// Bouton Démarrer
public class Adaptateur_bouton_demarrer implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_demarrer(CollecteurEvenements collecteur) {
        this.collecteur_evenements = collecteur;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_demarrer();
    }
}





