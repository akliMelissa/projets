package Vue;

import Controleur.ControleurMediateur;
import Global.UCC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TutorielOnitama extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel pagesContainer;
    private final List<JPanel> pages;
    private int currentPageIndex = 0;
    private final JButton btnSuivant;
    private final JButton btnPrecedent;
    private final JButton btnQuitter;

    // Couleurs des boutons
    private Color couleur_bouton = new Color(0, 0, 0, 180);
    private Color couleur_survol = new Color(80, 0, 0, 200);
    private Color couleur_pression = new Color(0, 0, 100, 200);

    private String chemin_image_fond = "Images/tutoriel/bg_plateau1.png";
    private Image image_fond;

    public TutorielOnitama(ControleurMediateur controleur) {
        this.setLayout(new BorderLayout());
        charger_image_fond();
        this.pages = new ArrayList<>();
        this.cardLayout = new CardLayout();
        this.pagesContainer = new JPanel(cardLayout);
        pagesContainer.setOpaque(false);

        // Page 1 : Introduction narrative enrichie
        JPanel page1 = new JPanel(new BorderLayout());
        page1.setOpaque(false);

        JLabel titre = new JLabel("Onitama", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 32));
        titre.setForeground(Color.BLACK);
        titre.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        page1.add(titre, BorderLayout.NORTH);

        JTextArea texteIntro = new JTextArea("Sculpté dans les rochers des montagnes embrumées du vieux Japon réside l'Autel de l'Onitama.\n"+
                "Ce lieu de purification et de formation subtile de l'esprit accueille les écoles d'arts martiaux de tout le pays.\n\n"+
                "Les maîtres de ces écoles effectuent le voyage vers l'Onitama avec leurs disciples les plus prometteurs\n"+
                "afin d'affronter leurs adversaires dans cette enceinte sacrée et prouver leur supériorité.\n\n"+
                "Ces combats sont hors-normes car les esprits des animaux qui guident les combattants montrent leur voie aux maîtres\n" +
                "et soutiennent les élèves avec une force et une dextérité venues d'un autre monde.");
        texteIntro.setWrapStyleWord(true);
        texteIntro.setLineWrap(true);
        texteIntro.setEditable(false);
        texteIntro.setOpaque(false);
        texteIntro.setFont(new Font("Serif", Font.PLAIN, 18));
        texteIntro.setForeground(Color.BLACK);
        texteIntro.setMargin(new Insets(20, 20, 20, 20));
        page1.add(texteIntro, BorderLayout.CENTER);

        JTextArea infos = new JTextArea(" Auteur : Shimpei Sato\n"+
                "Développement : Groupe 4\n"+
                "Tutrice : Mme Dalloul Dima\n"+
                "Projet réalisé dans le cadre de l’UE PROG6 - L3 INFO"
        );
        infos.setEditable(false);
        infos.setOpaque(false);
        infos.setFont(new Font("SansSerif", Font.ITALIC, 14));
        infos.setForeground(Color.GRAY);
        infos.setMargin(new Insets(10, 20, 10, 20));
        page1.add(infos, BorderLayout.SOUTH);

        pages.add(page1);
        pagesContainer.add(page1, "page0");

        // Page 2 : Image tutorielle redimensionnée
        JPanel page2 = creerPageAvecImage("Images/tutoriel/1.png");
        pages.add(page2);
        pagesContainer.add(page2, "page1");

        // Page 3 : Image tutorielle redimensionnée
        JPanel page3 = creerPageAvecImage("Images/tutoriel/2.png");
        pages.add(page3);
        pagesContainer.add(page3, "page2");

        // Page 4 : Image tutorielle redimensionnée
        JPanel page4 = creerPageAvecImage("Images/tutoriel/3.png");
        pages.add(page4);
        pagesContainer.add(page4, "page3");

        // Barre de navigation bas
        JPanel barreNavigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrecedent = new BoutonArrondi("Précédent",15,couleur_bouton,couleur_survol,couleur_pression);
        btnSuivant = new BoutonArrondi("Suivant",15,couleur_bouton,couleur_survol,couleur_pression);
        btnQuitter = new BoutonArrondi("Quitter le tutoriel",15,couleur_bouton,couleur_survol,couleur_pression);

        btnPrecedent.addActionListener(e -> montrerPage(currentPageIndex - 1));
        btnSuivant.addActionListener(e -> montrerPage(currentPageIndex + 1));
        btnQuitter.addActionListener(e -> controleur.clic_bouton_quitter());

        barreNavigation.add(btnPrecedent);
        barreNavigation.add(btnSuivant);
        barreNavigation.add(btnQuitter);

        this.add(pagesContainer, BorderLayout.CENTER);
        this.add(barreNavigation, BorderLayout.SOUTH);

        mettreAJourBoutons();
        cardLayout.show(pagesContainer, "page0");
    }



    private JPanel creerPageAvecImage(String chemin) {
        return new JPanel(new BorderLayout()) {
            private Image image;
            {
                try {
                    image = ImageIO.read(UCC.ouvre(chemin));
                } catch (IOException e) {
                    image = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imgWidth = image.getWidth(null);
                    int imgHeight = image.getHeight(null);

                    double widthRatio = (double) panelWidth / imgWidth;
                    double heightRatio = (double) panelHeight / imgHeight;
                    double scale = Math.min(widthRatio, heightRatio);

                    int drawWidth = (int) (imgWidth * scale);
                    int drawHeight = (int) (imgHeight * scale);
                    int x = (panelWidth - drawWidth) / 2;
                    int y = (panelHeight - drawHeight) / 2;

                    g2.drawImage(image, x, y, drawWidth, drawHeight, this);
                }
            }
        };
    }


    private void charger_image_fond() {
        try {
            image_fond = ImageIO.read(UCC.ouvre(chemin_image_fond));
        } catch (IOException e) {
            image_fond = null;
            System.err.println("Impossible de charger l'image de fond : " + chemin_image_fond);
        }
    }


    private void montrerPage(int index) {
        if (index >= 0 && index < pages.size()) {
            currentPageIndex = index;
            cardLayout.show(pagesContainer, "page" + index);
            mettreAJourBoutons();
        }
    }


    private void mettreAJourBoutons() {
        btnPrecedent.setEnabled(currentPageIndex > 0);
        btnSuivant.setEnabled(currentPageIndex < pages.size() - 1);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image_fond != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(image_fond, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
