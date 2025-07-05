package Controleur;

import Modele.*;
import IA.*;
import Vue.*;
import Multijoueur.*;


import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/*** Classe qui gère les interactions entre la vue et le modèle. */
public class ControleurMediateur implements Vue.CollecteurEvenements {

    private Jeu jeu;
    private VueJeu vue;
    private Scanner scan;

    // État du jeu
    private boolean Ajouer = true;
    private Point case_source = null;
    private int carte_selectionnee = -1;
    private CarteJeu carteJ;

    // Gestion de theme
    private boolean theme_clair = true;
    private boolean langue_francais = true;
    private boolean son_active = false;

    // Gestion IA
    private IA ia;
    private boolean IABool = false;
    private boolean estIAvsIA = false;
    private IA ia1 = null; // IA Rouge
    private IA ia2 = null; // IA Bleue
    private int niveauIA;
    private int indiceCarteIA = -1;

    // Gestion temps tours IA
    private javax.swing.Timer timerIAHumain = null;   // IA vs Humain
    private javax.swing.Timer timerIAvsIA  = null;    // IA vs IA

    //Son
    private final MusiqueFond musiqueFond = new MusiqueFond();

    //Pour la suppresion de le fichier sauvgarder apres la fin de partie
    String nomImageCharge = null;

    // reseaux
    private  BlockingQueue<Command> qLocale = new LinkedBlockingQueue<>();
    private BlockingQueue<Command> qSend   = new LinkedBlockingQueue<>();
    private boolean modeReseau;         // true = client/serveur démarré
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    boolean estServeur;
    private Socket socket;
    private Thread thTx, thRx, thMoteur;


    //constructeur
    public ControleurMediateur(Jeu j) {
        this.jeu = j;
        this.scan = new Scanner(System.in);
    }

    // ============= Gestion de Page Accueil =============

    @Override
    public void clic_bouton_demarrer() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        vue.allerA("mode_selection");
        vue.changerPanneaxSelection("EMPTY");
    }

    @Override
    public void clic_bouton_tutoriel() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        vue.allerA("tutoriel");
    }

    @Override
    public void clic_bouton_quitter() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        stopAllTimers();
        Ajouer = true;
        niveauIA = 0;
        jeu.setNiveauIARouge(0);
        jeu.setNiveauIABleu(0);
        vue.reinitialiserCouleursBoutonsIAvsIA();
        reinitialiserSelectionApresCoumain();
        reinitialiserToutesLesIAs();

        if (modeReseau) {
            try {
                if (getQueueSend() != null) {
                    getQueueSend().put(new DeconnexionCmd("L'autre joueur a quitté la partie."));
                }
                Thread.sleep(200);  //temps d'envoyer le message
            } catch (Exception ignored) {}
            arretConnexion();
        }

        vue.allerA("accueil");
    }


    @Override
    public void redimensionnement(int largeur, int hauteur) {}
    public void setVue(VueJeu v) {
        this.vue = v;
    }
    public Jeu getJeu() {
        return jeu;
    }


    // ============= GESTION DES PARAMETRES =============

    @Override
    public void clic_bouton_parametres() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        vue.afficherParametres();
    }

    @Override
    public void clic_bouton_theme() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        theme_clair = !theme_clair;
        vue.changerTheme(theme_clair);
        jeu.metAJour();
    }

    @Override
    public void clic_bouton_anglais() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        langue_francais = !langue_francais;
        vue.changerLangue(langue_francais);
        jeu.metAJour();
    }

    @Override
    public void clic_bouton_son() {

        son_active = !son_active;
        if (son_active) {
            musiqueFond.demarrer("Sons/fond.wav");
        } else {
            musiqueFond.arreter();
        }
    }


    // ============= Gestion sauvgarder et recharger  =============

    @Override
    public void clic_bouton_continuer() {
        if (son_active) musiqueFond.jouerEffet("Sons/click.wav");

        // recuperer les sauvegardes disponibles
        List<String> fichiers = listerFichiersSauvegarde();

        if (fichiers.isEmpty()) {
            String msg   = langue_francais ? "Aucune sauvegarde trouvée." : "No saved game found.";
            String titre = langue_francais ? "Information" : "Information";
            vue.afficherMessage(msg, titre);
            return;
        }

        // lister les pour l'utilisateur
        String titreDlg = langue_francais ? "Charger une partie" : "Load a game";
        String labelDlg = langue_francais ? "Choisissez la sauvegarde :" : "Choose a save file:";

        String choix = (String) JOptionPane.showInputDialog(
                null, labelDlg, titreDlg, JOptionPane.QUESTION_MESSAGE,
                null, fichiers.toArray(), fichiers.get(0));

        if (choix != null && !choix.isEmpty()) {
            chargerPartie(choix);
        }
    }
    

   //Retourne la liste des fichiers présents dans le dossier Savefile
    private List<String> listerFichiersSauvegarde() {

        List<String> fichiers = new ArrayList<>();
        File dossier = new File("Savefile");
        if (!dossier.exists() || !dossier.isDirectory()) {
            return fichiers;
        }

        File[] saves = dossier.listFiles(f -> f.isFile() && f.getName().endsWith(".sav"));
        if (saves != null) {
            for (File f : saves) {
                // pour enlever l’extension .sav
                String nomSansExt = f.getName().replaceFirst("\\.sav$", "");
                fichiers.add(nomSansExt);
            }
        }
        return fichiers;
    }


    @Override
    public void clic_bouton_sauvegarder() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        String demande = langue_francais ? "Entrez le nom du fichier pour la sauvegarde:" : "Enter the file name to save:";
        String titre = langue_francais ? "Sauvegarder la partie" : "Save the game";

        String nomFichier = vue.demanderTexte(demande, titre);
        if (nomFichier != null && !nomFichier.isEmpty()) {
            sauvegarderPartie(nomFichier);
        }
    }

    //Chargement d'une partie depuis le fichier choisi
    private void chargerPartie(String nomFichier) {
        String succes = langue_francais ? "Partie chargée avec succès depuis " : "Game successfully loaded from ";
        String ok = langue_francais ? "Chargement réussi" : "Load success";
        String echec = langue_francais ? "Échec du chargement de la partie." : "Failed to load the game.";
        String erreur = langue_francais ? "Erreur" : "Error";
        String erreurInattendue = langue_francais ? "Erreur inattendue : " : "Unexpected error: ";

        try {
            //Réinitialiser l'état
            jeu.nouvelle_partie();
            reinitialiserToutesLesIAs();
            stopAllTimers();
            jeu.metAJour();

            if (jeu.charger(nomFichier)) {
                nomImageCharge = nomFichier;
                vue.afficherMessage(succes + nomFichier, ok);
                vue.allerA("plateau");

                String mode = jeu.getModeJeu();

                if ("Joueur contre IA".equals(mode)) { // mode IA vs Humain
                    IABool = true;
                    niveauIA = getJeu().getNiveauIARouge();
                    ia = IA.creerIA(jeu, niveauIA, false);
                    jeu.metAJour();
                    debuterTourIAvsHumain();
                }
                else if ("IA contre IA".equals(mode)) { //mode IA vs IA
                    IABool = true;
                    estIAvsIA = true;

                    // creation des IAs
                    ia1 = IA.creerIA(jeu, getJeu().getNiveauIARouge(), false);  // IA Rouge
                    ia2 = IA.creerIA(jeu, getJeu().getNiveauIABleu(), true);    // IA Bleue

                    // relance
                    debuterTourIAvsIA();
                }
                else { // Mode Joueur vs Joueur
                    IABool = false;
                    estIAvsIA = false;
                }

            } else {
                vue.afficherErreur(echec, erreur);
            }
        } catch (Exception e) {
            vue.afficherErreur(erreurInattendue + e.getMessage(), erreur);
        }
    }


    // suppression des fichiers sauvgarder apres fin de partie
    private void supprimerSauvegardeChargee() {
        if (nomImageCharge != null) {
            File save = new File("Savefile" + File.separator + nomImageCharge + ".sav");
            if (save.exists()) {
                if (save.delete()) {
                    String msg = langue_francais
                            ? "Sauvegarde supprimée : " + nomImageCharge
                            : "Save deleted: " + nomImageCharge;
                    String titre = langue_francais ? "Suppression" : "Deletion";
                    vue.afficherMessage(msg, titre);
                } else {
                    String msg = langue_francais
                            ? "Échec de la suppression de la sauvegarde : " + nomImageCharge
                            : "Failed to delete save file: " + nomImageCharge;
                    String titre = langue_francais ? "Erreur" : "Error";
                    vue.afficherErreur(msg, titre);
                }
            }
            nomImageCharge = null;
        }
    }


    // Sauvegarde la partie dans un fichier
    private void sauvegarderPartie(String nomFichier) {
        String succes = langue_francais ? "Partie sauvegardée avec succès dans " : "Game saved successfully in ";
        String ok = langue_francais ? "Sauvegarde réussie" : "Save success";
        String echec = langue_francais ? "Échec de la sauvegarde" : "Save failed";
        String erreur = langue_francais ? "Erreur" : "Error";
        String impossible = langue_francais ? "Erreur : Impossible de créer le fichier - " : "Error: Cannot create file - ";
        String inattendue = langue_francais ? "Erreur inattendue : " : "Unexpected error: ";

        try {
            if (jeu.sauvegarder(nomFichier)) {
                vue.afficherMessage(succes + nomFichier, ok);
            } else {
                vue.afficherErreur(echec, erreur);
            }
        } catch (FileNotFoundException e) {
            vue.afficherErreur(impossible + e.getMessage(), erreur);
        } catch (Exception e) {
            vue.afficherErreur(inattendue + e.getMessage(), erreur);
        }
    }


    // ============= Gestion des actions sur Plateaux =============

    @Override
    public void clic_bouton_annuler() {
        jeu.annuler();
        jeu.metAJour();
    }

    @Override
    public void clic_bouton_refaire() {
        jeu.refaire();
        jeu.metAJour();
    }

    @Override
    public void clic_bouton_nouvelle_partie() {
        reinitialiserPartie();
        if ("Joueur contre IA".equals(jeu.getModeJeu())) {
            demarrerPartieIAvsHumain();
        }
        else if("IA contre IA".equals(jeu.getModeJeu())){
            demarrerPartieIAvsIA();
        }
    }


    @Override
    public void clic_bouton_Indice() {
        boolean estBleu = jeu.getjoueurCourant();
        IA iaHint = IA.creerIA(jeu, 3, estBleu);

        // coup propose par l'IA
        int[] coup = iaHint.proposerCoup();

        // Pas de coup valide trouvé
        if (coup[2] < 0 || coup[3] < 0) {
            String titre = langue_francais ? "Indice" : "Hint";
            String msg = langue_francais
                    ? "Aucun coup gangant pour le moment, choisir aleatoirement."
                    : "No winning moves available at the moment, choose randomly.";
            vue.afficherMessage(msg, titre);
            return;
        }

        //slectionne la carte choisi
        int idxCarte = (coup[4] <2)? coup[4] : coup[4]-2;
        vue.getPlateauGraphique().setCarteSelectionnee(idxCarte);
        selectionner_carte(idxCarte);
        case_source = new Point(coup[0] , coup[1]);

        // Réinitialiser la liste des cases jouables
        jeu.init_cases_jouables_tour_c1();
        jeu.init_cases_jouables_tour_c2();

        Point caseIndice = new Point(coup[2], coup[3]);

        if(modeReseau){
            boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) || (!estServeur &&
                    jeu.getjoueurCourant());
            if(estMonTour){
                if (idxCarte==0){
                    jeu.get_cases_jouables_tour_c1().add(caseIndice);
                }else{
                    jeu.get_cases_jouables_tour_c2().add(caseIndice);
                }
            }
        }else{
            if (idxCarte==0){
                jeu.get_cases_jouables_tour_c1().add(caseIndice);
            }else{
                jeu.get_cases_jouables_tour_c2().add(caseIndice);
            }
        }

        // effet de selection de la pièce source dans la vue
        vue.getPlateauGraphique().setPieceSelectionnee(new Point(coup[0], coup[1]));

        jeu.metAJour();
        if (son_active) {
            musiqueFond.jouerEffet("Sons/indice.wav");
        }
    }



    //init de la partie
    private void reinitialiserPartie() {
        jeu.nouvelle_partie();
        case_source = null;
        carte_selectionnee = -1;
        carteJ = null;
        jeu.init_cases_jouables_tour_c1();
        jeu.init_cases_jouables_tour_c2();

        jeu.set_est_jouable(false);
        vue.desectionneCartes();
        jeu.metAJour();
    }


    // ============= Gestion de selection de mode de jeu =============

    @Override
    public void clic_mode_humain_vs_humain() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        jeu.setModeJeu("Joueur contre Joueur");
        reinitialiserToutesLesIAs();
        jeu.nouvelle_partie();
        vue.allerA("plateau");
        jeu.metAJour();
    }

    @Override
    public void clic_mode_ia_vs_humain() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        jeu.setModeJeu("Joueur contre IA");
        reinitialiserModeIAvsIA();
        vue.changerPanneaxSelection("Joueur contre IA");
        jeu.nouvelle_partie();
        jeu.metAJour();
    }

    @Override
    public void clic_mode_ia_vs_ia() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        jeu.setModeJeu("IA contre IA");
        reinitialiserModeIAvsHumain();
        estIAvsIA = true;
        vue.changerPanneaxSelection("IA contre IA");
        jeu.metAJour();
    }

    @Override
    public void clic_mode_jouer_a_distance() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        jeu.setModeJeu("ModeReseau");
        vue.changerPanneaxSelection("ModeReseau");
    }



    // ============= Gestion de selection de niveaux IA =============

    // IA vs Humain
    @Override
    public void clic_bouton_facile() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        niveauIA = 1;
        jeu.setNiveauIARouge(1);
        demarrerPartieIAvsHumain();
    }

    @Override
    public void clic_bouton_intermediaire() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        niveauIA = 2;
        jeu.setNiveauIARouge(2);
        demarrerPartieIAvsHumain();
    }

    @Override
    public void clic_bouton_dificile() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        niveauIA = 3;
        jeu.setNiveauIARouge(3);
        demarrerPartieIAvsHumain();
    }

    // IA Rouge (IA vs IA)
    @Override
    public void clic_bouton_facile_rouge() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        if (estIAvsIA) {
            jeu.setNiveauIARouge(1);
            ia1 = new IAAleatoire(jeu, false); // false = IA rouge
            verifierDemarrerPartieIAvsIA();
        }
    }

    @Override
    public void clic_bouton_intermediaire_rouge() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        if (estIAvsIA) {
            jeu.setNiveauIARouge(2);
            ia1 = IA.creerIA(jeu, 2, false);
            verifierDemarrerPartieIAvsIA();
        }
    }

    @Override
    public void clic_bouton_difficile_rouge() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        if (estIAvsIA) {
            jeu.setNiveauIARouge(3);
            ia1 = IA.creerIA(jeu, 3, false);
            verifierDemarrerPartieIAvsIA();
        }
    }

    // IA Bleue (IA vs IA)
    @Override
    public void clic_bouton_facile_bleu() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        if (estIAvsIA) {
            jeu.setNiveauIABleu(1);
            ia2 = new IAAleatoire(jeu, true); // true = IA bleue
            verifierDemarrerPartieIAvsIA();
        }
    }

    @Override
    public void clic_bouton_intermediaire_bleu() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        if (estIAvsIA) {
            jeu.setNiveauIABleu(2);
            ia2 = IA.creerIA(jeu, 2, true);
            verifierDemarrerPartieIAvsIA();
        }
    }


    @Override
    public void clic_bouton_difficile_bleu() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }

        if (estIAvsIA) {
            jeu.setNiveauIABleu(3);
            ia2 = IA.creerIA(jeu, 3, true);
            verifierDemarrerPartieIAvsIA();
        }
    }

    private void verifierDemarrerPartieIAvsIA() {
        if (ia1 != null && ia2 != null) {
            vue.reinitialiserCouleursBoutonsIAvsIA();
            demarrerPartieIAvsIA();
        }
    }


    // ============= interaction plateaux =============

    @Override
    public void selectionner_carte(int index) {
        carte_selectionnee = index;
        carteJ = jeu.getjoueurCourant()
                ? jeu.getJoueur2().getCarte(index)
                : jeu.getJoueur1().getCarte(index);

        jeu.init_cases_jouables_tour_c1();
        jeu.init_cases_jouables_tour_c2();

        if (case_source != null && carteJ != null) {
            jeu.trouverCasesJouables(carteJ, case_source);
        }

        jeu.metAJour();
    }


    @Override
    public void clic_sur_plateau(int colonne, int ligne) {
        if (!jeu.est_dans_terrain(ligne, colonne)) return;

        Pion[][] plateau = jeu.getPlateau();
        Pion cible = plateau[ligne][colonne];
        Couleur couleurJoueur = jeu.getjoueurCourant() ? Couleur.BLEU : Couleur.ROUGE;

        // Si on a déjà une source ET une carte sélectionnée, tenter de jouer
        if (case_source != null && carte_selectionnee >= 0 && peutJouerCoup(colonne, ligne)) {
            if (!modeReseau) {
                jouerCoupHumain(colonne, ligne);
            } else {
                boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) ||
                        (!estServeur && jeu.getjoueurCourant());
                if (!estMonTour) {
                    SwingUtilities.invokeLater(() -> {
                        vue.afficherErreur("Ce n'est pas votre tour !", "Tour de jeu");
                    });
                    return;
                }

                int idx = this.getCarteSelectionnee();

                Joueur joueurCourant = jeu.getjoueurCourant()
                        ? jeu.getJoueur2()
                        : jeu.getJoueur1();

                CarteJeu carteChoisie = joueurCourant.getCarte(idx);
                byte ci = jeu.indexCarte(carteChoisie);
                byte joueurActuel  = (byte) (estServeur ? 0 : 1);

                MoveCmd cmd = new MoveCmd(
                        ci,
                        (byte) case_source.x, (byte) case_source.y,
                        (byte) colonne,       (byte) ligne,
                        joueurActuel
                );
                try {
                    // Envoi vers l’autre machine
                    getQueueSend().put(cmd);
                    getQueueLocale().put(cmd);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return;
        }

        // Si on a une source mais pas de carte sélectionnée
        if (case_source != null && carte_selectionnee < 0) {

            // si on clique sur un pion du joueur courant changer la source
            if (cible != null && cible.getCouleur() == couleurJoueur) {
                boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) ||
                        (!estServeur && jeu.getjoueurCourant());
                selectionnerPionSource(colonne, ligne, cible, couleurJoueur , estMonTour);
                return;
            }

            // si ce coup est possible avec au moins une carte
            if (jeu.get_cases_jouables_tour_c1().contains(new Point(colonne, ligne)) &&
                    jeu.get_cases_jouables_tour_c2().contains(new Point(colonne, ligne)) ) {
                String message = langue_francais
                        ? "Deux cartes permettent ce déplacement. Veuillez choisir celle que vous souhaitez utiliser."
                        : "Two cards allow this move. Please choose the one you want to use.";

                String titre = langue_francais
                        ? "Choix de carte"
                        : "Choose Your Card";

                vue.afficherMessage(message, titre);
            } else if (jeu.get_cases_jouables_tour_c1().contains(new Point(colonne, ligne))) {
                selectionner_carte(0);
                vue.getPlateauGraphique().setCarteSelectionnee(carte_selectionnee);

                // si on peut jouer
                if (peutJouerCoup(colonne, ligne)) {
                    if (!modeReseau) {
                        jouerCoupHumain(colonne, ligne);
                    } else {
                        boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) ||
                                (!estServeur && jeu.getjoueurCourant());
                        if (!estMonTour) {
                            SwingUtilities.invokeLater(() -> {
                                vue.afficherErreur("Ce n'est pas votre tour !", "Tour de jeu");
                            });
                            return;
                        }
                        Joueur joueurCourant = jeu.getjoueurCourant()
                                ? jeu.getJoueur2()
                                : jeu.getJoueur1();
                        CarteJeu carteChoisie = joueurCourant.getCarte(0);
                        byte ci = jeu.indexCarte(carteChoisie);
                        byte joueurActuel  = (byte) (estServeur ? 0 : 1);

                        MoveCmd cmd = new MoveCmd(ci,
                                (byte) case_source.x, (byte) case_source.y,
                                (byte) colonne, (byte) ligne,
                                joueurActuel
                        );
                        try {
                            getQueueSend().put(cmd);
                            getQueueLocale().put(cmd);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } else if (jeu.get_cases_jouables_tour_c2().contains(new Point(colonne, ligne)) ) {
                selectionner_carte(1);
                vue.getPlateauGraphique().setCarteSelectionnee(carte_selectionnee);

                if (peutJouerCoup(colonne, ligne)) {
                    if (!modeReseau) {
                        jouerCoupHumain(colonne, ligne);
                    } else {
                        boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) ||
                                (!estServeur && jeu.getjoueurCourant());
                        if (!estMonTour) {
                            SwingUtilities.invokeLater(() -> {
                                vue.afficherErreur("Ce n'est pas votre tour !", "Tour de jeu");
                            });
                            return;
                        }
                        Joueur joueurCourant = jeu.getjoueurCourant()
                                ? jeu.getJoueur2()
                                : jeu.getJoueur1();
                        CarteJeu carteChoisie = joueurCourant.getCarte(1);
                        byte ci = jeu.indexCarte(carteChoisie);
                        byte joueurActuel  = (byte) (estServeur ? 0 : 1);

                        MoveCmd cmd = new MoveCmd( ci,
                                (byte) case_source.x, (byte) case_source.y,
                                (byte) colonne,       (byte) ligne,
                                joueurActuel
                        );
                        try {
                            getQueueSend().put(cmd);
                            getQueueLocale().put(cmd);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            return;
        }

        // Sinon, sélectionner un nouveau pion source
        boolean estMonTour = (estServeur && !jeu.getjoueurCourant()) ||
                (!estServeur && jeu.getjoueurCourant());
        selectionnerPionSource(colonne, ligne, cible, couleurJoueur , estMonTour);
    }

    //Sélectionne un pion source
    private void selectionnerPionSource(int colonne, int ligne, Pion cible,
                                        Couleur couleurJoueur , Boolean estMonTour) {

        // si c'est bien un pion du joueur actuel
        if (cible != null && cible.getCouleur() == couleurJoueur ) {
            if(!estMonTour && modeReseau){
                return;
            }
            case_source = new Point(colonne, ligne);

            // trouver les coups possibles
            if (carte_selectionnee >= 0 && carteJ != null) {
                jeu.trouverCasesJouables(carteJ, case_source);
            } else {
                //calculer avec les 2 cartes
                carte_selectionnee = -1;
                carteJ = null;
                jeu.trouverToutesCasesJouables(case_source);
            }
            jeu.metAJour();
        }
    }


    //si on peut jouer un coup à cette position
    private boolean peutJouerCoup(int colonne, int ligne) {
        return case_source != null && carte_selectionnee > -1 &&
                (jeu.get_cases_jouables_tour_c1().contains(new Point(colonne, ligne)) ||
                        jeu.get_cases_jouables_tour_c2().contains(new Point(colonne, ligne)));
    }

    /*** Joue un coup pour un joueur humain */
    private void jouerCoupHumain(int colonne, int ligne) {

        jeu.set_est_jouable(true);
        if (vue != null && vue.getPlateauGraphique() != null) {
            vue.getPlateauGraphique().animerDeplacement(
                    new Point(case_source.x, case_source.y),   // départ
                    new Point(colonne, ligne)   // arrivée
            );
        }
        Ajouer = jeu.jouerCoup(case_source.x, case_source.y, colonne, ligne, carteJ);

        if (Ajouer) {

            if (vue != null && vue.getPlateauGraphique() != null) {
                int indexCarteSelectionnee = vue.getPlateauGraphique().getcarteSelectionnee();
                int indexCarteCentre = 4; // carte du milieu
                vue.getPlateauGraphique().afficherSwapCartes(indexCarteSelectionnee, indexCarteCentre);
            }
            if (son_active) {
                musiqueFond.jouerEffet("Sons/coup.wav");
            }
            
            jeu.metAJour();

            if (jeu.estTermine()) {
                gererFinDePartieHumain();
            } else {
                reinitialiserSelectionApresCoumain();
                // lancer le tour d'IA
                if (IABool) {
                    programmerProchainTourIAvsHumain();
                }
            }

        } else {
            System.out.println("Erreur impossible de jouer ce coup.");
        }
    }


    //la fin de partie pour un joueur humain
    private void gererFinDePartieHumain() {
        if (son_active) {
            musiqueFond.jouerEffet("Sons/fin.wav");
        }

        supprimerSauvegardeChargee();

        Couleur gagnant = jeu.getGagnant();
        String joueurGagnant = (gagnant == Couleur.ROUGE)
                ? (langue_francais ? "Rouge (Joueur 1)" : "Red (Player 1)")
                : (langue_francais ? "Bleu (Joueur 2)" : "Blue (Player 2)");

        String message = langue_francais
                ? "Partie terminée ! Le joueur " + joueurGagnant + " a gagné !"
                : "Game over! Player " + joueurGagnant + " has won!";
        String titre = langue_francais ? "Fin de partie" : "Game over";

        reinitialiserSelectionApresCoumain();
        IABool = false;

        vue.afficherMessage(message, titre);
        clic_bouton_nouvelle_partie();
    }

    // init apres un coup
    private void reinitialiserSelectionApresCoumain() {
        case_source = null;
        carte_selectionnee = -1;
        carteJ = null;
        jeu.init_cases_jouables_tour_c1();
        jeu.init_cases_jouables_tour_c2();
        jeu.set_est_jouable(false);
        vue.desectionneCartes();
        jeu.metAJour();
    }


    // ============= Gestion parties IA =============

    //nouvelle partie IA vs IA
    private void demarrerPartieIAvsIA() {
        jeu.nouvelle_partie();
        vue.allerA("plateau");
        jeu.metAJour();
        IABool = true;
        debuterTourIAvsIA();
    }

    //nouvelle partie IA vs Humain
    private void demarrerPartieIAvsHumain() {
        jeu.nouvelle_partie();
        vue.allerA("plateau");
        jeu.metAJour();
        IABool = true;
        ia = IA.creerIA(jeu, niveauIA , false);
        debuterTourIAvsHumain();
    }

    //Termine une partie IA vs IA
    private void terminerPartieIAvsIA() {
        supprimerSauvegardeChargee();
        Couleur gagnant = jeu.getGagnant();

        String joueurGagnant = (gagnant == Couleur.ROUGE)
                ? (langue_francais ? "IA Rouge" : "Red AI")
                : (langue_francais ? "IA Bleue" : "Blue AI");

        String message = langue_francais
                ? "Partie terminée ! " + joueurGagnant + " a gagné !"
                : "Game over! " + joueurGagnant + " has won!";
        String titre = langue_francais ? "Fin de partie" : "Game over";

        if (son_active) {
            musiqueFond.jouerEffet("Sons/fin.wav");
        }
        vue.afficherMessage(message, titre);
        clic_bouton_nouvelle_partie();
        jeu.metAJour();
    }

    //Termine une partie IA vs Humain
    private void terminerPartieIAvsHumain() {
        supprimerSauvegardeChargee();
        Couleur gagnant = jeu.getGagnant();

        String joueurGagnant = (gagnant == Couleur.ROUGE)
                ? (langue_francais ? "Rouge (Joueur 1)" : "Red (Player 1)")
                : (langue_francais ? "Bleu (Joueur 2)" : "Blue (Player 2)");

        String message = langue_francais
                ? "Partie terminée ! Le joueur " + joueurGagnant + " a gagné !"
                : "Game over! Player " + joueurGagnant + " has won!";
        String titre = langue_francais ? "Fin de partie" : "Game over";

        if (son_active) {
            musiqueFond.jouerEffet("Sons/fin.wav");
        }
        vue.afficherMessage(message, titre);

        clic_bouton_nouvelle_partie();
        jeu.metAJour();
    }

    //premier tour si c'est l'IA qui commence (IA vs Humain)
    public void debuterTourIAvsHumain() {
        // tour de l'IA (rouge = false)
        if (!jeu.getjoueurCourant()) {
            afficherMessageDebutIA();

            new javax.swing.Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                jouerTourIAvsHumain();
            }).start();
        }
    }

    private void debuterTourIAvsIA() {
        new javax.swing.Timer(1000, e -> {
            ((Timer)e.getSource()).stop();
            jouerTourIAvsIA();
        }).start();
    }

    /*** Affiche un message indiquant que l'IA débute*/
    private void afficherMessageDebutIA() {
        String titre = langue_francais ? "Information" : "Information";
        String msg = langue_francais
                ? "L'IA débute le jeu !"
                : "The AI is starting the game!";
        vue.afficherMessage(msg, titre);
    }

    /*** Gère un tour dans le mode IA vs IA*/
    private void jouerTourIAvsIA() {
        if (jeu.estTermine()) {
            terminerPartieIAvsIA();
            return;
        }

        // Déterminer quelle IA doit jouer
        IA iaActuelle = jeu.getjoueurCourant() ? ia2 : ia1; // true = bleu (ia2), false = rouge (ia1)
        Joueur joueurActuel = jeu.getjoueurCourant() ? jeu.getJoueur2() : jeu.getJoueur1();

        System.out.println("IA " + (jeu.getjoueurCourant() ? "bleue" : "rouge") + " joue");

        // Exécuter le coup de l'IA
        executerCoupIA(iaActuelle, joueurActuel);

        // Programmer le prochain tour
        programmerProchainTourIAvsIA();
    }

    /**
     * Gère un tour de l'IA dans le mode IA vs Humain
     */
    private void jouerTourIAvsHumain(){
        // Vérifications de sécurité
        if (IABool && !jeu.getjoueurCourant() && !jeu.estTermine()){
            System.out.println("IA joue (mode vs Humain)");
            Joueur joueurRouge = jeu.getJoueur1();

            // Exécuter le coup de l'IA
            executerCoupIA(ia, joueurRouge);

            // Vérifier fin de partie
            if (jeu.estTermine()){
                terminerPartieIAvsHumain();
            }
        }
    }


    /*** Exécute un coup pour une IA donnée*/
    private void executerCoupIA(IA ia, Joueur joueur) {

        boolean peutJouer = jeu.tour_est_jouable();

        // Obtenir le coup proposé par l'IA
        int[] coup = ia.proposerCoup();
        System.out.println("Coup IA : " + Arrays.toString(coup));

        // Extraire la carte du coup
        indiceCarteIA = coup[4]; // pour la vue
        int idxCarte = (coup[4] <2)? coup[4] : coup[4]-2;
        CarteJeu carte = joueur.getCarte(idxCarte);

        // Jouer le coup
        if (peutJouer) {
            vue.getPlateauGraphique().animerDeplacement(
                    new Point(coup[0], coup[1]),
                    new Point(coup[2], coup[3])
            );

            if (vue != null && vue.getPlateauGraphique() != null && indiceCarteIA!=-1) {
                int indexCarteCentre = 4; // carte du milieu
                vue.getPlateauGraphique().afficherSwapCartes(indiceCarteIA, indexCarteCentre);
            }

            jeu.jouerCoup(coup[0], coup[1], coup[2], coup[3], carte);

            if (son_active) {
                musiqueFond.jouerEffet("Sons/coup.wav");
            }

        } else {
            jeu.swapCartes(carte);
            if (vue != null && vue.getPlateauGraphique() != null && indiceCarteIA!=-1) {
                int indexCarteCentre = 4; // carte du milieu
                vue.getPlateauGraphique().afficherSwapCartes(indiceCarteIA, indexCarteCentre);
            }
        }

    }

    /**
     * Programme le prochain tour IA vs IA avec un délai
     */
    private void programmerProchainTourIAvsIA() {
        if (timerIAvsIA != null && timerIAvsIA.isRunning())
            timerIAvsIA.stop();

        timerIAvsIA = new javax.swing.Timer(4000, e -> {
            timerIAvsIA.stop();
            jouerTourIAvsIA();
        });
        timerIAvsIA.setRepeats(false);
        timerIAvsIA.start();
    }

    /**
     * Programme le prochain tour IA vs Humain avec un délai
     */
    private void programmerProchainTourIAvsHumain() {
        if (timerIAHumain != null && timerIAHumain.isRunning())
            timerIAHumain.stop();

        timerIAHumain = new javax.swing.Timer(4000, e -> {
            timerIAHumain.stop();
            jouerTourIAvsHumain();
        });
        timerIAHumain.setRepeats(false);
        timerIAHumain.start();
    }

    private void stopAllTimers() {
        if (timerIAHumain != null) {
            timerIAHumain.stop();
            timerIAHumain = null;
        }
        if (timerIAvsIA != null) {
            timerIAvsIA.stop();
            timerIAvsIA = null;
        }
    }


    /**
     * init le mode IA vs IA
     */
    private void reinitialiserModeIAvsIA() {
        estIAvsIA = false;
        ia1 = null;
        ia2 = null;
        IABool = false;
    }

    /**
     * init le mode IA vs Humain
     */
    private void reinitialiserModeIAvsHumain() {
        IABool = false;
        ia = null;
    }

    private void reinitialiserToutesLesIAs() {
        reinitialiserModeIAvsIA();
        reinitialiserModeIAvsHumain();
    }


    @Override
    public void clic_bouton_heberger(){
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        vue.setEstClient(false);
        vue.changerPanneaxSelection("Reseau");
        connexionServeur(7000);
    }

    @Override
    public void clic_bouton_rejoindre(){
        if (son_active) {
            musiqueFond.jouerEffet("Sons/click.wav");
        }
        vue.setEstClient(true);
        vue.changerPanneaxSelection("Reseau");

    }

    @Override
    public void clic_bouton_se_connecter() {

        // la vue en mode client
        vue.setEstClient(true);

        //panneau réseau
        vue.changerPanneaxSelection("Reseau");

        //message de connexion
        PlateauReseau p = vue.getPlateauReseau();
        p.afficherMessage("Connexion…");

        // récupérer ip/port
        String ipStr   = p.getIpSaisie().trim();
        String portStr = p.getPortSaisi().trim();
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException ex) {
            p.afficherMessage("Port invalide !");
            return;
        }

        // lancer la connexion dans un thread
        connexionClient(ipStr, port);
    }


    public void arretConnexion() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}

        if (thTx != null) thTx.interrupt();
        if (thRx != null) thRx.interrupt();
        if (thMoteur != null) thMoteur.interrupt();

        modeReseau = false;

        reinitialiserSelectionApresCoumain();

        System.out.println("Connexion réseau fermée");
    }


    public void connexionClient(String ip, int port) {
        this.estServeur = false;
        new Thread(() -> {
            try {
                this.socket = new Socket(ip, port);
                demarrerMultijoueur(false);

                SwingUtilities.invokeLater(() -> {
                    vue.allerA("plateau");
                    modeReseau = true;
                    vue.getPlateauReseau().afficherMessage("Connecté à " + ip + ":" + port);

                    String premierJoueur = jeu.getjoueurCourant() ? "BLEU" : "ROUGE";
                    String votreRole = "Vous êtes BLEU (client)";
                    String message = String.format(
                            "Premier joueur : %s\n%s\n%s",
                            premierJoueur,
                            votreRole,
                            jeu.getjoueurCourant() ? "C'est à vous de jouer !" : "Attendez votre tour"
                    );
                    vue.afficherMessage(message, "Jeu réseau");
                });
            } catch (IOException | ClassNotFoundException e) {
                SwingUtilities.invokeLater(() ->
                        vue.afficherErreur("Échec connexion : " + e.getMessage(), "Réseau")
                );
            }
        }, "Connexion-Client").start();
    }


    public void connexionServeur(int port) {
        this.estServeur = true;
        SwingUtilities.invokeLater(() -> {
            vue.getPlateauReseau().afficherMessage("En attente de connexion sur port " + port + "...");
        });
        new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(port);
                this.socket = ss.accept();
                ss.close();

                demarrerMultijoueur(true);

                SwingUtilities.invokeLater(() -> {
                    vue.allerA("plateau");
                    modeReseau = true;
                    vue.getPlateauReseau().afficherMessage("Client connecté !");
                    String premierJoueur = jeu.getjoueurCourant() ? "BLEU" : "ROUGE";
                    String votreRole = "Vous êtes ROUGE (serveur)";
                    String message = String.format(
                            "Premier joueur : %s\n%s\n%s",
                            premierJoueur,
                            votreRole,
                            jeu.getjoueurCourant() ? "Attendez votre tour" : "C'est à vous de jouer !"
                    );
                    vue.afficherMessage(message, "Jeu réseau");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        vue.afficherErreur("Échec hébergement : " + ex.getMessage(), "Réseau")
                );
            }
        }, "Connexion-Serveur").start();
    }

    public void demarrerMultijoueur(boolean serveur) throws IOException, ClassNotFoundException {
        this.estServeur = serveur;
        jeu.set_estRougeReseaux(serveur);

        if (serveur) {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            this.jeu.nouvelle_partie();
            oos.writeObject(this.jeu);
            oos.flush();
            String confirmation = (String) ois.readObject();
            if (!"SYNC_OK".equals(confirmation)) {
                throw new IOException("Échec synchronisation initiale");
            }
            SwingUtilities.invokeLater(() -> {
                if (vue != null && vue.getPlateauGraphique() != null) {
                    vue.getPlateauGraphique().repaint();
                    jeu.metAJour();
                }
            });
        } else {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            Jeu etat = (Jeu) ois.readObject();
            this.jeu.copyFrom(etat);
            oos.writeObject("SYNC_OK");
            oos.flush();
            SwingUtilities.invokeLater(() -> {
                if (vue != null && vue.getPlateauGraphique() != null) {
                    vue.getPlateauGraphique().repaint();
                    jeu.metAJour();
                }
            });
        }
        startThreads(oos, ois);
        initMoteur();
    }

    private void startThreads(ObjectOutputStream oos, ObjectInputStream ois) {
        thTx = new Thread(new Emetteur(qSend, oos), "TX");
        thRx = new Thread(new Recepteur(qLocale, ois), "RX");
        thTx.start();
        thRx.start();
        modeReseau = true;
    }

    private void gererFinDePartieReseau() {
        Couleur gagnant  = jeu.getGagnant();
        boolean jaiGagne = (estServeur && gagnant == Couleur.ROUGE) ||
                (!estServeur && gagnant == Couleur.BLEU);

        String message = jaiGagne
                ? "🎉 FÉLICITATIONS ! 🎉\nVous avez GAGNÉ !"
                : "😞 DOMMAGE ! 😞\nVous avez PERDU !";
        String titre   = jaiGagne ? "Victoire !" : "Défaite !";

        JOptionPane.showMessageDialog(
                null,
                message,
                titre,
                JOptionPane.INFORMATION_MESSAGE
        );

        //prévient l’autre joueur
        try { getQueueSend().put(new FinDeConnexionCmd("Fin de partie")); }
        catch (InterruptedException ignored) { }

        //revient à l’écran d’accueil
        arretConnexion();
        vue.allerA("accueil");
    }


    public BlockingQueue<Command> getQueueSend()   { return qSend; }

    /** Lance le thread « moteur » qui applique les commandes et tient la vue à jour. */
    private void initMoteur() {

        thMoteur = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    //prochaine commande
                    Command cmd = qLocale.take();
                    if (cmd instanceof MoveCmd) {
                        MoveCmd m = (MoveCmd) cmd;

                        boolean ok = jeu.apply(m);

                       // vue
                        SwingUtilities.invokeLater(() -> {
                            reinitialiserSelectionApresCoumain();
                            if (vue != null && vue.getPlateauGraphique() != null) {
                                vue.getPlateauGraphique().repaint();
                            }
                            jeu.metAJour();

                            //Fin de partie
                            if (jeu.estTermine()) {
                                if (estServeur) {
                                    try { getQueueSend().put(new FinDePartieCmd(jeu.getGagnant())); }
                                    catch (InterruptedException ignored) {}
                                }
                                gererFinDePartieReseau();   //retour accueil
                            }
                        });
                    }

                    else if (cmd instanceof FinDePartieCmd) {
                        System.out.println("[MOTEUR] FinDePartieCmd reçue");
                        SwingUtilities.invokeLater(this::gererFinDePartieReseau);
                    }

                    else if (cmd instanceof FinDeConnexionCmd || cmd instanceof DeconnexionCmd) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "L’autre joueur a quitté la partie.",
                                    "Déconnexion",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            arretConnexion();
                            vue.allerA("accueil");
                        });
                        break;
                    }



                }
            }
            catch (InterruptedException ie) {
                System.out.println("[MOTEUR] Interrompu (arrêt propre)");
                Thread.currentThread().interrupt();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null,
                            "Erreur réseau : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                    arretConnexion();
                    vue.allerA("accueil");
                });
            }
        }, "Moteur");

        thMoteur.start();
    }


    public void setJeu(Jeu jeu) { this.jeu = jeu; }
    public BlockingQueue<Command> getQueueLocale() { return qLocale; }

    public int getCarteSelectionnee() {
        return carte_selectionnee;
    }

}