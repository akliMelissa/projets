package Vue.Adaptateurs;

import Vue.CollecteurEvenements;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Adaptateur_redimensionnement extends ComponentAdapter {
    private final CollecteurEvenements collecteur_evenements;

    public Adaptateur_redimensionnement(CollecteurEvenements collecteur) {
        this.collecteur_evenements = collecteur;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int largeur = e.getComponent().getWidth();
        int hauteur = e.getComponent().getHeight();
        collecteur_evenements.redimensionnement(largeur, hauteur);
    }
}
