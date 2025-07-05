package Vue;

import javax.swing.*;
import java.awt.*;

public class Parametres extends JDialog {
    private final CollecteurEvenements collecteur;
    private Theme theme;
    private boolean estFrancais;
    private boolean sonActive;

    // --- Mise en page ---
    private static final int LARGEUR_FENETRE = 300;
    private static final int HAUTEUR_FENETRE = 400;
    private static final int HAUTEUR_BOUTON = 50;
    private static final int ESPACEMENT_VERTICAL = 20;
    private static final double POURCENTAGE_LARGEUR_BOUTON = 0.7;

    // Composants
    private BackgroundPanel panneauFond;
    private JLabel etiquetteTitre;
    private BoutonArrondi boutonTheme;
    private BoutonArrondi boutonLangue;
    private BoutonArrondi boutonSon;

    public Parametres(JFrame parent, CollecteurEvenements collecteur, Theme theme) {
        super(parent, "Paramètres", true);
        this.collecteur = collecteur;
        this.theme = theme;
        this.estFrancais = true;
        this.sonActive = false;

        initialiserFenetre();
        initialiserComposants();
        disposerComposants();
        ajouterEcouteurs();

        // Appliquer l'état initial
        appliquerTheme(theme);
        changerLangue(estFrancais);
    }

    private void initialiserFenetre() {
        setTitle(estFrancais ? "Paramètres" : "Settings");
        setSize(LARGEUR_FENETRE, HAUTEUR_FENETRE);
        setLocationRelativeTo(getParent());
        setResizable(false);

        panneauFond = new BackgroundPanel(theme.getImgParametres(), true);
        panneauFond.setLayout(null);
        setContentPane(panneauFond);
    }

    private void initialiserComposants() {
        etiquetteTitre = new JLabel("", SwingConstants.CENTER);
        etiquetteTitre.setFont(new Font("Georgia", Font.BOLD, 28));
        etiquetteTitre.setForeground(Color.WHITE);

        boutonTheme = new BoutonArrondi("", Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK);
        boutonLangue = new BoutonArrondi("", Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK);
        boutonSon = new BoutonArrondi("", Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK);
    }

    private void disposerComposants() {
        int y = 40;
        etiquetteTitre.setBounds(0, y, LARGEUR_FENETRE, 40);
        panneauFond.add(etiquetteTitre);
        y += 70;

        int largeurBouton = (int) (LARGEUR_FENETRE * POURCENTAGE_LARGEUR_BOUTON);
        int x = (LARGEUR_FENETRE - largeurBouton) / 2;

        boutonTheme.setBounds(x, y, largeurBouton, HAUTEUR_BOUTON);
        y += HAUTEUR_BOUTON + ESPACEMENT_VERTICAL;

        boutonLangue.setBounds(x, y, largeurBouton, HAUTEUR_BOUTON);
        y += HAUTEUR_BOUTON + ESPACEMENT_VERTICAL;

        boutonSon.setBounds(x, y, largeurBouton, HAUTEUR_BOUTON);

        panneauFond.add(boutonTheme);
        panneauFond.add(boutonLangue);
        panneauFond.add(boutonSon);
    }

    private void ajouterEcouteurs() {
        boutonTheme.addActionListener(e -> basculerTheme());
        boutonLangue.addActionListener(e -> basculerLangue());
        boutonSon.addActionListener(e -> basculerSon());
    }

    private void basculerTheme() {
        // Change le thème
        theme = (theme == Theme.CLAIR) ? Theme.SOMBRE : Theme.CLAIR;
        collecteur.clic_bouton_theme();
        appliquerTheme(theme);
    }

    private void basculerLangue() {
        // Change la langue
        estFrancais = !estFrancais;
        collecteur.clic_bouton_anglais();
        changerLangue(estFrancais);
    }

    private void basculerSon() {
        sonActive = !sonActive;
        collecteur.clic_bouton_son();
        changerLangue(estFrancais); // pour remettre le label Son
    }

    public void appliquerTheme(Theme theme) {

        // Met à jour le fond
        setTitle(estFrancais ? "Paramètres" : "Settings");
        panneauFond.setBackgroundImage(theme.getImgParametres());

        // Met à jour les couleurs des boutons
        Color normal = theme.getCouleurBoutonNormal();
        Color hover  = theme.getCouleurBoutonSurvol();
        Color press  = theme.getCouleurBoutonPression();
        Color border = theme.getCouleurBoutonConteur();
        boutonTheme.setCouleurs(normal, hover, press, border);
        boutonLangue.setCouleurs(normal, hover, press, border);
        boutonSon.setCouleurs(normal, hover, press, border);

        // Actualise les textes
        String t = estFrancais ? (theme == Theme.CLAIR ? "Thème: Clair" : "Thème: Sombre")
                : (theme == Theme.CLAIR ? "Theme: Light" : "Theme: Dark");

        boutonTheme.setText(t);

        revalidate();
        repaint();
    }


    public void changerLangue(boolean estFrancais) {

        // Titre
        this.estFrancais = estFrancais;
        setTitle(estFrancais ? "Paramètres" : "Settings");
        etiquetteTitre.setText(estFrancais ? "Paramètres" : "Settings");

        // Bouton langue
        boutonLangue.setText(estFrancais ? "Langue: Français" : "Language: English");

        //Bouton theme
        String themeTxt = estFrancais
                ? (theme == Theme.CLAIR ? "Thème: Clair" : "Thème: Sombre")
                : (theme == Theme.CLAIR ? "Theme: Light"  : "Theme: Dark");
        boutonTheme.setText(themeTxt);

        // Bouton son
        String sonTexteFR = sonActive ? "Son: Activé" : "Son: Désactivé";
        String sonTexteEN = sonActive ? "Sound: On" : "Sound: Off";
        boutonSon.setText(estFrancais ? sonTexteFR : sonTexteEN);

        revalidate();
        repaint();
    }

}
