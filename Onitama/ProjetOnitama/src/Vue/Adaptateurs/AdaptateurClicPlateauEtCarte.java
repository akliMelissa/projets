package Vue.Adaptateurs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

import Vue.CollecteurEvenements;
import Vue.PlateauGraphique;


// Pour selectionner des pieces sur le plateau et des cartes pour effectuer des actions
public class AdaptateurClicPlateauEtCarte extends MouseAdapter {

    private final CollecteurEvenements collecteur;   // pour transmettre les actions
    private final PlateauGraphique plateau;

    // Constructeur de l'adaptateur
    public AdaptateurClicPlateauEtCarte(CollecteurEvenements collecteur, PlateauGraphique plateau) {
        this.collecteur = collecteur;
        this.plateau = plateau;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Dimensions du composant
        int largeurTotale = plateau.getWidth();
        int hauteurTotale = plateau.getHeight();

        // Calcul des dimensions du plateau de jeu
        int dimensionBase = Math.min(largeurTotale, hauteurTotale);
        int taillePlateau = (int)(dimensionBase * PlateauGraphique.RATIO_GRILLE);
        int debutX = (largeurTotale - taillePlateau) / 2;
        int debutY = (hauteurTotale - taillePlateau) / 2;
        int tailleCase = taillePlateau / 5; // Taille d'une case (plateau 5x5)

        // Position du clic
        int x = e.getX();
        int y = e.getY();

        // Vérifier si le clic est sur le plateau
        if (estClicSurPlateau(x, y, debutX, debutY, taillePlateau)) {
            gererClicSurPlateau(x, y, debutX, debutY, tailleCase);
            return;
        }

        // Si le clic n'est pas sur le plateau, vérifier s'il est sur une carte
        gererClicSurCarte(x, y, debutX, debutY, taillePlateau, dimensionBase);
    }




    // Verifie si le clic est dans les limites du plateau
    private boolean estClicSurPlateau(int x, int y, int debutX, int debutY, int taillePlateau) {
        return x >= debutX && x < debutX + taillePlateau &&
                y >= debutY && y < debutY + taillePlateau;
    }

    //------>>>>>>>> changé iciiiii
    // Gere un clic sur le plateau en calculant la case correspondante
    private void gererClicSurPlateau(int x, int y, int debutX, int debutY, int tailleCase) {

        // Convertir les coordonnées du clic en indices de ligne et colonne
        int colonne = (x - debutX) / tailleCase;
        int ligne = (y - debutY) / tailleCase;

        // Notifier le collecteur d'événements du clic sur le plateau
        plateau.setPieceSelectionnee(new Point(colonne, ligne));
        collecteur.clic_sur_plateau(colonne , ligne);  //changé iciiiii colone puis ligne
    }

    // Gere un clic sur les cartes en determinant quelle carte a ete cliquee
    private void gererClicSurCarte(int x, int y, int debutX, int debutY, int taillePlateau, int dimensionBase) {

        // Calcul des dimensions et positions des cartes
        int marge = (int)(dimensionBase * PlateauGraphique.RATIO_MARGE);
        int margeDerniereCarte = (int)(dimensionBase * PlateauGraphique.RATIO_MARGE_DERNIERE_CARTE);
        int largeurCarte = (int)(dimensionBase * PlateauGraphique.RATIO_LARGEUR_CARTE);
        int hauteurCarte = (int)(dimensionBase * PlateauGraphique.RATIO_HAUTEUR_CARTE);

        // Position des cartes du haut (cartes du joueur 1)
        int[] posXHaut = new int[] {
                debutX + taillePlateau/2 - marge/2 - largeurCarte,
                debutX + taillePlateau/2 + marge/2
        };
        int posYHaut = debutY - marge - hauteurCarte;

        // Position des cartes du bas (cartes du joueur 2)
        int[] posXBas = posXHaut;
        int posYBas = debutY + taillePlateau + marge;

        // Position de la carte centrale (cartePartie)
        int posXDroite = debutX + taillePlateau + margeDerniereCarte;
        int posYDroite = debutY + (taillePlateau - hauteurCarte)/2;

        // --- Déterminer qui joue ---
        boolean joueurBleu = plateau.getJeu().getjoueurCourant();

        if (!joueurBleu) {
            // Joueur ROUGE (cartes en haut)
            for (int i = 0; i < 2; i++) {
                if (estClicSurCarte(x, y, posXHaut[i], posYHaut, largeurCarte, hauteurCarte)) {
                    selectionnerCarte(i);  // index 0 ou 1 pour joueur 1
                    return;
                }
            }
        } else {
            // Joueur BLEU (cartes en bas)
            for (int i = 0; i < 2; i++) {
                if (estClicSurCarte(x, y, posXBas[i], posYBas, largeurCarte, hauteurCarte)) {
                    selectionnerCarte(i);  // index 0 ou 1 pour joueur 2 aussi
                    return;
                }
            }
        }

        // Carte centrale (non sélectionnable)
        if (estClicSurCarte(x, y, posXDroite, posYDroite, largeurCarte, hauteurCarte)) {
            System.out.println("Carte de côté sélectionnée (ignorée)");
            return;
        }
    }


    /**
     * Vérifie si le clic est dans les limites d'une carte
     */
    private boolean estClicSurCarte(int x, int y, int carteX, int carteY, int largeurCarte, int hauteurCarte) {
        return x >= carteX && x <= carteX + largeurCarte &&
                y >= carteY && y <= carteY + hauteurCarte;
    }

    /**
     * Sélectionne une carte et notifie le collecteur d'événements
     */
    private void selectionnerCarte(int indiceCarte) {
        plateau.setCarteSelectionnee(indiceCarte);
        collecteur.selectionner_carte(indiceCarte);
    }
}
