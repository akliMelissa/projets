package Vue;

import javax.swing.*;
import java.awt.*;

public class BoutonArrondi extends JButton {

    // style de la bouton
    private Color couleur_normale;
    private Color couleur_survol;
    private Color couleur_pression;
    private Color couleur_conteur;
    private int arc_largeur;
    private int arc_hauteur;

    public BoutonArrondi(String texte, Color couleur_normale,Color couleur_survol,Color couleur_pression,
                         Color couleur_conteur) {
        super(texte);
        this.couleur_normale = couleur_normale;
        this.couleur_survol = couleur_survol;
        this.couleur_pression = couleur_pression;
        this.couleur_conteur = couleur_conteur;
        this.arc_largeur = 25;
        this.arc_hauteur = 25;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Georgia", Font.BOLD, 20));
    }

    public BoutonArrondi(String texte,int tailleFont, Color couleur_normale,Color couleur_survol,
                         Color couleur_pression) {
        super(texte);
        this.couleur_normale = couleur_normale;
        this.couleur_survol = couleur_survol;
        this.couleur_pression = couleur_pression;
        this.arc_largeur = 25;
        this.arc_hauteur = 25;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Georgia", Font.BOLD, tailleFont));
    }

    public BoutonArrondi(int tailleFont , String texte, Color couleur_normale,Color couleur_survol,
                         Color couleur_pression, Color couleur_conteur) {
        super(texte);
        this.couleur_normale = couleur_normale;
        this.couleur_survol = couleur_survol;
        this.couleur_pression = couleur_pression;
        this.couleur_conteur = couleur_conteur;
        this.arc_largeur = 25;
        this.arc_hauteur = 25;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Georgia", Font.BOLD, tailleFont));
    }

    // changement des couleurs des boutons ( selon le theme )
    public void setCouleurs(Color normale, Color survol, Color pression , Color borderColor) {
        this.couleur_normale = normale;
        this.couleur_survol = survol;
        this.couleur_pression = pression;
        this.couleur_conteur = borderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // couleur selon l'etat de bouton
        if (getModel().isPressed()) {
            g2d.setColor(couleur_pression);
        } else if (getModel().isRollover()) {
            g2d.setColor(couleur_survol);
        } else {
            g2d.setColor(couleur_normale);
        }

        // fond arrondi
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc_largeur, arc_hauteur);

        // contour
        g2d.setColor(couleur_conteur);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc_largeur,
                arc_hauteur);

        // texte centre
        FontMetrics fm = g2d.getFontMetrics();
        int largeur_texte = fm.stringWidth(getText());
        int hauteur_texte = fm.getAscent();
        int x = (getWidth() - largeur_texte) / 2;
        int y = (getHeight() + hauteur_texte) / 2 - fm.getDescent();

        g2d.setColor(new Color(240, 240, 240));
        g2d.drawString(getText(), x, y);
        g2d.dispose();
    }

}
