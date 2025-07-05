package Vue;

import Global.UCC;
import Modele.CarteJeu;
import Modele.Couleur;
import Modele.Jeu;
import java.io.FileNotFoundException;
import java.util.Scanner;


/*** Class permettant de définir le jeu ( pour tester )*/
public class affichagetextuelle {
    Jeu jeu;
    Scanner scan;

    public affichagetextuelle(Jeu jeu) {
        this.jeu = jeu;
        scan = new Scanner(System.in);
        nouvelle_partie();
    }

    public void nouvelle_partie(){

        String partie = "none";
        while (true) {
            System.out.print("Veuillez choisir une partie : \n-Partie local\n-Partie en ligne\n-exit\n->>");
            partie = scan.nextLine();
            switch (partie){
                case "Partie local":
                    mode_jeu();
                    break;
                case "Partie en ligne":
                    System.out.println("Partie en ligne pas encore implémenter");
                    break;
                case "exit":
                    System.out.println("exiting.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Commande inconnue");
            }
        }
    }

    public void mode_jeu(){

        String mode_de_jeu="none";
        boolean retour = false;
        while (!retour) {
            System.out.print("Veuillez choisir une mode\n- Joueur contre Joueur\n- Joueur contre IA\n- IA contre IA\n- back\n- exit\n->>");
            mode_de_jeu= scan.nextLine();
            switch (mode_de_jeu){
                case "Joueur contre Joueur":
                    jeu.setModeJeu(mode_de_jeu);
                    game_local();
                    break;
                case "Joueur contre IA":
                    jeu.setModeJeu(mode_de_jeu);
                    System.out.println("Joueur contre IA Pas encore implémenter");
                    break;
                case "IA contre IA":
                    System.out.println("IA contre IA Pas encore implémenter");
                    jeu.setModeJeu(mode_de_jeu);
                    break;
                case "back":
                    retour = true;
                    break;
                case "exit":
                    System.out.println("exiting.");
                    System.exit(0);
                default:
                    System.out.println("Commande inconnue");
            }
        }
    }

    public void game_local(){

        String entre="";
        CarteJeu carteJ;
        String[] commande;
        boolean quitter_partie = false;
        boolean peu_jouer = true;
        boolean Ajouer = true;
        boolean nouvelle_partie_oui_non = false;
        boolean commande_On_Off=true;
        while (!quitter_partie) {
            if(Ajouer){

                peu_jouer = jeu.tour_est_jouable();
                Ajouer=false;
            }
            if(jeu.estTermine()){
                System.out.print("La partie est terminer\nLe gagnant est : ");
                if(jeu.getGagnant()== Couleur.ROUGE){
                    System.out.println("Le joueur 1 (Rouge) à gagné ");;
                }else{
                    System.out.println("Le joueur 2 (Bleu) à gagné");
                }
            }
            if(commande_On_Off) {
                System.out.print("Commande :\n- help règle/Commande(par défaut)\n- nouvelle_partie\n- joue [carte] [from_i] [from_j] [to_i] [to_j]\n- jeter [carte]\n- afficher\n- annuler\n- refaire\n- sauvegarder [nom_du_fichier]\n- charger [nom_du_fichier]\n- commande\n- quitter_partie\n- exit\n");
            }
            System.out.print("->> ");
            entre = scan.nextLine();
            commande = entre.split(" ");
            switch (commande[0]) {
                case "help":
                    if (commande.length == 1) {
                        help_commande();
                    } else {
                        switch (commande[1]) {
                            case "règle":
                                help_regle();
                                break;
                            case "commande":
                                help_commande();
                                break;
                            default:
                                System.out.println("paramètre " + commande[1] + " inconnue\n");
                        }
                    }
                    break;
                case "nouvelle_partie":
                    jeu.nouvelle_partie();
                    break;
                case "joue":
                    if (commande.length == 6) {
                        int carte = Integer.parseInt(commande[1]);
                        int fromY = Integer.parseInt(commande[2]);
                        int fromX = Integer.parseInt(commande[3]);
                        int toY = Integer.parseInt(commande[4]);
                        int toX = Integer.parseInt(commande[5]);
                        if (jeu.getjoueurCourant()) {
                            carteJ = jeu.getJoueur2().getCarte(carte - 1);
                        } else {
                            carteJ = jeu.getJoueur1().getCarte(carte - 1);
                        }
                        if (carteJ == null) break;
                        Ajouer = jeu.jouerCoup(fromX, fromY, toX, toY, carteJ);
                        if (!Ajouer) {
                            System.out.println("Erreur impossible de jouer ce coup.");
                        }else {
                            System.out.println("\n"+jeu);
                        }
                    } else {
                        System.err.println("paramètre incorrecte");
                        help_commande();
                    }
                    break;
                case "jeter":

                    if (commande.length == 2) {
                        if (!peu_jouer) {
                            int carte = Integer.parseInt(commande[1]);
                            if (jeu.getjoueurCourant()) {
                                carteJ = jeu.getJoueur2().getCarte(carte-1);
                            } else {
                                carteJ = jeu.getJoueur1().getCarte(carte-1);
                            }
                            if (carteJ == null) break;
                            jeu.swapCartes(carteJ);
                            Ajouer = true;
                        }
                    }else {
                        System.err.println("paramètre incorrecte");
                        help_commande();
                    }
                    break;
                case "afficher":
                    System.out.println(jeu.toString());
                    break;
                case "annuler":
                    jeu.annuler();
                    break;
                case "refaire":
                    jeu.refaire();
                    break;
                case "sauvegarder":
                    if (commande.length == 2) {
                        try {
                            String nomFichier = commande[1];
                            boolean succes = jeu.sauvegarder(nomFichier);
                            if (succes) {
                                System.out.println("Partie sauvegardée avec succès dans " + nomFichier);
                            } else {
                                System.out.println("Échec de la sauvegarde");
                            }
                        } catch (FileNotFoundException e) {
                            System.out.println("Erreur : Impossible de créer le fichier - " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("Erreur inattendue : " + e.getMessage());
                        }
                    } else {
                        System.out.println("Usage: sauvegarder [nom_fichier]");
                    }
                    break;
                case "charger":
                    if (commande.length == 2) {
                        try {
                            String nomFichier = commande[1];
                            boolean succes = jeu.charger(nomFichier);
                            if (succes) {
                                System.out.println("Partie chargée avec succès depuis " + nomFichier);
                            } else {
                                System.out.println("Échec du chargement de la partie.");
                            }

                        } catch (Exception e) {
                            System.out.println("Erreur inattendue : " + e.getMessage());
                        }
                    } else {
                        System.out.println("Usage: charger [nom_fichier]");
                    }
                    break;
                case "commande" :
                    commande_On_Off=commande_On_Off ? false : true;
                    break;
                case "quitter_partie":
                    quitter_partie = true;
                    break;
                case "exit":
                    System.out.println("exiting.");
                    System.exit(0);
                default:
                    System.out.println("Commande inconnue");
                    UCC.alerte("Commande inconnue dans commande");
            }

        }
    }

    public void help_regle(){
        System.out.println("Règle du jeu ici.\n");
    }

    public void help_commande(){
        System.out.println("" +
                "- help règle/Commande(par défaut)\n" +
                "   paramètre règle : affiche les règle du jeu\n"+
                "   paramètre commande : affiche les information sur les différentes commandes (par défaut)\n"+
                "- nouvelle partie\n"+
                "   Permet de lancer une nouvelle partie.\n"+
                "- joue [carte] [from_i] [from_j] [to_i] [to_j]\n" +
                "   Permet de jouer une carte de sa main ([carte] doit être la valeur 1 ou 2).\n" +
                "   avec le pion au coordonnée [from_i] [from_j] (valeur autoriser de 0 à 4 inclus) \n" +
                "   à la position de [to_i] [to_j] (valeur autoriser de 0 à 4 inclus)\n"+
                "- jeter [carte]\n" +
                "   Permet de jeter la carte ([carte] doit être la valeur 1 ou 2).\n" +
                "- afficher\n" +
                "  Permet d'afficher le jeu.\n" +
                "- annuler\n" +
                "   Permet d'annuler un coup du joueur\n" +
                "- refaire\n" +
                "   Permet de refaire un coup du joueur l'autre joueur peut refaire un coup lui même.\n" +
                "- sauvegarder [nom_du_ficher]\n" +
                "   Permet de sauvegarder une partie sous le nom : nom_du_fichier.\n" +
                "- charger [nom_du_fichier]\n" +
                "   Permet de charger une partie sauvegarder sous ce nom : nom_du_fichier.\n" +
                "- commande\n" +
                "   Permet de désactiver ou activer l'affichage des commandes à chaque entré.\n" +
                "- quitter_partie\n" +
                "   Permet de quitter la partie en cours\n" +
                "- exit\n" +
                "   Arrête le programme.\n");
    }
}
