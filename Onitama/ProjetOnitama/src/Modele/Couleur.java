package Modele;

/**
 * Énumération des couleurs possibles pour les pions
 */
public enum Couleur {
    ROUGE, BLEU;

    @Override
    public String toString() {
        switch (this){
            case ROUGE:
                return "Rouge";
            case BLEU:
                return "Bleu";
            default:
                return "null";
        }
    }

    public static Couleur toCouleur(String nom) {
        if(nom==null){
            return null;
        }
        switch (nom){
            case "ROUGE":
            case "Rouge":
                return ROUGE;
            case "BLEU":
            case "Bleu":
                return BLEU;
            default:
                return null;
        }
    }
}
