package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Adaptateur_bouton_tutoriel implements ActionListener {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_bouton_tutoriel(CollecteurEvenements collecteur) {
        this.collecteur_evenements = collecteur;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur_evenements.clic_bouton_tutoriel();
    }

    public static class Adaptateur_bouton_son implements ActionListener {
        private final CollecteurEvenements collecteur_evenements;

        public Adaptateur_bouton_son(CollecteurEvenements collecteur_evenements) {
            this.collecteur_evenements = collecteur_evenements;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            collecteur_evenements.clic_bouton_son();
        }
    }
}