package Vue;

import javax.swing.*;
import java.awt.*;


public class BoutonCercle extends JButton {

    private Image icon;
    private Color couleurNormale;
    private Color couleurSurvol;
    private Color couleurClique;
    private String infobulle;

    public BoutonCercle(Image icon, String infobulle) {
        this.icon = icon;
        this.infobulle = infobulle;
        configurer();
    }

    private void configurer() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setToolTipText(infobulle);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // chngement de la couleur des boutons
    public void setCouleurs(Color couleurNormale, Color couleurSurvol, Color couleurClique) {
        this.couleurNormale = couleurNormale;
        this.couleurSurvol = couleurSurvol;
        this.couleurClique = couleurClique;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessine le fond
        if (getModel().isPressed()) {
            g2d.setColor(couleurClique);
        } else if (getModel().isRollover()) {
            g2d.setColor(couleurSurvol);
        } else {
            g2d.setColor(couleurNormale);
        }
        g2d.fillOval(0, 0, getWidth(), getHeight());

        // dessine l'icone
        if (icon != null) {
            int padding = getModel().isRollover() ? 2 : 0;
            g2d.drawImage(icon, padding, padding, getWidth() - padding * 2,
                    getHeight() - padding * 2, this);
        }
    }


    @Override
    public boolean contains(int x, int y) {
        int rayon = Math.min(getWidth(), getHeight()) / 2;
        return Math.pow(x - getWidth()/2, 2) + Math.pow(y - getHeight()/2, 2) <= Math.pow(rayon, 2);
    }
}
