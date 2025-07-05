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


public class SelectionMode extends JComponent {

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
    private BoutonArrondi btnIaVsIa;
    private BoutonArrondi btnHumainVsHumain;
    private BoutonArrondi btnIaVsHumain;
    private BoutonArrondi btnJouerADistance;
    private BoutonArrondi retourAlaccueil;

    public SelectionMode(CollecteurEvenements c , Theme theme) {
        this.collecteur = c;
        setLayout(null);

        this.chemin_image_fond = theme.getImgSelectionMode();
        chargerImageFond();

        this.couleur_bouton = theme.getCouleurBoutonNormal();
        this.couleur_survol = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur = theme.getCouleurBoutonConteur();

        // Titre au-dessus des boutons
        lblTitre = new JLabel("CHOIX DU MODE", SwingConstants.CENTER);
        lblTitre.setForeground(theme.getTextColor());
        lblTitre.setFont(new Font("Georgia", Font.BOLD, 24));
        add(lblTitre);

        // Création des boutons
        int tailleText = 16;
        btnIaVsIa = new BoutonArrondi(tailleText,  "IA vs IA",
                couleur_bouton,couleur_survol,
                couleur_pression , couleur_conteur );
        btnHumainVsHumain = new BoutonArrondi(tailleText, "Humain vs Humain",couleur_bouton,couleur_survol,
                couleur_pression , couleur_conteur );
        btnIaVsHumain = new BoutonArrondi(tailleText, "IA vs Humain",couleur_bouton,couleur_survol,
                couleur_pression , couleur_conteur );
        btnJouerADistance = new BoutonArrondi(tailleText, "Jouer en ligne",couleur_bouton,couleur_survol,
                couleur_pression , couleur_conteur );
        retourAlaccueil = new BoutonArrondi(tailleText, "Retour à l'accueil",couleur_bouton,couleur_survol,
                couleur_pression , couleur_conteur );

        // Association des Adaptateurs
        btnHumainVsHumain.addActionListener(new Adaptateur_choix_HumainVsHumain(collecteur));
        btnIaVsHumain.addActionListener(new Adaptateur_choix_IAvsHumain(collecteur));
        btnIaVsIa.addActionListener(new Adaptateur_choix_IAvsIA(collecteur));
        btnJouerADistance.addActionListener(new Adaptateur_choix_JouerADistance(collecteur));
        retourAlaccueil.addActionListener(new Adaptateur_bouton_quitter(collecteur));
        add(btnHumainVsHumain);
        add(btnIaVsHumain);
        add(btnIaVsIa);
        add(btnJouerADistance);
        add(retourAlaccueil);


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
        int w = getWidth();
        int h = getHeight();

        // Dimensions boutons
        int btnW = (int)(w *(0.7));
        int btnH = (int)(h *(0.1));
        int gap = 20;
        int totalH = 4 * btnH + 3 * gap;
        int startY = (h - totalH) / 2;
        int x = (w - btnW) / 2;

        // Positionner titre juste au-dessus du premier bouton avec marge
        int labelH = 30;
        int margin = gap;
        int labelY = startY - labelH - margin;
        lblTitre.setBounds(0, labelY, w, labelH);

        // Positionner boutons
        btnHumainVsHumain.setBounds(x, startY, btnW, btnH);
        btnIaVsHumain.setBounds(x, startY + (btnH + gap), btnW, btnH);
        btnIaVsIa.setBounds(x, startY + 2 * (btnH + gap), btnW, btnH);
        btnJouerADistance.setBounds(x, startY + 3 * (btnH + gap), btnW, btnH);
        retourAlaccueil.setBounds(x, startY + 4 * (btnH + gap), btnW, btnH);

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

        this.couleur_bouton   = theme.getCouleurBoutonNormal();
        this.couleur_survol   = theme.getCouleurBoutonSurvol();
        this.couleur_pression = theme.getCouleurBoutonPression();
        this.couleur_conteur  = theme.getCouleurBoutonConteur();

        // Mettre à jour les couleurs des boutons
        btnIaVsIa.setCouleurs(couleur_bouton, couleur_survol, couleur_pression ,
                couleur_conteur);
        btnHumainVsHumain.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        btnIaVsHumain.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        btnJouerADistance.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);
        retourAlaccueil.setCouleurs(couleur_bouton, couleur_survol, couleur_pression,
                couleur_conteur);

        repaint();
    }


    public void changerLangue(Boolean estFrancais) {

        if (estFrancais) {
            lblTitre.setText("CHOIX DU MODE");
            btnIaVsIa.setText("IA vs IA");
            btnHumainVsHumain.setText("Humain vs Humain");
            btnIaVsHumain.setText("IA vs Humain");
            btnJouerADistance.setText("Jouer en ligne");
            retourAlaccueil.setText("Retour à l'accueil");
        } else {
            lblTitre.setText("CHOOSE MODE");
            btnIaVsIa.setText("AI vs AI");
            btnHumainVsHumain.setText("Human vs Human");
            btnIaVsHumain.setText("AI vs Human");
            btnJouerADistance.setText("Play online");
            retourAlaccueil.setText("Back to Home");
        }
        repaint();
    }
}
