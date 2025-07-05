package Vue;

public interface VueJeu {
    void afficherMessage(String message, String titre);
    void afficherErreur(String message, String titre);
    String demanderTexte(String message, String titre);
    void afficherParametres();
    void allerA(String nomVue);
    void changerTheme(Boolean theme_clair);
    void changerLangue(Boolean est_francais);
    void desectionneCartes();
    void changerPanneaxSelection(String page);
    void reinitialiserCouleursBoutonsIAvsIA();
    PlateauGraphique getPlateauGraphique();
    void setEstClient(boolean estClient);
    PlateauReseau getPlateauReseau();
}
