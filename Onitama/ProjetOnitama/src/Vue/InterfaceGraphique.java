package Vue;

import Controleur.ControleurMediateur;
import javax.swing.*;
import java.awt.*;


/*** interface principale qui contient tous les pages */
public class InterfaceGraphique implements Runnable, VueJeu {

    private final ControleurMediateur controleur;
    private Theme theme = Theme.CLAIR;
    private JFrame fenetre;
    private JPanel contenu;
    private CardLayout layout;

    // tous les pages de l'application
    private Accueil accueil;
    private Parametres parametres;
    private TutorielOnitama tutoriel;
    private PlateauGraphique plateau;
    private SelectionMode modeSelection;
    private NiveauIA niveauIA;
    private DoubleNiveauIA niveauDouble;
    private PanneauVide panneauVide;
    private PlateauReseau plateauReseau;
    private SelectionModeReseau selectionModeReseau;

    // panneaux de selection de mode
    private JPanel PannauxMode;
    private CardLayout LayoutMode;

    // pour la page reseaux
    private boolean estClient = false;

    // constructeur
    public InterfaceGraphique(ControleurMediateur c) {
        this.controleur = c;
        this.controleur.setVue(this);
    }

    // initialisation de panneaux de selection ( 2 cotés )
    private void initSelcetionMode() {

        modeSelection = new SelectionMode(controleur, theme);
        PannauxMode = new JPanel(new CardLayout());
        LayoutMode = (CardLayout)PannauxMode.getLayout();

        niveauIA = new NiveauIA(controleur, theme);
        niveauDouble= new DoubleNiveauIA(controleur, theme);

        panneauVide = new PanneauVide(theme);
        plateauReseau = new PlateauReseau(theme, estClient , controleur);
        selectionModeReseau = new SelectionModeReseau(controleur, theme);

        // ligne de separation
        modeSelection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
                Color.BLACK));

        // ajout tous les pages
        PannauxMode.add(panneauVide, "EMPTY");
        PannauxMode.add(niveauIA, "Joueur contre IA");
        PannauxMode.add(niveauDouble, "IA contre IA");
        PannauxMode.add(plateauReseau, "Reseau");
        PannauxMode.add(selectionModeReseau, "ModeReseau");
        LayoutMode.show(PannauxMode, "EMPTY");
    }


    @Override
    public void run() {

        // Création du frame et du CardLayout
        fenetre = new JFrame("Onitama");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contenu = new JPanel(new CardLayout());
        layout = (CardLayout) contenu.getLayout();

        // creation des pages principales
        accueil = new Accueil(controleur, theme);
        plateau = new PlateauGraphique(controleur, controleur.getJeu(), theme);
        tutoriel = new TutorielOnitama(controleur);
        parametres = new Parametres(fenetre, controleur, theme);

        // creation de panneaux de selction de mode
        initSelcetionMode();
        JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                modeSelection, PannauxMode);

        //40% pour le coté des boutons choix de mode
        split.setResizeWeight(0.4);
        split.setOneTouchExpandable(true);
        split.setEnabled(false);
        split.setDividerSize(0);

        // Ajoute des pages au conteneur principal
        contenu.add(accueil, "accueil");
        contenu.add(plateau, "plateau");
        contenu.add(tutoriel, "tutoriel");
        contenu.add(split, "mode_selection");

        // Configuration de la fenêtre
        fenetre.setContentPane(contenu);
        fenetre.setSize(800, 600);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }

    public static void demarrer(ControleurMediateur c) {
        SwingUtilities.invokeLater(new InterfaceGraphique(c));
    }


    // pour les affichages
    @Override
    public void afficherMessage(String message, String titre) {
        JOptionPane.showMessageDialog(fenetre, message, titre, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void afficherErreur(String message, String titre) {
        JOptionPane.showMessageDialog(fenetre, message, titre, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String demanderTexte(String message, String titre) {
        return JOptionPane.showInputDialog(fenetre, message, titre, JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public void afficherParametres() {
        if (parametres != null) {
            parametres.setVisible(true);
        }
    }

    // changement de page
    @Override
    public void allerA(String nomVue) {
        layout.show(contenu, nomVue);
    }

    // changement de theme ( clair et sombre ) pour tous les pages
    @Override
    public void changerTheme(Boolean theme_clair) {

        if(theme_clair) {
            this.theme = Theme.CLAIR;
            accueil.appliquerTheme(Theme.CLAIR);
            plateau.appliquerTheme(Theme.CLAIR);
            modeSelection.appliquerTheme(Theme.CLAIR);
            parametres.appliquerTheme(Theme.CLAIR);
            niveauIA.appliquerTheme(Theme.CLAIR);
            niveauDouble.appliquerTheme(Theme.CLAIR);
            panneauVide.appliquerTheme(Theme.CLAIR);
            plateauReseau.appliquerTheme(Theme.CLAIR);
            selectionModeReseau.appliquerTheme(Theme.CLAIR);

        }else{
            this.theme = Theme.SOMBRE;
            accueil.appliquerTheme(Theme.SOMBRE);
            plateau.appliquerTheme(Theme.SOMBRE);
            modeSelection.appliquerTheme(Theme.SOMBRE);
            parametres.appliquerTheme(Theme.SOMBRE);
            niveauIA.appliquerTheme(Theme.SOMBRE);
            niveauDouble.appliquerTheme(Theme.SOMBRE);
            panneauVide.appliquerTheme(Theme.SOMBRE);
            plateauReseau.appliquerTheme(Theme.SOMBRE);
            selectionModeReseau.appliquerTheme(Theme.SOMBRE);
        }
    }

    // changement de langue pour tous les pages
    @Override
    public void changerLangue(Boolean est_francais){
        accueil.changerLangue(est_francais);
        plateau.changerLangue(est_francais);
        modeSelection.changerLangue(est_francais);
        parametres.changerLangue(est_francais);
        niveauIA.changerLangue(est_francais);
        niveauDouble.changerLangue(est_francais);
        panneauVide.changerLangue(est_francais);
        plateauReseau.changerLangue(est_francais);
        selectionModeReseau.changerLangue(est_francais);
    }

    // pour changement de l'effet de selection des cartes dans la vue (autre carte est selectonné)
    @Override
    public void desectionneCartes(){
        plateau.initCarteSelctionne();
    }

    // changement de 2eme coté de selction mode panneaux selon la bouton cliqué
    @Override
    public void changerPanneaxSelection(String page){
        LayoutMode.show(PannauxMode, page);
    }

    // pour effacer l'effet de selection sur les boutons ( dans mode ia vs ia )
    @Override
    public void reinitialiserCouleursBoutonsIAvsIA(){
        niveauDouble.reinitialiserCouleursBoutons();
    }

    // pour access direct dans le controleur
    @Override
    public PlateauGraphique getPlateauGraphique() { return plateau;}

    // pour Plateau Reseau ( si client ou serveur pas la meme page qui va etre )
    // si serveur affichage des infos , sionon demande des infos
    @Override
    public void setEstClient(boolean estClient) {
        plateauReseau.setEstClient(estClient);
    }

    // pour acces direct au plateauReseau
    @Override
    public PlateauReseau getPlateauReseau(){
        return plateauReseau;
    }

}
