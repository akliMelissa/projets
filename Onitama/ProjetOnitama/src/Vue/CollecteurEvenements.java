package Vue;

public interface CollecteurEvenements {

    // Clic sur une case du plateau
    void clic_sur_plateau(int ligne, int colonne);
    // clic sur une carte
    void selectionner_carte(int indice_carte);

    // Clic sur les boutons de l’écran d’accueil
    void clic_bouton_demarrer();
    void clic_bouton_tutoriel();
    void clic_bouton_continuer();

    // Boutons du plateau
    void clic_bouton_annuler();
    void clic_bouton_refaire();
    void clic_bouton_nouvelle_partie();
    void clic_bouton_sauvegarder();
    void clic_bouton_parametres();
    void clic_bouton_quitter();

    // mode de jouer page
    void clic_mode_ia_vs_humain();
    void clic_mode_jouer_a_distance();
    void clic_mode_ia_vs_ia();
    void clic_mode_humain_vs_humain();
    void clic_bouton_anglais();
    void clic_bouton_theme();
    void clic_bouton_son();
    void clic_bouton_Indice();
    void clic_bouton_facile();
    void clic_bouton_intermediaire();
    void clic_bouton_dificile();

    // Redimensionnement de la vue
    void redimensionnement(int largeur, int hauteur);

    // gestion de  IA vs IA
    void clic_bouton_facile_rouge();
    void clic_bouton_intermediaire_rouge();
    void clic_bouton_difficile_rouge();
    void clic_bouton_facile_bleu();
    void clic_bouton_intermediaire_bleu();
    void clic_bouton_difficile_bleu();

    // gestion mode reseaux
    void clic_bouton_se_connecter();
    void clic_bouton_heberger();
    void clic_bouton_rejoindre();

}


