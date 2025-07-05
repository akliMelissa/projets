package Vue;
import Global.UCC;
import Modele.*;
import Patterns.*;

import Vue.Adaptateurs.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlateauGraphique extends JComponent implements Observateur {

    private final CollecteurEvenements collecteur_evenements;
    private final Jeu jeu;
    private Theme theme;
    Boolean estFrancais = true;

    // pour charger les images une seul fois seulement pour les cartes
    private final Map<String, Image> cacheCartes = new HashMap<>();

    private Image pionBleu, pionRouge, roiBleu, roiRouge, positionRoiRouge, positionRoiBleu;
    private Image imageFond;
    private Image[] ImagesCartes;

    private int tailleCote;
    private final int TAILLE_PLATEAU = 5;
    private final int NOMBRE_CARTES = 5;
    private final Color COULEUR_CASE = new Color(231, 215, 177, 255);
    private final Color COULEUR_SELECTION_CASE = new Color(230, 16, 62);
    private final Color COULEUR_COUP_POSSIBLE_C1= new Color(110, 182, 110, 200);
    private final Color COULEUR_COUP_POSSIBLE_C2= new Color(144, 238, 144, 180);

    private List<Point> casesCapturePossible = new ArrayList<>();
    private boolean showHighlight = true;
    private Timer timerClignotement;

    private String[] nomsCartes;

    public static final double RATIO_GRILLE = 0.5;
    public static final double RATIO_LARGEUR_CARTE = 0.25;
    public static final double RATIO_HAUTEUR_CARTE = 0.18;
    public static final double RATIO_MARGE = 0.03;
    public static final double RATIO_BOUTON = 0.07;
    public static final double RATIO_MARGE_DERNIERE_CARTE = 0.08;

    private Point pieceSelectionnee = null;
    private int carteSelectionnee = -1;
    private int indiceSurvole = -1;

    private final Color btnColor_dlt = new Color(102, 102, 102);
    private final Color btnClicked_dlt = new Color(64, 224, 189);
    private final Color btnMouseOver_dlt = new Color(255, 174, 0);

    private Image iconeAnnuler, iconeRefaire, iconeSauvegarder,
            iconeQuitter, iconeParametres, iconeNouvellePartie, iconeIndice;
    private JButton boutonAnnuler, boutonRefaire, boutonNouvellePartie,
            boutonSauvegarder, boutonParametres, boutonQuitter, boutonIndice;
    private Image textureBois, lastPosR, lastPosB;
    // Add these fields at the top with other fields
    private Image[] originalImagesCartes; // To store original card positions during swap
    private boolean needsCardUpdate = false; // Flag to update cards after animation


    // animation de deplacement des pions
    private AnimationDeplacement animation;
    private Point animStartGrid, animEndGrid;
    private Image animPieceImage;
    private float animProgress = 0f;


    // Animation de swap des cartes
    private Point startPos = null;
    private Point endPos = null;
    private float swapProgress = 0f;
    private boolean isSwapping = false;
    private Timer swapAnimationTimer;
    private int swappingCardIndex = -1;
    private static final int SWAP_DURATION = 1500; // durée en millisecondes
    private long swapStartTime;

    // Swap en attendre la fin du déplacement du pion
    private int  pendingSwapIndex = -1;   // aucun swap en attente
    private static final int INDEX_CARTE_CENTRALE = 4;   // carte du milieu

    /**
     * Constructeur de la classe PlateauGraphique qui initialise les composants visuels
     * du plateau de jeu, les boutons d'interaction et les gestionnaires d'événements.
     */
    public PlateauGraphique(CollecteurEvenements c, Jeu jeu, Theme theme) {
        this.collecteur_evenements = c;
        this.jeu = jeu;
        jeu.ajouteObservateur(this);
        this.theme = theme;
        ImagesCartes = new Image[NOMBRE_CARTES];
        chargerImageFond(theme);
        setNomsCartes();
        chargerImages(theme);  //pions et icons
        chargerImagesCartes();  // cartes
        recupererImgesNomsOrdre();

        // Création des boutons
        if(!jeu.getModeJeu().equals("ModeReseau")){
            boutonAnnuler = creerBouton(iconeAnnuler, "Annuler");
            boutonRefaire = creerBouton(iconeRefaire, "Refaire");
            boutonNouvellePartie = creerBouton(iconeNouvellePartie, "Nouvelle partie");
            boutonSauvegarder = creerBouton(iconeSauvegarder, "Sauvegarder");
        }
        boutonParametres = creerBouton(iconeParametres, "Paramètres");
        boutonQuitter = creerBouton(iconeQuitter, "Quitter");
        boutonIndice = creerBouton(iconeIndice, "Indice");

        // Ajout des listeners aux boutons
        if(!jeu.getModeJeu().equals("ModeReseau")){
            boutonAnnuler.addActionListener(new Adaptateur_bouton_annuler(c));
            boutonRefaire.addActionListener(new Adaptateur_bouton_refaire(c));
            boutonNouvellePartie.addActionListener(new Adaptateur_bouton_nouvelle_partie(c));
            boutonSauvegarder.addActionListener(new Adaptateur_bouton_sauvegarder(c));
        }
        boutonParametres.addActionListener(new Adaptateur_bouton_parametres(c));
        boutonQuitter.addActionListener(new Adaptateur_bouton_quitter(c));
        boutonIndice.addActionListener(new Adaptateur_bouton_Indice(c));

        addMouseListener(new AdaptateurClicPlateauEtCarte(collecteur_evenements, this));
        addComponentListener(new Adaptateur_redimensionnement(collecteur_evenements));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int ancien = indiceSurvole;
                indiceSurvole = detecterIndiceCarte(p);
                if (indiceSurvole != ancien) {
                    repaint();
                }
            }
        });
        timerClignotement = new Timer(500, e -> {
            showHighlight = !showHighlight;
            repaint();
        });
        timerClignotement.start();
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
    }

    /**
     * Méthode pour définir les noms des cartes utilisées dans la partie et les préparer
     * à être affichées dans l'interface graphique.
     */
    void setNomsCartes() {
        CarteJeu carte1J1 = jeu.getJoueur1().getCarte(0);
        CarteJeu carte2J1 = jeu.getJoueur1().getCarte(1);
        CarteJeu carte1J2 = jeu.getJoueur2().getCarte(0);
        CarteJeu carte2J2 = jeu.getJoueur2().getCarte(1);
        CarteJeu cartePartie = jeu.getCartePARTIE();

        nomsCartes = new String[]{
                carte1J1.getNomCarte() + ".png",
                carte2J1.getNomCarte() + ".png",
                carte1J2.getNomCarte() + ".png",
                carte2J2.getNomCarte() + ".png",
                cartePartie.getNomCarte() + ".png"
        };
    }

    /**
     * Crée un bouton cercle avec une icône
     */
    private JButton creerBouton(Image icone, String infobulle) {
        return new BoutonCercle(icone, infobulle);
    }

    /**
     * Place les boutons autour du plateau.
     * */
    private void disposerBoutons() {

        for (JButton btn : new JButton[]{
                boutonAnnuler, boutonRefaire,
                boutonNouvellePartie, boutonSauvegarder,
                boutonIndice, boutonParametres,
                boutonQuitter
        }) {
            if (btn != null) remove(btn);
        }

        int taille = (int) (getHeight() * RATIO_BOUTON);
        int marge = taille / 2, espace = 5, posX = marge, posY = marge;

        if(!jeu.getModeJeu().equals("ModeReseau")){
            boutonAnnuler.setBounds(posX, posY, taille, taille);
            boutonRefaire.setBounds(posX += taille + espace, posY, taille, taille);
            boutonNouvellePartie.setBounds(posX += taille + espace, posY, taille, taille);
            boutonIndice.setBounds(posX += taille + espace, posY, taille, taille);
        }else{
            boutonIndice.setBounds(posX, posY, taille, taille);
        }

        posX = getWidth() - marge - taille;
        boutonQuitter.setBounds(posX, posY, taille, taille);
        boutonParametres.setBounds(posX -= (taille + espace), posY, taille, taille);

        if(!jeu.getModeJeu().equals("ModeReseau")){
            boutonSauvegarder.setBounds(posX -= (taille + espace), posY, taille, taille);
        }

        JButton[] ma_liste;
        if(!jeu.getModeJeu().equals("ModeReseau")) {
            ma_liste =new JButton[]{boutonAnnuler, boutonRefaire, boutonNouvellePartie, boutonIndice,
                    boutonSauvegarder, boutonParametres, boutonQuitter};
        }else{
            ma_liste = new JButton[]{ boutonIndice, boutonParametres, boutonQuitter};
        }
        for (JButton bouton : ma_liste) {
            if (bouton.getParent() == null) add(bouton);
        }
    }
    /**
     * Méthode pour charger l'image de fond du plateau de jeu en fonction du thème sélectionné.
     */
    private void chargerImageFond(Theme theme) {
        try {
            imageFond = ImageIO.read(UCC.ouvre(theme.getImgPlateau()));
        } catch (Exception exception) {
            imageFond = creerImageFondParDefaut();
        }
    }
    /**
     * Initialisation des icônes et textures par défaut lorsque
     * les images ne peuvent pas être chargées.
     */

    private void chargerImages(Theme theme) {
        try {
            pionBleu = ImageIO.read(UCC.ouvre(theme.getPionBleu()));
            pionRouge = ImageIO.read(UCC.ouvre(theme.getPionRouge()));
            roiBleu = ImageIO.read(UCC.ouvre(theme.getRoiBleu()));
            roiRouge = ImageIO.read(UCC.ouvre(theme.getRoiRouge()));
            positionRoiRouge = ImageIO.read(UCC.ouvre(theme.getPositionRoiRouge()));
            positionRoiBleu = ImageIO.read(UCC.ouvre(theme.getPositionRoiBleu()));

            iconeAnnuler = ImageIO.read(UCC.ouvre("Images/icons/annuler.png"));
            iconeRefaire = ImageIO.read(UCC.ouvre("Images/icons/refaire.png"));
            iconeSauvegarder = ImageIO.read(UCC.ouvre("Images/icons/sauvgarder.png"));
            iconeQuitter = ImageIO.read(UCC.ouvre("Images/icons/quitter.png"));
            iconeParametres = ImageIO.read(UCC.ouvre("Images/icons/parametres.png"));
            iconeNouvellePartie = ImageIO.read(UCC.ouvre("Images/icons/nouvelle_partie.png"));
            iconeIndice = ImageIO.read(UCC.ouvre("Images/icons/Indice.png"));
            textureBois = ImageIO.read(UCC.ouvre("Images/boisClair.jpeg"));
            lastPosR = ImageIO.read(UCC.ouvre("Images/lastPosR.png"));
            lastPosB =ImageIO.read(UCC.ouvre("Images/lastPosB.png"));

        } catch (IOException exception) {
            pionBleu = creerImageTemporaire(Color.BLUE);
            pionRouge = creerImageTemporaire(Color.RED);
            roiBleu = creerImageTemporaire(Color.CYAN);
            roiRouge = creerImageTemporaire(Color.MAGENTA);
            positionRoiRouge = creerImageTemporaire(Color.ORANGE);
            positionRoiBleu = creerImageTemporaire(Color.CYAN);
            iconeAnnuler = creerImageTemporaire(Color.GRAY);
            iconeRefaire = creerImageTemporaire(Color.GRAY);
            iconeSauvegarder = creerImageTemporaire(Color.GRAY);
            iconeQuitter = creerImageTemporaire(Color.GRAY);
            iconeParametres = creerImageTemporaire(Color.GRAY);
            iconeNouvellePartie = creerImageTemporaire(Color.GRAY);
            iconeIndice = creerImageTemporaire(Color.GRAY);
            textureBois = null;
            lastPosR = creerImageTemporaire(Color.RED);
            lastPosB = creerImageTemporaire(Color.BLUE);
        }

    }

    private Image creerImageTemporaire(Color couleur) {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphique = image.createGraphics();
        graphique.setColor(couleur);
        graphique.fillOval(5, 5, 40, 40);
        graphique.dispose();
        return image;
    }

    private Image creerImageFondParDefaut() {

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphique = image.createGraphics();
        GradientPaint degrade = new GradientPaint(0, 0, new Color(11, 101, 157, 190),
                800, 600, new Color(185, 10, 37));
        graphique.setPaint(degrade);
        graphique.fillRect(0, 0, 800, 600);
        graphique.dispose();
        return image;
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D dessin = (Graphics2D) g;
        dessin.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /*──────────────────  Fond ──────────────────*/
        if (imageFond != null) {
            dessin.drawImage(imageFond, 0, 0, getWidth(), getHeight(), this);
        }

        /*──────────────────  Paramètres plateau ─────*/
        int largeur = getWidth(), hauteur = getHeight();
        int dimension = Math.min(largeur, hauteur);
        int taillePlateau = (int) (dimension * RATIO_GRILLE);
        tailleCote = taillePlateau / TAILLE_PLATEAU;
        int debutX = (largeur - taillePlateau) / 2;
        int debutY = (hauteur - taillePlateau) / 2;


        TexturePaint textureBoisPaint = null;
        if (textureBois != null) {
            BufferedImage bufImg = toBufferedImage(textureBois);
            textureBoisPaint = new TexturePaint(bufImg,
                    new Rectangle(0, 0, bufImg.getWidth(),
                            bufImg.getHeight()));
        }
        Pion[][] plateau = jeu.getPlateau();
        Point casePrecedente = jeu.getCasePrecedente();
        List<Point> casesJouables_c1 = jeu.get_cases_jouables_tour_c1();
        List<Point> casesJouables_c2= jeu.get_cases_jouables_tour_c2();
        boolean animEnCours = (animation != null && animProgress < 1f);
        /*──────────────────  Grille + pièces ────────*/
        for (int lig = 0; lig < TAILLE_PLATEAU; lig++) {
            for (int col = 0; col < TAILLE_PLATEAU; col++) {
                int x = debutX + col * tailleCote;
                int y = debutY + lig * tailleCote;


                Point pt = new Point(col, lig);


                if (casesJouables_c1.contains(pt)) {
                    dessin.setColor(COULEUR_COUP_POSSIBLE_C2);
                    dessin.fillRect(x, y, tailleCote, tailleCote);
                }else if (casesJouables_c2.contains(pt)){
                    dessin.setColor(COULEUR_COUP_POSSIBLE_C1);
                    dessin.fillRect(x, y, tailleCote, tailleCote);
                } else if (textureBoisPaint != null) {
                    // Utiliser la texture bois pour remplir la case
                    dessin.setPaint(textureBoisPaint);
                    dessin.fillRect(x, y, tailleCote, tailleCote);
                    Color overlay = ((lig + col) % 2 == 0)
                            ? new Color(255, 255, 255, 40)
                            : new Color(0, 0, 0, 40);
                    dessin.setColor(overlay);
                    dessin.fillRect(x, y, tailleCote, tailleCote);
                } else {

                    dessin.setColor(COULEUR_CASE);
                    dessin.fillRect(x, y, tailleCote, tailleCote);
                }


                dessin.setColor(Color.BLACK);
                dessin.drawRect(x, y, tailleCote, tailleCote);

                /* Surbrillance « case précédente » */
                if (casePrecedente != null && pt.equals(casePrecedente)) {
                    if (jeu.getjoueurCourant()) {
                        dessin.drawImage(lastPosR, x, y, tailleCote, tailleCote, this);
                    } else {
                        dessin.drawImage(lastPosB, x, y, tailleCote, tailleCote, this);
                    }
                }

                /* Bordure de sélection */
                if (pieceSelectionnee != null
                        && pieceSelectionnee.x == col
                        && pieceSelectionnee.y == lig
                        && !animEnCours) {
                    dessin.setStroke(new BasicStroke(2));
                    dessin.setColor(COULEUR_SELECTION_CASE);
                    dessin.drawRect(x, y, tailleCote, tailleCote);
                    dessin.setStroke(new BasicStroke(1));
                }

                /* Indicateurs positions Roi fixes */
                if (lig == 4 && col == 2) {
                    dessin.drawImage(positionRoiBleu, x + 5, y, tailleCote - 10, tailleCote - 10, null);
                } else if (lig == 0 && col == 2) {
                    dessin.drawImage(positionRoiRouge, x + 5, y, tailleCote - 10, tailleCote - 10, null);
                }

                /* Pièce à dessiner ? */
                Pion pion = plateau[lig][col];

                /*―――― Filtre : on masque départ et arrivée pendant l’anim ――――*/
                if (animEnCours) {
                    boolean depart = (animStartGrid != null
                            && animStartGrid.x == col
                            && animStartGrid.y == lig);
                    boolean arrivee = (animEndGrid != null
                            && animEndGrid.x == col
                            && animEndGrid.y == lig);
                    if (depart || arrivee) {
                        pion = null;
                    }
                }
                /*――――――――――――――――――――――――――――――――――――――――――――――――――――――*/

                if (pion != null && pion.estActif()) {
                    Image img;
                    if (pion.getRole() == Role.ROI)
                        img = (pion.getCouleur() == Couleur.BLEU) ? roiBleu : roiRouge;
                    else
                        img = (pion.getCouleur() == Couleur.BLEU) ? pionBleu : pionRouge;

                    dessin.drawImage(img, x, y, tailleCote, tailleCote, null);
                }
            }
        }

        /*────────────────── Cadres capture possibles ───────────*/
        if (showHighlight) {
            Graphics2D g2 = (Graphics2D) g.create();
            Color color = (theme == Theme.CLAIR)? Color.YELLOW : new Color(220, 25, 25);
            g2.setColor(color);
            int dX = (getWidth() - taillePlateau) / 2;
            int dY = (getHeight() - taillePlateau) / 2;

            for (Point p : casesCapturePossible) {
                int rx = dX + p.x * tailleCote;
                int ry = dY + p.y * tailleCote;
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(rx + 2, ry + 2, tailleCote - 4, tailleCote - 4);
                g2.setStroke(new BasicStroke(1));
            }
            g2.dispose();
        }

        /*────────────────── Cartes, flèches, preview ───────────*/
        dessinerCartes(dessin, dimension, debutX, debutY, taillePlateau);

        if (indiceSurvole >= 0) dessinerPreviewCarte(dessin);

        /*──────────────────  Pion animé  ───────────────*/
        if (animEnCours) {
            int taille = taillePlateau / TAILLE_PLATEAU;
            int sx = debutX + animStartGrid.x * taille;
            int sy = debutY + animStartGrid.y * taille;
            int ex = debutX + animEndGrid.x * taille;
            int ey = debutY + animEndGrid.y * taille;

            int px = (int) (sx + (ex - sx) * animProgress);
            int py = (int) (sy + (ey - sy) * animProgress);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(animPieceImage, px, py, taille, taille, this);
            g2.dispose();
        }

        dessinerStatut(dessin);

        /*──────────────────  Boutons ────────────────────────────*/
        disposerBoutons();
    }


    private void dessinerPreviewCarte(Graphics2D g2d) {
        if (indiceSurvole < 0 || indiceSurvole >= ImagesCartes.length) return;
        Image carte = ImagesCartes[indiceSurvole];
        if (carte == null) return;

        // calcul de l’échelle
        int hMax = (int)(getHeight() * 0.3);
        int wMax = (int)(getWidth()  * 0.25);
        int w0 = carte.getWidth(this), h0 = carte.getHeight(this);
        if (w0 <= 0 || h0 <= 0) return;
        float scale = Math.min((float)wMax/w0, (float)hMax/h0);
        int wImg = (int)(w0 * scale), hImg = (int)(h0 * scale);


        String ligne1 = estFrancais
                ? "Note : zoom sur la carte survolée"
                : "Note: zoom on hovered card";


        TraducteurCarteOnitama traducteur = new TraducteurCarteOnitama();
        String fileName = nomsCartes[indiceSurvole];
        String nomCarte = fileName.substring(0, fileName.lastIndexOf('.'));
        String ligne2 = nomCarte + " / " + traducteur.enFrancais(nomCarte);

        g2d.setFont(new Font("SansSerif", Font.ITALIC, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textH = fm.getHeight();
        int textW1 = fm.stringWidth(ligne1);
        int textW2 = fm.stringWidth(ligne2);


        int padding = 10;
        int spacingText  = 4;
        int spacingImage = 8;
        int marginEx = 20;


        int contW = Math.max(Math.max(textW1, textW2), wImg) + 2*padding;
        int contH = textH * 2
                + spacingText
                + spacingImage
                + hImg
                + 2*padding;

        int contX = marginEx;
        int contY = (getHeight() - contH) / 2;

        g2d.setColor(new Color(0,0,0,200));
        g2d.fillRoundRect(contX, contY, contW, contH, 16, 16);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(contX, contY, contW, contH, 16, 16);

        int tx = contX + padding;
        int ty = contY + padding + fm.getAscent();
        g2d.setColor(Color.WHITE);
        g2d.drawString(ligne1, tx, ty);
        g2d.drawString(ligne2, tx, ty + textH + spacingText);

        int imgX = contX + (contW - wImg)/2;
        int imgY = contY + padding + textH*2 + spacingText + spacingImage;

        if (indiceSurvole <= 1) {
            double cx = imgX + wImg/2.0;
            double cy = imgY + hImg/2.0;
            g2d.rotate(Math.PI, cx, cy);
            g2d.drawImage(carte, imgX, imgY, wImg, hImg, this);
            g2d.rotate(-Math.PI, cx, cy);
        } else {
            g2d.drawImage(carte, imgX, imgY, wImg, hImg, this);
        }
    }


    public void setCarteSelectionnee(int carte) {
        if (jeu.getjoueurCourant()) {
            // BLEU → cartes [2] et [3]
            carteSelectionnee = carte + 2;
        } else {
            // ROUGE → cartes [0] et [1]
            carteSelectionnee = carte;
        }
        repaint();
    }


    private void dessinerCartes(Graphics2D dessin, int dimension, int debutX, int debutY, int taillePlateau) {
        int marge = (int) (dimension * RATIO_MARGE);
        int margeDerniereCarte = (int) (dimension * RATIO_MARGE_DERNIERE_CARTE);
        int largeurCarte = (int) (dimension * RATIO_LARGEUR_CARTE);
        int hauteurCarte = (int) (dimension * RATIO_HAUTEUR_CARTE);

        int posYHaut = debutY - marge - hauteurCarte;
        int[] posXHaut = new int[]{
                debutX + taillePlateau / 2 - marge / 2 - largeurCarte,
                debutX + taillePlateau / 2 + marge / 2
        };
        int posYBas = debutY + taillePlateau + marge;
        int posXDroite = debutX + taillePlateau + margeDerniereCarte;
        int posYDroite = debutY + (taillePlateau - hauteurCarte) / 2;

        double angleCarteMilieu = jeu.getjoueurCourant() ? 0 : 180;
        double[] angles = {180, 180, 0, 0, angleCarteMilieu};

        for (int i = 0; i < 5; i++) {
            if (isSwapping && (i == swappingCardIndex || i == 4)) {
                continue;
            }

            int x = i < 2 ? posXHaut[i] : (i < 4 ? posXHaut[i - 2] : posXDroite);
            int y = i < 2 ? posYHaut : (i < 4 ? posYBas : posYDroite);

            dessinerCarte(dessin, ImagesCartes[i], i, x, y, largeurCarte, hauteurCarte, angles[i]);
        }

        if (isSwapping && startPos != null && endPos != null) {
            int startX = swappingCardIndex < 2 ? posXHaut[swappingCardIndex] :
                    (swappingCardIndex < 4 ? posXHaut[swappingCardIndex-2] : posXDroite);
            int startY = swappingCardIndex < 2 ? posYHaut :
                    (swappingCardIndex < 4 ? posYBas : posYDroite);

            int x1 = (int) (startX + (posXDroite - startX) * swapProgress);
            int y1 = (int) (startY + (posYDroite - startY) * swapProgress);

            int x2 = (int) (posXDroite + (startX - posXDroite) * swapProgress);
            int y2 = (int) (posYDroite + (startY - posYDroite) * swapProgress);

            double angleStart = angles[swappingCardIndex];
            double angleEnd = angles[4];
            double currentAngle1 = angleStart + (angleEnd - angleStart) * swapProgress;
            double currentAngle2 = angleEnd + (angleStart - angleEnd) * swapProgress;

            dessinerCarte(dessin, originalImagesCartes[swappingCardIndex], swappingCardIndex,
                    x1, y1, largeurCarte, hauteurCarte, currentAngle1);
            dessinerCarte(dessin, originalImagesCartes[4], 4,
                    x2, y2, largeurCarte, hauteurCarte, currentAngle2);
        }
    }



//__________________partie deplacement_____________________________


    public void setPieceSelectionnee(Point p) {
        pieceSelectionnee = p;
        repaint();
    }


//__________________________________________________________________

    private int detecterIndiceCarte(Point p) {
        int largeur = getWidth(), hauteur = getHeight();
        int dimension = Math.min(largeur, hauteur);
        int taillePlateau = (int) (dimension * RATIO_GRILLE);
        int tailleCarteW = (int) (dimension * RATIO_LARGEUR_CARTE);
        int tailleCarteH = (int) (dimension * RATIO_HAUTEUR_CARTE);
        int marge = (int) (dimension * RATIO_MARGE);
        int debutX = (largeur - taillePlateau) / 2;
        int debutY = (hauteur - taillePlateau) / 2;

        // positions des 2 cartes du haut
        int posYHaut = debutY - marge - tailleCarteH;
        int[] posXHaut = new int[]{
                debutX + taillePlateau / 2 - marge / 2 - tailleCarteW,
                debutX + taillePlateau / 2 + marge / 2
        };

        // cartes 2 & 3 en bas
        int posYBas = debutY + taillePlateau + marge;
        // carte 4 à droite
        int posXDroite = debutX + taillePlateau + (int) (dimension * RATIO_MARGE_DERNIERE_CARTE);
        int posYDroite = debutY + (taillePlateau - tailleCarteH) / 2;

        Rectangle[] zones = new Rectangle[]{
                new Rectangle(posXHaut[0], posYHaut, tailleCarteW, tailleCarteH),
                new Rectangle(posXHaut[1], posYHaut, tailleCarteW, tailleCarteH),
                new Rectangle(posXHaut[0], posYBas, tailleCarteW, tailleCarteH),
                new Rectangle(posXHaut[1], posYBas, tailleCarteW, tailleCarteH),
                new Rectangle(posXDroite, posYDroite, tailleCarteW, tailleCarteH)
        };
        for (int i = 0; i < zones.length; i++) {
            if (zones[i].contains(p)) return i;
        }
        return -1;
    }


    private void dessinerCarte(Graphics g, Image img, int indexCarte,
                               int x, int y,
                               int largeur, int hauteur,
                               double angleDeg) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.translate(x + largeur / 2.0, y + hauteur / 2.0);
        g2.rotate(Math.toRadians(angleDeg));
        if (img != null) {
            g2.drawImage(img,
                    -largeur / 2, -hauteur / 2,
                    largeur, hauteur,
                    this);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillRect(-largeur / 2, -hauteur / 2, largeur, hauteur);
        }

        // dessiner la sélection
        boolean joueurCourantEstBleu = jeu.getjoueurCourant();
        boolean carteAppartientJoueurCourant = (joueurCourantEstBleu && (indexCarte >= 2 && indexCarte <= 3))
                || (!joueurCourantEstBleu && (indexCarte >= 0 && indexCarte <= 1));

        if (!carteAppartientJoueurCourant) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(-largeur / 2, -hauteur / 2, largeur, hauteur);
        }

        boolean carteSelectionnable = carteAppartientJoueurCourant;
        if (carteSelectionnable && (carteSelectionnee == indexCarte || carteSelectionnee < 0)) {
            Color coleur;
            if (indexCarte%2==0) {
                coleur = new Color(89, 246, 89, 255);;
                g2.setColor(coleur);
            }else{
                coleur = new Color(80, 196, 80, 255);
                g2.setColor(coleur);
            }

            g2.setStroke(new BasicStroke(4));
            g2.drawRect(-largeur / 2 - 2, -hauteur / 2 - 2,
                    largeur + 4, hauteur + 4);
        }


        g2.dispose();
    }

    private void chargerImagesCartes() {
        try {
            for (CarteJeu c : jeu.getCartes()) {
                String file = c.getNomCarte() + ".png";
                Image img = ImageIO.read(UCC.ouvre("Images/cartes/" + file));
                cacheCartes.put(file, img);
            }
        } catch (IOException e) {
            //
        }
    }


    private void recupererImgesNomsOrdre() {
        for (int i = 0; i < NOMBRE_CARTES; i++) {
            ImagesCartes[i] = cacheCartes.get(nomsCartes[i]);
        }
    }

    public Jeu getJeu() {
        return this.jeu;
    }

    @Override
    public void miseAJour() {
        // swap des cartes joué
        setNomsCartes();
        recupererImgesNomsOrdre();
        updateCasesCapturePossible();
        repaint();
    }

    public void appliquerTheme(Theme theme) {
        this.theme = theme;
        chargerImageFond(theme);
        chargerImages(theme);
        Color normal, hover, pressed;

        if (theme == Theme.SOMBRE) {
            normal = btnColor_dlt;
            hover = btnMouseOver_dlt;
            pressed = btnClicked_dlt;
        } else {
            normal = theme.getCouleurBoutonNormal();
            hover = theme.getCouleurBoutonSurvol();
            pressed = theme.getCouleurBoutonPression();
        }

        JButton[] ma_liste;
        if(!jeu.getModeJeu().equals("ModeReseau")) {
            ma_liste =new JButton[]{boutonAnnuler, boutonRefaire, boutonNouvellePartie, boutonIndice,
                    boutonSauvegarder, boutonParametres, boutonQuitter};
        }else{
            ma_liste = new JButton[]{ boutonIndice, boutonParametres, boutonQuitter};
        }

        for (JButton b : ma_liste) {
            ((BoutonCercle) b).setCouleurs(normal, hover, pressed);
        }
        repaint();
    }

    ;


    public void changerLangue(boolean estFrancais) {
        this.estFrancais = estFrancais;

        if (estFrancais) {

            if(!jeu.getModeJeu().equals("ModeReseau")) {
                boutonAnnuler.setToolTipText("Annuler");
                boutonRefaire.setToolTipText("Refaire");
                boutonNouvellePartie.setToolTipText("Nouvelle partie");
                boutonSauvegarder.setToolTipText("Sauvegarder");
            }
            boutonParametres.setToolTipText("Paramètres");
            boutonQuitter.setToolTipText("Quitter");
            boutonIndice.setToolTipText("Indice");
        } else {
            if(!jeu.getModeJeu().equals("ModeReseau")) {
                boutonAnnuler.setToolTipText("Undo");
                boutonRefaire.setToolTipText("Redo");
                boutonNouvellePartie.setToolTipText("New Game");
                boutonSauvegarder.setToolTipText("Save");
            }
            boutonParametres.setToolTipText("Settings");
            boutonQuitter.setToolTipText("Exit");
            boutonIndice.setToolTipText("Hint");
        }

        repaint();
    }

    public void initCarteSelctionne() {
        carteSelectionnee = -1;
    }

    private void updateCasesCapturePossible() {
        casesCapturePossible.clear();
        List<Point> casesJouables_c1 = jeu.get_cases_jouables_tour_c1();
        List<Point> casesJouables_c2 = jeu.get_cases_jouables_tour_c2();

        Pion[][] plateau = jeu.getPlateau();
        Couleur couleurJoueur = jeu.getjoueurCourant() ? Couleur.BLEU : Couleur.ROUGE;

        for (Point p : casesJouables_c1) {
            Pion pionSurCase = plateau[p.y][p.x];
            if (pionSurCase != null && pionSurCase.getCouleur() != couleurJoueur) {
                casesCapturePossible.add(p);
            }
        }
        for (Point p : casesJouables_c2) {
            Pion pionSurCase = plateau[p.y][p.x];
            if (pionSurCase != null && pionSurCase.getCouleur() != couleurJoueur) {
                casesCapturePossible.add(p);
            }
        }

    }

    public Point getPositionCarte(int indexCarte) {
        int largeur = getWidth(), hauteur = getHeight();
        int dimension = Math.min(largeur, hauteur);
        int taillePlateau = (int) (dimension * RATIO_GRILLE);
        int debutX = (largeur - taillePlateau) / 2;
        int debutY = (hauteur - taillePlateau) / 2;

        int marge = (int) (dimension * RATIO_MARGE);
        int margeDerniereCarte = (int) (dimension * RATIO_MARGE_DERNIERE_CARTE);
        int largeurCarte = (int) (dimension * RATIO_LARGEUR_CARTE);
        int hauteurCarte = (int) (dimension * RATIO_HAUTEUR_CARTE);

        // Positions cartes haut (joueur rouge)
        int posYHaut = debutY - marge - hauteurCarte;
        int[] posXHaut = new int[]{
                debutX + taillePlateau / 2 - marge / 2 - largeurCarte,
                debutX + taillePlateau / 2 + marge / 2
        };

        // Positions cartes bas (joueur bleu)
        int posYBas = debutY + taillePlateau + marge;
        int[] posXBas = new int[]{
                debutX + taillePlateau / 2 - marge / 2 - largeurCarte,
                debutX + taillePlateau / 2 + marge / 2
        };

        // Position carte milieu
        int posXDroite = debutX + taillePlateau + margeDerniereCarte;
        int posYDroite = debutY + (taillePlateau - hauteurCarte) / 2;

        switch (indexCarte) {
            case 0:
                return new Point(posXHaut[0] + largeurCarte / 2, posYHaut + hauteurCarte / 2);
            case 1:
                return new Point(posXHaut[1] + largeurCarte / 2, posYHaut + hauteurCarte / 2);
            case 2:
                return new Point(posXBas[0] + largeurCarte / 2, posYBas + hauteurCarte / 2);
            case 3:
                return new Point(posXBas[1] + largeurCarte / 2, posYBas + hauteurCarte / 2);
            case 4:
                return new Point(posXDroite + largeurCarte / 2, posYDroite + hauteurCarte / 2);
            default:
                return null;
        }
    }


    public void afficherSwapCartes(int indexCarteSelectionnee, int indexCarteCentre) {
        if (isSwapping) return;

        originalImagesCartes = ImagesCartes.clone();

        this.swappingCardIndex = indexCarteSelectionnee;
        this.isSwapping = true;
        this.swapStartTime = System.currentTimeMillis();
        this.needsCardUpdate = true;

        Point p1 = getPositionCarte(indexCarteSelectionnee);
        Point p2 = getPositionCarte(indexCarteCentre);

        if (p1 != null && p2 != null) {
            this.startPos = p1;
            this.endPos = p2;

            if (swapAnimationTimer != null && swapAnimationTimer.isRunning()) {
                swapAnimationTimer.stop();
            }

            swapAnimationTimer = new Timer(16, e -> {
                updateCardSwapAnimation();
                if (!isSwapping) {
                    ((Timer)e.getSource()).stop();
                    if (needsCardUpdate) {
                        setNomsCartes();
                        recupererImgesNomsOrdre();
                        needsCardUpdate = false;
                    }
                }
            });
            swapAnimationTimer.start();
        }
    }



    public int getcarteSelectionnee() {
        return carteSelectionnee;
    }


    public void animerDeplacement(Point start, Point end) {
        Pion pion = jeu.getPlateau()[start.y][start.x];
        Image pieceImage;

        if (pion != null) {
            if (pion.getRole() == Role.ROI) {
                pieceImage = pion.getCouleur() == Couleur.BLEU ? roiBleu : roiRouge;
            } else {
                pieceImage = pion.getCouleur() == Couleur.BLEU ? pionBleu : pionRouge;
            }

            setAnimationProgress(start, end, pieceImage, 0f);

            this.animation = new AnimationDeplacement(start, end, pieceImage, this);
            this.animation.start();
        }
    }

    /**
     * Mise à jour interne du progrès
     */
    void setAnimationProgress(Point start, Point end, Image img, float progress) {
        this.animStartGrid = start;
        this.animEndGrid = end;
        this.animPieceImage = img;
        this.animProgress = progress;
        repaint();
    }

    /**
     * Fin de l’animation : revenir au dessin normal et mettre à jour le modèle
     */
    void endAnimation() {
        this.animation    = null;
        this.animProgress = 0f;
        jeu.metAJour();

        if (pendingSwapIndex >= 0) {
            int idx = pendingSwapIndex;
            pendingSwapIndex = -1;
            startSwapAnimation(idx, INDEX_CARTE_CENTRALE);
        }
    }



    /**
     * affichage des info tour +mode + rôles
     */
    private void dessinerStatut(Graphics2D g) {
        int w = getWidth(), h = getHeight();

        // marges
        int marginX      = 7;
        int marginBottom = 7;


        float ratioBoxW    = 0.25f;  // 25 % largeur fenêtre
        float ratioBoxH    = 0.25f;  // 25 % hauteur fenêtre
        float ratioPawn    = 0.40f;  // 40 % hauteur box pour le pion
        float ratioFont    = 0.08f;  // 8 % hauteur box pour la police
        float ratioPadding = 0.05f;  //  5 % largeur box pour le padding
        float ratioSpacing = 0.05f;  //  5 % hauteur box pour l’espacement

        // dimensions de la box
        int contentW = (int)(w * ratioBoxW);
        int contentH = (int)(h * ratioBoxH);

        int padding  = (int)(contentW * ratioPadding);
        int spacing  = (int)(contentH * ratioSpacing);
        int pionSize = (int)(contentH * ratioPawn);

        int fontSize    = Math.max(8, (int)(contentW * ratioFont));
        Font font       = new Font("SansSerif", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm  = g.getFontMetrics(font);
        int lineHeight  = fm.getHeight();
        int textLines   = 3;

        int neededH = padding*2 + pionSize + spacing + textLines*lineHeight;
        if (neededH > contentH) {
            contentH = neededH;
        }

        int x = marginX;
        int y = h - contentH - marginBottom;

        g.setColor(new Color(0, 0, 200, 30));
        g.fillRect(x, y, contentW, contentH);
        g.setColor(Color.ORANGE);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, contentW, contentH);

        int cx      = x + contentW/2;
        int py      = y + padding;
        Image pionT = jeu.getjoueurCourant() ? pionBleu : pionRouge;
        if (pionT != null) {
            int px = cx - pionSize/2;
            g.drawImage(pionT, px, py, pionSize, pionSize, this);
        }

        int infoX = x + padding;
        int infoY = py + pionSize + spacing;
        g.setColor(theme == Theme.CLAIR ? Color.BLACK : Color.WHITE);

        // mode de jeu
        String rawMode = jeu.getModeJeu();
        String modeLbl;
        if (estFrancais) {
            modeLbl = rawMode;
            if("ModeReseau".equals(rawMode)){
                String jcourant =jeu.getjoueurCourant()?"Bleu":"Rouge";
                modeLbl = "Tour de Jouer :"+ jcourant;
            }

        } else {
            switch (rawMode) {
                case "Joueur contre Joueur":
                    modeLbl = "Player vs Player";
                    break;
                case "Joueur contre IA":
                    modeLbl = "Player vs AI";
                    break;
                case "IA contre IA":
                    modeLbl = "AI vs AI";
                    break;
                case "ModeReseau":
                    String jcourant =jeu.getjoueurCourant()?"Blue":"Red";
                    modeLbl = "Current player :"+ jcourant;
                    break;
                default:
                    modeLbl = "";
                    break;
            }
        }
        g.drawString(modeLbl, infoX, infoY + fm.getAscent());
        infoY += lineHeight;


        String bleuStr, rougeStr;
        if ("Joueur contre Joueur".equals(rawMode)) {
            if (estFrancais) {
                bleuStr  = "Bleu  : Humain";
                rougeStr = "Rouge : Humain";
            } else {
                bleuStr  = "Blue  : Human";
                rougeStr = "Red   : Human";
            }
        } else if ("Joueur contre IA".equals(rawMode)) {
            String nivR = libelleNiveauIA(jeu.getNiveauIARouge());
            if (estFrancais) {
                bleuStr  = "Bleu  : Humain";
                rougeStr = "Rouge : " + nivR;
            } else {
                bleuStr  = "Blue  : Human";
                rougeStr = "Red   : " + nivR;
            }
        } else if ("IA contre IA".equals(rawMode)) {
            String nivB  = libelleNiveauIA(jeu.getNiveauIABleu());
            String nivR2 = libelleNiveauIA(jeu.getNiveauIARouge());
            if (estFrancais) {
                bleuStr  = "Bleu  : " + nivB;
                rougeStr = "Rouge : " + nivR2;
            } else {
                bleuStr  = "Blue  : " + nivB;
                rougeStr = "Red   : " + nivR2;
            }
        }else if ("ModeReseau".equals(rawMode)){
            if(estFrancais) {
                bleuStr  = "Vous êtes le joueur :";
                rougeStr  = (jeu.get_estRougeReseaux())? "Rouge" : "Bleu";
            }else{
                bleuStr  = "You are the player :";
                rougeStr = (jeu.get_estRougeReseaux())? "Red" : "Blue";
            }

        }else {
            bleuStr  = "";
            rougeStr = "";
        }
        g.drawString(bleuStr,  infoX, infoY + fm.getAscent());
        infoY += lineHeight;
        g.drawString(rougeStr, infoX, infoY + fm.getAscent());
    }


    private String libelleNiveauIA(int n) {
        switch (n) {
            case 1:
                return estFrancais ? "IA facile" : "Easy AI";
            case 2:
                return estFrancais ? "IA intermédiaire" : "Medium AI";
            case 3:
                return estFrancais ? "IA difficile" : "Difficult AI";
            default:
                return estFrancais ? "Humain" : "Human";
        }
    }


    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    private void updateCardSwapAnimation() {
        if (!isSwapping) return;

        long currentTime = System.currentTimeMillis();
        float progress = (currentTime - swapStartTime) / (float) SWAP_DURATION;

        if (progress >= 1.0f) {
            isSwapping = false;
            swapProgress = 0f;
            startPos = null;
            endPos = null;
        } else {
            swapProgress = progress;

            float easedProgress = (float) (1 - Math.pow(1 - progress, 3));
            swapProgress = easedProgress;
        }
        repaint();
    }

    /** Lance l’animation de swap de cartes */
    private void startSwapAnimation(int indexCarteSel, int indexCarteCentre) {
        this.swappingCardIndex = indexCarteSel;
        this.isSwapping = true;
        this.swapStartTime = System.currentTimeMillis();

        this.startPos = getPositionCarte(indexCarteSel);
        this.endPos = getPositionCarte(indexCarteCentre);

        if (swapAnimationTimer != null && swapAnimationTimer.isRunning())
            swapAnimationTimer.stop();

        swapAnimationTimer = new Timer(16, e -> {
            updateCardSwapAnimation();
            if (!isSwapping) ((Timer) e.getSource()).stop();
        });
        swapAnimationTimer.start();
    }

}