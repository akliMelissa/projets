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


public class SelectionModeReseau extends JComponent {

    private final CollecteurEvenements collecteur;

    // Ressources & couleurs
    private String cheminImageFond;
    private Image  imageFond;
    private Color  coulNormal, coulHover, coulPress, coulBorder;

    // Composants
    private final JLabel        lblTitre;
    private final BoutonArrondi btnHeberger;
    private final BoutonArrondi btnRejoindre;

    public SelectionModeReseau(CollecteurEvenements c, Theme theme) {
        this.collecteur = c;
        setLayout(null);

        // Fond et couleurs
        this.cheminImageFond = theme.getImgSelectionMode();
        chargerImageFond();

        coulNormal = theme.getCouleurBoutonNormal();
        coulHover  = theme.getCouleurBoutonSurvol();
        coulPress  = theme.getCouleurBoutonPression();
        coulBorder = theme.getCouleurBoutonConteur();

        // Titre
        lblTitre = new JLabel("CHOIX DE MODE DE CONNEXION", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Georgia", Font.BOLD, 24));
        lblTitre.setForeground(theme.getTextColor());
        add(lblTitre);

        // Boutons
        btnHeberger  = new BoutonArrondi("Héberger",  coulNormal, coulHover, coulPress, coulBorder);
        btnRejoindre = new BoutonArrondi("Rejoindre", coulNormal, coulHover, coulPress, coulBorder);

        // Adaptateurs personnalisés
        btnHeberger .addActionListener(new Adaptateur_bouton_heberger (collecteur));
        btnRejoindre.addActionListener(new Adaptateur_bouton_rejoindre(collecteur));

        add(btnHeberger);
        add(btnRejoindre);

        // Re-layout sur redimensionnement
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { positionnerComposants(); }
        });
    }


    private void positionnerComposants() {
        int w = getWidth(), h = getHeight();
        int btnW = (int)(w * 0.4), btnH = (int)(h * 0.10), gap = 25;

        int titleH = 35;
        int totalH = titleH + gap + 2*btnH + gap;
        int startY = (h - totalH) / 2;
        int x      = (w - btnW) / 2;

        lblTitre    .setBounds(0,            startY,              w,    titleH);
        btnHeberger .setBounds(x,            startY + titleH + gap,      btnW, btnH);
        btnRejoindre.setBounds(x,            startY + titleH + gap + btnH + gap,
                btnW, btnH);
    }



    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageFond != null)
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), (ImageObserver)this);
    }


    private void chargerImageFond() {
        try { imageFond = ImageIO.read(UCC.ouvre(cheminImageFond)); }
        catch (IOException e) {
            imageFond = null;
            System.err.println("Erreur chargement fond : " + cheminImageFond);
        }
    }


    public void appliquerTheme(Theme theme) {
        cheminImageFond = theme.getImgSelectionMode();
        chargerImageFond();

        coulNormal = theme.getCouleurBoutonNormal();
        coulHover  = theme.getCouleurBoutonSurvol();
        coulPress  = theme.getCouleurBoutonPression();
        coulBorder = theme.getCouleurBoutonConteur();

        btnHeberger .setCouleurs(coulNormal, coulHover, coulPress, coulBorder);
        btnRejoindre.setCouleurs(coulNormal, coulHover, coulPress, coulBorder);
        lblTitre.setForeground(theme.getTextColor());

        repaint();
    }



    public void changerLangue(boolean fr) {
        if (fr) {
            lblTitre   .setText("CHOIX DE MODE DE CONNEXION");
            btnHeberger.setText("Héberger");
            btnRejoindre.setText("Rejoindre");
        } else {
            lblTitre   .setText("CONNECTION MODE");
            btnHeberger.setText("Host");
            btnRejoindre.setText("Join");
        }
        repaint();
    }
}
