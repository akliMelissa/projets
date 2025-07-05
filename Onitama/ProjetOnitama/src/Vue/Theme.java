package Vue;

import java.awt.Color;

/**
 * Représente un thème global pour l'application,
 * comprenant les images de fond, les couleurs de boutons
 * et les chemins vers les images des pièces pour chaque vue.
 */

public class Theme {

    // Chemins vers les images de fond
    private final String imgAccueil;
    private final String imgSelectionMode;
    private final String imgPlateau;
    private final String imgParametres;

    // Couleurs des boutons
    private final Color textColor;
    private final Color couleurBoutonNormal;
    private final Color couleurBoutonSurvol;
    private final Color couleurBoutonPression;
    private final Color couleurBoutonConteur;

    // Chemins vers les images des pièces et indicateurs
    private final String pionBleu;
    private final String roiBleu;
    private final String roiRouge;
    private final String pionRouge;
    private final String positionRoiRouge;
    private final String positionRoiBleu;


    /*** Constructeur complet du thème.*/
    public Theme(
            String imgAccueil,
            String imgSelectionMode,
            String imgPlateau,
            String imgParametres,
            Color textColor,
            Color couleurBoutonNormal,
            Color couleurBoutonSurvol,
            Color couleurBoutonPression,
            Color couleurBoutonConteur,
            String pionBleu,
            String roiBleu,
            String roiRouge,
            String pionRouge,
            String positionRoiRouge,
            String positionRoiBleu
    ) {
        this.imgAccueil            = imgAccueil;
        this.imgSelectionMode      = imgSelectionMode;
        this.imgPlateau            = imgPlateau;
        this.imgParametres         = imgParametres;

        this.textColor = textColor;
        this.couleurBoutonNormal   = couleurBoutonNormal;
        this.couleurBoutonSurvol   = couleurBoutonSurvol;
        this.couleurBoutonPression = couleurBoutonPression;
        this.couleurBoutonConteur  = couleurBoutonConteur;

        this.pionBleu            = pionBleu;
        this.roiBleu             = roiBleu;
        this.roiRouge            = roiRouge;
        this.pionRouge           = pionRouge;
        this.positionRoiRouge    = positionRoiRouge;
        this.positionRoiBleu     = positionRoiBleu;
    }

    // Getters pour fonds et couleurs
    public String getImgAccueil()            { return imgAccueil; }
    public String getImgSelectionMode()      { return imgSelectionMode; }
    public String getImgPlateau()            { return imgPlateau; }
    public String getImgParametres()         { return imgParametres; }
    public Color  getCouleurBoutonNormal() { return couleurBoutonNormal; }
    public Color  getCouleurBoutonSurvol() { return couleurBoutonSurvol; }
    public Color  getCouleurBoutonPression() { return couleurBoutonPression; }
    public Color  getCouleurBoutonConteur() { return couleurBoutonConteur; }
    public Color getTextColor() { return textColor; }


    // Getters pour pièces et indicateurs

    public String getPionBleu()         { return pionBleu; }
    public String getRoiBleu()          { return roiBleu; }
    public String getRoiRouge()         { return roiRouge; }
    public String getPionRouge()        { return pionRouge; }
    public String getPositionRoiRouge() { return positionRoiRouge; }
    public String getPositionRoiBleu()  { return positionRoiBleu; }

    // Thèmes prédéfinis
    public static final Theme CLAIR = new Theme(
            "Images/BG_acceuil.png",
            "Images/bg_fin.png",
            "Images/bg_plateau2.png",
            "Images/bg_parametres.png",
            Color.BLACK,
            new Color(0, 0, 0, 203),
            new Color(80, 0, 0, 208),
            new Color(0, 0, 100, 200),
            new Color(195, 168, 83),
            "Images/pion_bleu.png",
            "Images/roi_bleu.png",
            "Images/roi_rouge_rm.png",
            "Images/pion_rouge.png",
            "Images/empleOnitama.png",
            "Images/empleOnitamaBLEU.png"
    );


    public static final Theme SOMBRE = new Theme(
            "Images/theme2/bg_acceuil.png",
            "Images/theme2/bg_sombre.png",
            "Images/theme2/bg_sombre.png",
            "Images/theme2/bg_sombre.png",
            Color.WHITE,
            new Color(0, 0, 0, 213),
            new Color(185, 10, 37, 255),
            new Color(0, 0, 100, 232),
            new Color(195, 168, 83, 237),
            "Images/pion_bleu.png",
            "Images/roi_bleu.png",
            "Images/roi_rouge_rm.png",
            "Images/pion_rouge.png",
            "Images/empleOnitama.png",
            "Images/empleOnitamaBLEU.png"
    );
}
