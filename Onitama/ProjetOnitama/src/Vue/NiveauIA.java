package Vue;

import Global.UCC;
import Vue.Adaptateurs.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.ImageObserver;
import java.io.FileInputStream;
import java.io.IOException;

public class NiveauIA extends JComponent {

    private final CollecteurEvenements collecteur;

    // Fond d'écran
    private String chemin_image_fond;
    private Image imageFond;

    // Couleurs des boutons
    private Color couleur_bouton;
    private Color couleur_survol;
    private Color couleur_pression;
    private Color couleur_conteur;

    // Composants de la vue
    private JLabel lblTitre;
    private BoutonArrondi btnfacile;
    private BoutonArrondi btnintermediaire;
    private BoutonArrondi btndifficile;
    private JLabel lblNote;



    public NiveauIA(CollecteurEvenements c , Theme theme) {
        this.collecteur = c;
        setLayout(null);

        this.chemin_image_fond = theme.getImgSelectionMode();
        chargerImageFond();

        this.couleur_bouton = theme.getCouleurBoutonNormal();
        this.couleur_survol = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur = theme.getCouleurBoutonConteur();

        // Titre au-dessus des boutons
        lblTitre = new JLabel("CHOIX DU NIVEAU D'IA", SwingConstants.CENTER);
        lblTitre.setForeground(Color.BLACK);
        lblTitre.setFont(new Font("Georgia", Font.BOLD, 24));
        add(lblTitre);

        lblNote = new JLabel("Note : l'IA est le joueur rouge", SwingConstants.CENTER);
        lblNote.setFont(new Font("Georgia", Font.ITALIC, 16));
        lblNote.setForeground(Color.DARK_GRAY);
        add(lblNote);


        // Création des boutons
        btnfacile = new BoutonArrondi("Facile",couleur_bouton,couleur_survol,
                couleur_pression, couleur_conteur );
        btnintermediaire = new BoutonArrondi("Intermédiaire",couleur_bouton,
                couleur_survol,couleur_pression, couleur_conteur );
        btndifficile = new BoutonArrondi("Difficile",couleur_bouton,
                couleur_survol,couleur_pression, couleur_conteur );

        // Association des Adaptateurs
        btnfacile.addActionListener(new Adaptateur_bouton_facile(collecteur));
        btnintermediaire.addActionListener(new Adaptateur_bouton_intermediaire(collecteur));
        btndifficile.addActionListener(new Adaptateur_bouton_difficile(collecteur));
        add(btnfacile);
        add(btnintermediaire);
        add(btndifficile);

        // Adaptation lors du redimensionnement
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionnerComposants();
            }
        });
    }

    private void chargerImageFond() {
        try {
            imageFond = ImageIO.read(UCC.ouvre(chemin_image_fond));
        } catch (IOException e) {
            imageFond = null;
            System.err.println("Erreur chargement fond : " + chemin_image_fond);
        }
    }

    private void positionnerComposants() {
        int w = getWidth(), h = getHeight();

        int btnW = (int)(w * 0.4), btnH = (int)(h * 0.1), gap = 20;

        int labelH = 30;
        int noteH  = 20;

        int totalH = labelH + gap
                + noteH + gap
                + 3 * btnH + 2 * gap;
        int startY = (h - totalH) / 2;
        int x      = (w - btnW) / 2;

        // titre
        int labelY = startY;
        lblTitre.setBounds(0, labelY, w, labelH);

        // note
        int yNote = labelY + labelH + gap;
        lblNote.setBounds(0, yNote, w, noteH);

        // boutons
        int y0 = yNote + noteH + gap;
        btnfacile        .setBounds(x, y0,                   btnW, btnH);
        btnintermediaire .setBounds(x, y0 + (btnH + gap),    btnW, btnH);
        btndifficile     .setBounds(x, y0 + 2*(btnH + gap),  btnW, btnH);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageFond != null) {
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), (ImageObserver) this);
        }
    }

    public void appliquerTheme(Theme theme) {

        this.chemin_image_fond = theme.getImgSelectionMode();
        chargerImageFond();

        lblTitre.setForeground(theme.getTextColor());
        lblNote .setForeground(theme.getTextColor());

        this.couleur_bouton   = theme.getCouleurBoutonNormal();
        this.couleur_survol   = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur  = theme.getCouleurBoutonConteur();

        // Mettre à jour les couleurs des boutons
        btnfacile.setCouleurs(couleur_bouton, couleur_survol, couleur_pression ,
                couleur_conteur);
        btnintermediaire.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        btndifficile.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        repaint();
    }

    public void changerLangue(boolean estFrancais) {
        if (estFrancais) {
            lblTitre.setText("CHOIX DU NIVEAU D'IA");
            lblNote .setText("Note : l'IA est le joueur rouge");
            btnfacile.setText("Facile");
            btnintermediaire.setText("Intermédiaire");
            btndifficile.setText("Difficile");
        } else {
            lblTitre.setText("CHOOSE AI LEVEL");
            lblNote .setText("Note: the AI is the red player");
            btnfacile.setText("Easy");
            btnintermediaire.setText("Intermediate");
            btndifficile.setText("Hard");
        }
        repaint();
    }

}
