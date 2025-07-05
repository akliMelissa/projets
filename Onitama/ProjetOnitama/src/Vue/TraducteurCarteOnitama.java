package Vue;

public class TraducteurCarteOnitama {
    /**
     * Retourne le nom français d'une carte Onitama
     */

    public static String enFrancais(String nomAnglais) {
        switch (nomAnglais) {
            case "Tiger":
                return "Tigre";
            case "Dragon":
                return "Dragon";
            case "Frog":
                return "Grenouille";
            case "Rabbit":
                return "Lapin";
            case "Crab":
                return "Crabe";
            case "Elephant":
                return "Éléphant";
            case "Goose":
                return "Oie";
            case "Rooster":
                return "Coq";
            case "Monkey":
                return "Singe";
            case "Mantis":
                return "Mante";
            case "Horse":
                return "Cheval";
            case "Ox":
                return "Bœuf";
            case "Crane":
                return "Grue";
            case "Boar":
                return "Sanglier";
            case "Eel":
                return "Anguille";
            case "Cobra":
                return "Cobra";
            default:
                return nomAnglais;
        }
    }
}
