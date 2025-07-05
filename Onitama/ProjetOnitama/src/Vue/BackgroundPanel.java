package Vue;

import Global.UCC;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


/*** Panneau personnalisé */
public class BackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;  // image de fond
    private boolean isScaled = true;

    //Constructeur
    public BackgroundPanel(String imagePath, boolean isScaled) {

        this.isScaled = isScaled;
        setOpaque(false);
        setLayout(null);

        // charger l'image de fond
        try {
            backgroundImage = ImageIO.read(UCC.ouvre(imagePath));
        } catch (IOException e) {
            System.err.println("Erreur chargement image : " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            if (isScaled) {  //redimensionnement
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                int w = backgroundImage.getWidth();
                int h = backgroundImage.getHeight();

                for (int x = 0; x < getWidth(); x += w) {
                    for (int y = 0; y < getHeight(); y += h) {
                        g2d.drawImage(backgroundImage, x, y, null);
                    }
                }
            }

            g2d.dispose();
        }
    }

    //Changement de l'image de fond
    public void setBackgroundImage(String imagePath) {
        try { // lecteur d'image
            backgroundImage = ImageIO.read(UCC.ouvre(imagePath));
            repaint();
        } catch (IOException e) {
            System.err.println("Erreur chargement image : " + e.getMessage());
        }
    }

}
