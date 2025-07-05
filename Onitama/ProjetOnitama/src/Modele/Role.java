package Modele;

/**
 * Énumération pour les différents rôles possibles
 */
public enum Role {
    ROI, PION;


    @Override
    public String toString() {return this == PION ? "Pion" : "Roi";}

    public static Role toRole(String str){
        if(str==null){return null;}
        switch (str){
            case "Pion":
                return PION;
            case "Roi":
                return ROI;
            default:
                return null;
        }
    }
}
