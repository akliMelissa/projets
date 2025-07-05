package Vue;

import Global.UCC;
import Vue.Adaptateurs.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;

/*** Vue d'accueil de l'application **/
public class Accueil extends JComponent {

    Boolean estFrancais = true; // pour la langue
    private final CollecteurEvenements collecteur_evenements;

    // style
    private String chemin_image_fond;
    private Image image_fond;

    // les couleurs
    private Color couleur_bouton;
    private Color couleur_survol;
    private Color couleur_pression;
    private Color couleur_conteur;

    // les boutons
    private final BoutonArrondi bouton_demarrer;
    private final BoutonArrondi bouton_tutoriel;
    private final BoutonArrondi bouton_continuer;
    private final BoutonArrondi bouton_parametres;


    //Constructeur
    public Accueil(CollecteurEvenements c, Theme theme) {

        // init le collecteur des evenements
        this.collecteur_evenements = c;

        // les images du theme
        this.chemin_image_fond = theme.getImgAccueil();
        this.couleur_bouton = theme.getCouleurBoutonNormal();
        this.couleur_survol = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur = theme.getCouleurBoutonConteur();

        setLayout(null);
        charger_image_fond();

        // creations des boutons arrondis
        bouton_demarrer = new BoutonArrondi("Démarrer le jeu", couleur_bouton, couleur_survol,
                couleur_pression ,couleur_conteur);
        bouton_tutoriel = new BoutonArrondi("Tutoriel", couleur_bouton, couleur_survol,
                couleur_pression, couleur_conteur);
        bouton_continuer = new BoutonArrondi("Continuer une partie", couleur_bouton,
                couleur_survol, couleur_pression, couleur_conteur);
        bouton_parametres = new BoutonArrondi("Paramètres", couleur_bouton,
                couleur_survol, couleur_pression, couleur_conteur);

        // ajout des listeners
        bouton_demarrer.addActionListener(new Adaptateur_bouton_demarrer(collecteur_evenements));
        bouton_tutoriel.addActionListener(new Adaptateur_bouton_tutoriel(collecteur_evenements));
        bouton_continuer.addActionListener(new Adaptateur_bouton_continuer(collecteur_evenements));
        bouton_parametres.addActionListener(e -> collecteur_evenements.clic_bouton_parametres());

        // ajout des boutons a la vue
        add(bouton_demarrer);
        add(bouton_tutoriel);
        add(bouton_continuer);
        add(bouton_parametres);

        // positionnement des buttons
        positionner_boutons();

        // reagir au redimensionnement pour repositionner les boutons
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionner_boutons();
            }
        });
    }

    // changement de l'image de fond
    public void changer_image_fond(String chemin) {
        this.chemin_image_fond = chemin;
        charger_image_fond();
        repaint();
    }

    // changement de theme (image de fond et coleur des boutons)
    public void appliquerTheme(Theme theme) {

        // changement de fond
        changer_image_fond(theme.getImgAccueil());

        // changement de couleur des boutons
        this.couleur_bouton = theme.getCouleurBoutonNormal();
        this.couleur_survol = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur = theme.getCouleurBoutonConteur();

        bouton_demarrer.setCouleurs(couleur_bouton, couleur_survol, couleur_pression ,
                couleur_conteur);
        bouton_tutoriel.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        bouton_continuer.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        bouton_parametres.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);

        repaint();
    }

   // chargement d'image de fond
    private void charger_image_fond() {
        try {
            image_fond = ImageIO.read(UCC.ouvre(chemin_image_fond));
        } catch (IOException e) {
            image_fond = null;
            System.err.println("Impossible de charger l'image de fond : " + chemin_image_fond);
        }
    }

    // positionnement des boutons au centre
    private void positionner_boutons() {
        int largeur = getWidth();
        int hauteur = getHeight();
        int largeur_bouton = largeur / 3;
        int hauteur_bouton = 50;
        int espace = 18;
        int hauteur_totale = 3 * hauteur_bouton + 2 * espace;

        int decalage_vertical = hauteur / 6;
        int y_debut = (hauteur - hauteur_totale) / 2 + decalage_vertical;
        int x = (largeur - largeur_bouton) / 2;

        bouton_demarrer.setBounds(x, y_debut, largeur_bouton, hauteur_bouton);
        bouton_continuer.setBounds(x, y_debut + (hauteur_bouton + espace), largeur_bouton, hauteur_bouton);
        bouton_parametres.setBounds(x, y_debut + 2 * (hauteur_bouton + espace), largeur_bouton, hauteur_bouton);
        bouton_tutoriel.setBounds(x, y_debut + 3 * (hauteur_bouton + espace), largeur_bouton, hauteur_bouton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image_fond != null) {
            g.drawImage(image_fond, 0, 0, getWidth(), getHeight(), (ImageObserver) this);
        }
    }

    // changement de langue
    public void changerLangue(Boolean est_francais){
        this.estFrancais = est_francais;

        if (estFrancais) {
            bouton_demarrer.setText("Démarrer le jeu");
            bouton_tutoriel.setText("Tutoriel");
            bouton_continuer.setText("Continuer une partie");
            bouton_parametres.setText("Paramètres");
        } else {
            bouton_demarrer.setText("Start Game");
            bouton_tutoriel.setText("Tutorial");
            bouton_continuer.setText("Continue Game");
            bouton_parametres.setText("Settings");
        }
        repaint();
    }
}
