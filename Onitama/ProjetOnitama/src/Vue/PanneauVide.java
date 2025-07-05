package Vue;


import Global.UCC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class PanneauVide extends JComponent {
    private String cheminImageFond;
    private Image imageFond;
    private JLabel lblMessage;
    private Theme theme;
    private boolean estFrancais;

    public PanneauVide(Theme theme) {
        this.theme = theme;
        this.estFrancais = true;
        setLayout(null);

        // Charge le fond
        this.cheminImageFond = theme.getImgSelectionMode();
        try {
            imageFond = ImageIO.read(UCC.ouvre(cheminImageFond));
        } catch (IOException e) {
            imageFond = null;
        }


        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Georgia", Font.BOLD, 26));
        lblMessage.setForeground(theme.getTextColor());
        add(lblMessage);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionner();
            }
        });

        updateText();
    }

    private void positionner() {
        int w = getWidth();
        int h = getHeight();
        int labelH = 40, labelW = w - 40;
        lblMessage.setBounds(20, (h - labelH)/2, labelW, labelH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageFond != null) {
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), (ImageObserver)this);
        }
    }


    private void updateText() {
        String fr = "Sélectionnez un mode de jeu !";
        String en = "Choose a game mode!";
        lblMessage.setText(estFrancais ? fr : en);
    }

    public void appliquerTheme(Theme theme) {
        this.theme = theme;
        // recharge le fond et la couleur du texte
        try {
            imageFond = ImageIO.read(UCC.ouvre(theme.getImgSelectionMode()));
        } catch (IOException e) {
            imageFond = null;
        }
        lblMessage.setForeground(theme.getTextColor());
        repaint();
    }

    public void changerLangue(boolean estFrancais) {
        this.estFrancais = estFrancais;
        updateText();
        repaint();
    }
}
