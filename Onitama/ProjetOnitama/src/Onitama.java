import Global.UCC;
import Modele.Jeu;
import Vue.*;

import Controleur.ControleurMediateur;

public class Onitama {

    final static String graphics = UCC.lisString("graphics");

    public static void main(String[] args) {

        Jeu jeu = new Jeu();
        switch (graphics) {
            case "textuelle":
                new affichagetextuelle(jeu);
                break;
            case "graphique":
                ControleurMediateur controleur = new ControleurMediateur(jeu);
                InterfaceGraphique.demarrer(controleur);
                break;
            default:
                UCC.erreur("Interface inconnue");
                break;
        }

    }
}
