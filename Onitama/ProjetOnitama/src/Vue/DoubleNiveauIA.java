package Vue;

import Global.UCC;
import Vue.Adaptateurs.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class DoubleNiveauIA extends JPanel {
    private final CollecteurEvenements collecteur;

    // Image de fond
    private String cheminImageFond;
    private Image imageFond;

    // couleur des boutons
    private Color btnNormal, btnSurvol, btnPression, btnConteur, textColor;

    // text des boutons
    private final JLabel lblTitre = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblRouge = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblBleu  = new JLabel("", SwingConstants.CENTER);

    // les boutons
    private final BoutonArrondi btnRougeFacile, btnRougeInter, btnRougeDiff;
    private final BoutonArrondi btnBleuFacile, btnBleuInter, btnBleuDiff;

    private String niveauRouge = null, niveauBleu = null;
    private final Color couleurSelection = new Color(180, 10, 36);


    public DoubleNiveauIA(CollecteurEvenements c, Theme theme) {
        this.collecteur = c;
        setLayout(null);
        setOpaque(false);

        // Chargement du fond
        this.cheminImageFond = theme.getImgSelectionMode();
        try {
            imageFond = ImageIO.read(UCC.ouvre(cheminImageFond));
        } catch (IOException e) {
            imageFond = null;
            System.err.println("Impossible de charger : " + cheminImageFond);
        }

        // Couleurs
        btnNormal = theme.getCouleurBoutonNormal();
        btnSurvol = theme.getCouleurBoutonSurvol();
        btnPression = theme.getCouleurBoutonPression();
        btnConteur = theme.getCouleurBoutonConteur();
        textColor = theme.getTextColor();

        // titres
        lblTitre.setText("CHOIX DES NIVEAUX D'IA");
        lblRouge.setText("Joueur Rouge");
        lblBleu.setText("Joueur Bleu");

        for (JLabel lbl : new JLabel[]{lblTitre, lblRouge, lblBleu}) {
            lbl.setForeground(textColor);
            lbl.setFont(lbl == lblTitre ? new Font("Georgia", Font.BOLD, 24)
                    : new Font("Arial",  Font.BOLD, 19));
            add(lbl);
        }

        // listeners
        btnRougeFacile = makeLevelBtn("Facile", true,
                new Adaptateur_bouton_facile_rouge(collecteur));

        btnRougeInter = makeLevelBtn("Intermédiaire", true,
                new Adaptateur_bouton_intermediaire_rouge(collecteur));

        btnRougeDiff = makeLevelBtn("Difficile", true,
                new Adaptateur_bouton_difficile_rouge(collecteur));

        btnBleuFacile = makeLevelBtn("Facile", false,
                new Adaptateur_bouton_facile_bleu(collecteur));

        btnBleuInter = makeLevelBtn("Intermédiaire", false,
                new Adaptateur_bouton_intermediaire_bleu(collecteur));

        btnBleuDiff = makeLevelBtn("Difficile", false,
                new Adaptateur_bouton_difficile_bleu(collecteur)
        );

        // repositionnement
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionnerTous();
            }
        });
        repositionnerTous();
    }

    // creation des boutons
    private BoutonArrondi makeLevelBtn(String texte, boolean isRouge, ActionListener adaptateur) {
        BoutonArrondi b = new BoutonArrondi(texte, btnNormal, btnSurvol, btnPression, btnConteur);
        b.addActionListener(e -> {
            adaptateur.actionPerformed(e);
            if (isRouge) niveauRouge = texte;
            else niveauBleu = texte;
            surlignerSelection(b, isRouge); //surlignage
        });
        add(b);
        return b;
    }

    private void surlignerSelection(BoutonArrondi selectionne, boolean isRouge) {
        // lister les boutons de meme coté
        BoutonArrondi[] boutons = isRouge ? new BoutonArrondi[]{btnRougeFacile, btnRougeInter,
                btnRougeDiff} : new BoutonArrondi[]{btnBleuFacile, btnBleuInter, btnBleuDiff};

        // style
        for (BoutonArrondi b : boutons) {
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setCouleurs(btnNormal, btnSurvol, btnPression, btnConteur);
        }

        // surbrillance pour les boutons clique
        selectionne.setBorder(BorderFactory.createLineBorder(textColor, 2));
        selectionne.setCouleurs(couleurSelection, couleurSelection, couleurSelection, btnConteur);
        selectionne.repaint();
    }

    // positionnement des boutons
    private void repositionnerTous() {
        int W = getWidth(), H = getHeight();
        if (W <= 0 || H <= 0) return;

        int topMargin = 70;
        int sideMargin = (int)(W * 0.1);
        int interLabelMargin= 60;
        int labelToBtnMargin= (int)(H * 0.02);
        int bw = (int)(W * 0.35);
        int bh = (int)(H * 0.1);

        // Titre
        int titleH = (int)(H * 0.10);
        lblTitre.setBounds(sideMargin, topMargin,W - 2*sideMargin, titleH);

        // colonnes
        int startY = topMargin + titleH + interLabelMargin;
        int gapY = bh/2;

        // les boutons rouges
        int xR = sideMargin;
        lblRouge.setBounds(xR,startY - bh, bw, bh);
        btnRougeFacile.setBounds(xR, startY + labelToBtnMargin, bw, bh);
        btnRougeInter.setBounds(xR, startY + labelToBtnMargin + 1*(bh+gapY), bw, bh);
        btnRougeDiff.setBounds(xR, startY + labelToBtnMargin + 2*(bh+gapY), bw, bh);

        // les boutons bleus
        int xB = W - sideMargin - bw;
        lblBleu.setBounds(xB,startY - bh, bw, bh);
        btnBleuFacile.setBounds(xB, startY + labelToBtnMargin, bw, bh);
        btnBleuInter.setBounds(xB, startY + labelToBtnMargin + 1*(bh+gapY), bw, bh);
        btnBleuDiff.setBounds(xB, startY + labelToBtnMargin + 2*(bh+gapY), bw, bh);

        repaint();
    }


    //dessin
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageFond != null) {
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // changement de theme
    public void appliquerTheme(Theme theme) {

        // recuperer l'image de fond de theme
        this.cheminImageFond = theme.getImgSelectionMode();
        try {
            imageFond = ImageIO.read(UCC.ouvre(cheminImageFond));
        } catch (IOException ignored) {
            imageFond = null;
        }

        // couleurs de theme
        btnNormal = theme.getCouleurBoutonNormal();
        btnSurvol = theme.getCouleurBoutonSurvol();
        btnPression = theme.getCouleurBoutonPression();
        btnConteur = theme.getCouleurBoutonConteur();
        textColor = theme.getTextColor();

        // appliquer la couleur aux texts et boutons
        for (Component c : getComponents()) {

            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setForeground(textColor);
            }
            else if (c instanceof BoutonArrondi) {
                BoutonArrondi b = (BoutonArrondi) c;

                // si ce bouton est déjà sélectionné, on le laisse en surbrillance
                boolean selected = (b == btnRougeFacile && "Facile".equals(niveauRouge)) ||
                                (b == btnRougeInter && "Intermédiaire".equals(niveauRouge)) ||
                                (b == btnRougeDiff && "Difficile".equals(niveauRouge)) ||
                                (b == btnBleuFacile && "Facile".equals(niveauBleu)) ||
                                (b == btnBleuInter && "Intermédiaire".equals(niveauBleu)) ||
                                (b == btnBleuDiff && "Difficile".equals(niveauBleu));

                if (!selected) {
                    b.setCouleurs(btnNormal, btnSurvol, btnPression, btnConteur);
                    b.setForeground(textColor);
                }
            }
        }
        repaint();
    }

    // changement de langue
    public void changerLangue(boolean estFrancais) {

        // titre
        lblTitre.setText(estFrancais ? "CHOIX DES NIVEAUX D'IA" : "CHOOSE AI LEVELS");
        lblRouge.setText(estFrancais ? "Joueur Rouge" : "Red player");
        lblBleu .setText(estFrancais ? "Joueur Bleu" : "Blue player");

        String[] txt = estFrancais ? new String[]{"Facile","Intermédiaire","Difficile"}
                : new String[]{"Easy","Intermediate","Hard"};

        // text des boutons
        btnRougeFacile.setText(txt[0]);
        btnRougeInter.setText(txt[1]);
        btnRougeDiff.setText(txt[2]);
        btnBleuFacile.setText(txt[0]);
        btnBleuInter.setText(txt[1]);
        btnBleuDiff.setText(txt[2]);

        // surligner les sélections existantes
        if (niveauRouge != null) {

            BoutonArrondi sel;
            switch (niveauRouge) {
                case "Facile":
                case "Easy":
                    sel = btnRougeFacile;
                    break;
                case "Intermédiaire":
                case "Intermediate":
                    sel = btnRougeInter;
                    break;
                default:
                    sel = btnRougeDiff;
                    break;
            }
            surlignerSelection(sel, true);
        }

        if (niveauBleu != null) {

            BoutonArrondi sel;
            switch (niveauBleu) {
                case "Facile":
                case "Easy":
                    sel = btnBleuFacile;
                    break;
                case "Intermédiaire":
                case "Intermediate":
                    sel = btnBleuInter;
                    break;
                default:
                    sel = btnBleuDiff;
                    break;
            }
            surlignerSelection(sel, false);
        }

    }


    public void reinitialiserCouleursBoutons() {
        // On efface la selection des boutons
        niveauRouge = null;
        niveauBleu  = null;

        // les boutons rouge
        for (BoutonArrondi b : new BoutonArrondi[]{ btnRougeFacile, btnRougeInter, btnRougeDiff }) {
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setCouleurs(btnNormal, btnSurvol, btnPression, btnConteur);
            b.setForeground(textColor);
            b.repaint();
        }

        // les boutons bleu
        for (BoutonArrondi b : new BoutonArrondi[]{ btnBleuFacile, btnBleuInter, btnBleuDiff }) {
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setCouleurs(btnNormal, btnSurvol, btnPression, btnConteur);
            b.setForeground(textColor);
            b.repaint();
        }
    }

}
