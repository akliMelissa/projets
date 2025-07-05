package Vue.Adaptateurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vue.CollecteurEvenements;

/*** Relais du clic « Se connecter » vers le contrôleur.*/
public class Adaptateur_Plateau_SeConnecter implements ActionListener {

    private final CollecteurEvenements collecteur;

    public Adaptateur_Plateau_SeConnecter(CollecteurEvenements collecteur) {
        this.collecteur = collecteur;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        collecteur.clic_bouton_se_connecter();
    }
}
