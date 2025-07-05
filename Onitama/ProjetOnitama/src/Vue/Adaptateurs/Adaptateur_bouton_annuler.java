// Adaptateur pour le bouton "Annuler"
package Vue.Adaptateurs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import Vue.CollecteurEvenements;

public class Adaptateur_bouton_annuler implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_annuler(CollecteurEvenements collecteur_evenements) {
        this.collecteur_evenements = collecteur_evenements;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_annuler();
    }
}
