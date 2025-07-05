package Vue.Adaptateurs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import Vue.CollecteurEvenements;

public class Adaptateur_choix_JouerADistance implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_choix_JouerADistance(CollecteurEvenements collecteur_evenements) {
        this.collecteur_evenements = collecteur_evenements;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_mode_jouer_a_distance();
    }
}
