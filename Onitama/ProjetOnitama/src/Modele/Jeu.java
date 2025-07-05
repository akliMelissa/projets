package Modele;

import Global.UCC;
import Patterns.Observable;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Class permettant de définir le jeu
 * <P>
 *     Onitama est jeu deux joueur contenant deux pion
 *     Un Jeu est constituer d'un plateau avec des pion et des cartes de mouvement.
 *     Chaque joueur possède deux cartes et une 5ème carte est placée sur le coté(cette carte permet de définir qui commence la partie)
 * </P>
 */
public class Jeu extends Observable implements Serializable {
    private Pion[][] plateau;  //créé avec N
    private final Joueur[] joueurs;//joueurs[0]== joueur 1 (Rouge) et Joueur[1]==joueur 2 (Bleu)
    private boolean joueurCourant; //false == joueur1 et true == joueur2;
    private CarteJeu cartePartie;

    private Historique historique;
    private int commande_jouer=0;
    private int pion_capturer=0;

    private boolean peut_jouer =false;
    private CarteJeu[] AllCartes;
    private Pion ROIRouge;
    private Pion ROIBleu;
    private final ArrayList<Pion> Pion_Rouge;
    private final ArrayList<Pion> Pion_Bleu;
    private String modeJeu = "Joueur contre Joueur"; // Default mode
    private final List<Point> cases_jouables_tour_c1;
    private final List<Point> cases_jouables_tour_c2;

    private Point casePrecedente = null;

    private int niveauIARouge = 0;
    private int niveauIABleu  = 0;

    private boolean estRougeReseaux;

    //----------------méthode------------------//

    /**
     * Constructeur de Jeu
     */
    public Jeu(){
        Pion_Rouge=new ArrayList<>();
        Pion_Bleu=new ArrayList<>();
        joueurs=new Joueur[2];
        joueurs[0]=new Joueur(Couleur.ROUGE);
        joueurs[1]=new Joueur(Couleur.BLEU);
        initialiserPlateau();
        InitialiserCartes();
        historique=new Historique();
        cases_jouables_tour_c1= new ArrayList<>();
        cases_jouables_tour_c2= new ArrayList<>();

    }



    /**
     * Permet de Initialiser Les cartes
     */
    private void InitialiserCartes(){
        Scanner sc=new Scanner( UCC.ouvre(".Cartes.txt"));
        int nb_carte=sc.nextInt();
        AllCartes=new CarteJeu[nb_carte];
        sc.nextLine();
        sc.nextLine();
        String nom;
        String couleur;
        String line;

        for (int i=0;i<nb_carte;i++){
            nom=sc.nextLine();
            couleur=sc.nextLine();
            line=sc.nextLine();
            boolean [][] grilleDeplacements=new boolean[5][5];
            while(!line.isEmpty()){
                line=line.replaceAll("[()]","");
                String[] split = line.split(",");
                int y = Integer.parseInt(split[0]);
                int x = Integer.parseInt(split[1]);
                grilleDeplacements[y][x] = true;
                line=sc.nextLine();
            }

            AllCartes[i]=new CarteJeu(nom,grilleDeplacements,Couleur.toCouleur(couleur));
        }
        Distribuer_Carte();

    }//drive page 2

    /**
     * Permet de distribuer les cartes.
     */
    private void Distribuer_Carte(){
        List<CarteJeu> carteJeux = Arrays.asList(AllCartes);
        Collections.shuffle(carteJeux);

        joueurs[0].setCarte(carteJeux.get(0),0);
        joueurs[0].setCarte(carteJeux.get(1),1);
        joueurs[1].setCarte(carteJeux.get(2),0);
        joueurs[1].setCarte(carteJeux.get(3),1);
        cartePartie=carteJeux.get(4);

        joueurCourant= !cartePartie.estCouleurRouge();
    }

    /**
     * Initialiser le plateau de jeu
     */
    private void initialiserPlateau(){
        plateau = new Pion[5][5];
        Pion tmp;

        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                ROIBleu = new Pion(Role.ROI, Couleur.BLEU, new Point(i, 4));
                Pion_Bleu.add(ROIBleu);
                plateau[4][i] = ROIBleu;
                ROIRouge = new Pion(Role.ROI, Couleur.ROUGE, new Point(i, 0));
                Pion_Rouge.add(ROIRouge);
                plateau[0][i] = ROIRouge;
            } else {
                tmp = new Pion(Role.PION, Couleur.BLEU, new Point(i, 4));
                plateau[4][i] = tmp;
                Pion_Bleu.add(tmp);
                tmp = new Pion(Role.PION, Couleur.ROUGE, new Point(i, 0));
                plateau[0][i] = tmp;
                Pion_Rouge.add(tmp);
            }
        }

    } //a utiliser dans le constructeur pour les pions

    /**
     * Permet de lancer une nouvelle partie.
     */
    @SuppressWarnings("unused")
    public void nouvelle_partie(){
        Pion_Rouge.clear();
        Pion_Bleu.clear();
        casePrecedente=null;
        initialiserPlateau();
        Distribuer_Carte();
        historique=new Historique();
    }

    /**
     * Teste si le joueur Rouge a gagné ou non
     * @return vraie si Rouge gagne et false sinon
     */
    private boolean Testvictoire_Rouge(){return (!ROIBleu.estActif())||ROIRouge.getPosition().equals(new Point(2,4));}

    /**
     * Teste si le joueur Bleu a gagné ou non
     * @return vraie si Bleu gagne et false sinon
     */
    private boolean Testvictoire_Bleu(){return (!ROIRouge.estActif())||ROIBleu.getPosition().equals(new Point(2,0));}

    /**
     * Teste si le jeu est Terminé
     * @return retourne vraie si un des deux joueurs est gagnant,false sinon
     */
    public boolean estTermine(){return Testvictoire_Rouge()||Testvictoire_Bleu();}

    /**
     * Permet de déplacer les pièces d'un point A à un point B
     * Précondition Position toX et toY valide.
     * et pion doit être à la position fromX et fromY
     * @param fromX position du pion sur l'axe des abscisses.
     * @param fromY position du pion sur l'axe des ordonnées.
     * @param toX destination du pion sur l'axe des abscisses.
     * @param toY destination du pion sur l'axe des ordonnées.
     */
    public void deplacerPiece(int fromX, int fromY, int toX, int toY){

        if(plateau[toY][toX]!=null){
            plateau[toY][toX].capturer();
            pion_capturer=1;
        }else{
            pion_capturer=0;
        }
        plateau[toY][toX]=plateau[fromY][fromX];
        plateau[fromY][fromX].deplacer(toX,toY);
        plateau[fromY][fromX]=null;
    }

    /**
     * Vérifie si la position est dans le plateau.
     * @param x Position sur l'axe des abscisses.
     * @param y Position sur l'axe des ordonnées.
     * @return renvoie vraie si la position est dans le plateau, et faux sinon.
     */
    public boolean est_dans_terrain(int x, int y){
        return x>=0&&x<5&&y>=0&&y<5;
    }

    /**
     * Regarde si la position est dans le tableau et la pièce n'est pas de la même couleur
     * @param x Position sur l'axe des abscisses.
     * @param y Position sur l'axe des ordonnées.
     * @param CouleurJoueur couleur du joueur qui joue.
     * @return renvoie vraie si la position est dans le plateau et si la case sélectionnée n'est pas controlée par une des pièces du joueur
     */
    public boolean estPositionValide(int x, int y,Couleur CouleurJoueur){
        if(est_dans_terrain(x,y)){
            if(plateau[y][x]==null){
                return true;
            }else {
                return (CouleurJoueur!=plateau[y][x].getCouleur());
            }
        }
        return false;
    }

    /**
     * Donne la symétrie de central de x par rapport à 2.
     * @param i un entier.
     * @return la symétrie centrale de x.
     */
    public int symetrie(int i,int o){
        return 2*o-i;
    }

    /**
     * Permet de joue un coup.
     * @param fromX position sur l'accès des abscisses de la pièce à bouger.
     * @param fromY position sur l'accès des ordonnées de la pièce à bouger.
     * @param toX Position sur l'accès des abscisses, de la destination du pion.
     * @param toY Position sur l'accès des ordonnées, de la destination du pion.
     * @param carte Carte choisie par le joueur.
     * @return Vraie si le coup à réussir, et sinon faux.
     */
    @SuppressWarnings("unused")
    public boolean jouerCoup(int fromX, int fromY, int toX, int toY, CarteJeu carte) {
        if (!peut_jouer) {
            return false;
        }
        if(!est_dans_terrain(fromX,fromY)){
            return false;
        }

        if (plateau[fromY][fromX] == null) {
            return false;
        }

        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        Couleur couleurJoueur;
        if (joueurCourant) {
            couleurJoueur = Couleur.BLEU;

        } else {
            couleurJoueur = Couleur.ROUGE;
            deltaX = symetrie(deltaX ,0) ;
            deltaY = symetrie(deltaY ,0) ;
        }
        if (couleurJoueur != plateau[fromY][fromX].getCouleur()) {
            return false;
        }
        if (estPositionValide(toX, toY, couleurJoueur) && carte.deplacementPossible(deltaX, deltaY)) {
            setCasePrecedente(new Point(fromX, fromY));
            deplacerPiece(fromX, fromY, toX, toY);
            commande_jouer=commande_to_commande_coder(pion_capturer,fromX,fromY,toX,toY);
            swapCartes(carte);
            return true;
        }
        return false;
    }

    /**
     * méthode pour annuler un coup jouer
     * @param info information issue de l'historique
     */
    private void annuler_coup_jouer(int[] info){
        int joueur;
        ArrayList<Pion> pions;
        if(info[0]==1){
            deplacerPiece(info[6],info[7],info[4],info[5]);
            if(info[1]==1){
                pions=info[2]==0?Pion_Bleu:Pion_Rouge;

                for (Pion pion : pions) {
                    if (pion.getPosition().equals(new Point(info[6], info[7]))) {

                        pion.setActif();
                        plateau[info[7]][info[6]] = pion;
                        break;
                    }
                }
            }
        }
        CarteJeu tmp;
        joueur=info[2];

        tmp=joueurs[joueur].getCarte(info[3]);
        joueurs[joueur].setCarte(cartePartie,info[3]);
        cartePartie=tmp;
        metAJour();
    }

    /**
     * Permet de rafaire un coup
     * @param info un coup obtenu de l'historique
     */
    private void refaire_coup_jouer(int[] info){
        int joueur;
        if(info[0]==1){
            deplacerPiece(info[4],info[5],info[6],info[7]);
        }
        CarteJeu tmp;

        joueur=info[2];

        tmp=joueurs[joueur].getCarte(info[3]);
        joueurs[joueur].setCarte(cartePartie,info[3]);
        cartePartie=tmp;
    }

    /**
     * Vérifie si le joueur peu joué avec ses cartes et ses pions
     * @return faux si le joueur ne peut pas jouer un coup et vrai s'il peut jouer.
     */
    @SuppressWarnings("unused")
    public boolean tour_est_jouable() {
        CarteJeu[] main;
        main=joueurs[joueurCourant?1:0].getCartes();
        for(int i = 0;i<2;i++){
            CarteJeu carte=main[i];
            if(test_tout_coup_possible(carte)){
                peut_jouer =true;
                return true;
            }
        }
        return false;
    }

    /**
     * Test tous les coups possibles pour une carte
     * @param carte une des cartes du joueur.
     * @return vrai si un coup est possible pour la carte sinon fausse.
     */
    public boolean test_tout_coup_possible(CarteJeu carte) {
        boolean [][] movement_carte = carte.getGrilleDeplacements();
        int x,y;
        for(int i = 0; i<movement_carte.length; i++){
            for(int j = 0; j<movement_carte[0].length; j++){
                if(movement_carte[i][j]){
                    if(joueurCourant){
                        x=j;
                        y=i;
                    }else{
                        x=symetrie(j,2);
                        y=symetrie(i,2);
                    }
                    if(piece_est_jouable(x-2,y-2)){

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Regarde pour un mouvement si la pièce est jouable.
     * @param x position de la case a testé axe abscisse.
     * @param y position de la case a testé axe ordonné.
     * @return vrai si un des pions du joueur peu être joué.
     */
    public boolean piece_est_jouable(int x, int y) {
        Couleur couleur;
        ArrayList<Pion> pions;
        if(joueurCourant){
            couleur=Couleur.BLEU;
            pions=Pion_Bleu;
        }else {
            couleur=Couleur.ROUGE;
            pions=Pion_Rouge;
        }
        return enumeration(pions,couleur,x,y);
    }

    private boolean enumeration(ArrayList<Pion> pions,Couleur couleur,int x,int y){
        int deltaX,deltaY;
        for (Pion piece : pions) {
            deltaX= (int) (piece.getPosition().getX()+x);
            deltaY= (int) (piece.getPosition().getY()+y);
            if(estPositionValide(deltaX,deltaY,couleur)){
                return true;
            }
        }
        return false;
    }

    /**
     * Permet d'annuler un coup.
     */
    @SuppressWarnings("unused")
    public void annuler(){
        if(historique.peut_annuler()){
            int tmp=historique.annuler_coup();
            annuler_coup_jouer(historique_to_commande(tmp));
            joueurCourant=!joueurCourant;
            casePrecedente = null;
        }else {
            System.err.println("Impossible d'annuler pas assez de coup joué.");
        }
    }

    /**
     * Permet de refaire un coup.
     */
    @SuppressWarnings("unused")
    public void refaire(){
        if(historique.peut_refaire()){
            refaire_coup_jouer(historique_to_commande(historique.refaire_coup()));
            joueurCourant=!joueurCourant;
        }else {
            System.err.println("Impossible de refaire pas assez de coup annuler.");
        }
    }

    /**
     * Permet de transformer une commande en un entier pour être mis dans l'historique.
     * @param action correspond à l'action du joueur (0=jeter et 1=jouer).
     * @param carte La carte pioche depuis la main du joueur (0=1ᵉ carte de la main et 1=2ᵉ carte de la main).
     * @param joueur Le joueur qui joue le coup (0=joueur1 et 1=joueur2).
     * @param commande Le reste de la commande issue de commande_to_commande_coder.
     * @return un entier codant (action, pion_manger, joueur, carte, FromX, FromY, ToX, ToY).
     */
    private int commande_to_historique(int action,int carte,int joueur ,int commande){
        int resultat=commande;
        if(action!=0){
            resultat|=(1<<15);
        }
        if(joueur!=0){
            resultat|=(1<<13);
        }
        if(carte!=0){
            resultat|=(1<<12);
        }
        return resultat;
    }

    /**
     * Permet de capturer une commande en une version compacte.
     * @param pion_manger Si le pion est mangé ou non (1=mangé).
     * @param FromX coordonnée de départ sur la ligne des abscisses.
     * @param FromY coordonnée de départ sur la ligne des ordonnées.
     * @param ToX coordonnée d'arriver sur la ligne des abscisses.
     * @param ToY coordonnée d'arriver sur la ligne des ordonnées.
     * @return un entier qui regroupe toutes ces informations ci_dessus.
     */
    private int commande_to_commande_coder(int pion_manger,int FromX,int FromY,int ToX,int ToY){
        int resultat= (FromX << 9) | (FromY << 6) | (ToX << 3) | ToY;
        if(pion_manger != 0){
            resultat|=(1<<14);
        }

        return resultat;
    }

    /**
     * Permet de séparer toutes les informations suivantes en un tableau (action[0], pion_manger[1], joueur[2], carte[3], FromX[4], FromY[5], ToX[6], ToY[7]).
     * @param historique un entier codant (action, pion_manger, joueur, carte, FromX, FromY, ToX, ToY).
     * @return un tableau d'entier avec toutes les informations nécessaires.
     */
    private int[] historique_to_commande(int historique){
        int[] resultat=new int[8];
        resultat[0]=(historique>>15)&1;//action (0==Jeter et 1==jouer)
        resultat[1]=(historique>>14)&1;//pion_manger
        resultat[2]=(historique>>13)&1;//joueur
        resultat[3]=(historique>>12)&1;//carte
        resultat[4]=(historique>>9)& 0b111;//FromX
        resultat[5]=(historique>>6)& 0b111;//FromY
        resultat[6]=(historique>>3)& 0b111;//ToX
        resultat[7]=historique&0b111;//ToY
        return resultat;
    }

    /**
     * Permet d'obtenir le mode de jeu actuel
     * @return le mode de jeu
     */
    public String getModeJeu() {
        return modeJeu;
    }

    /**
     * Permet de définir le mode de jeu
     * @param modeJeu le nouveau mode de jeu
     */
    @SuppressWarnings("unused")
    public void setModeJeu(String modeJeu) {
        this.modeJeu = modeJeu;
    }

    /**
     * Permet de sauvegarder une partie dans le fichier {nom}
     * @param nom le nom du fichier.
     * @throws FileNotFoundException si le fichier ne peut être ouvert pas renvoie false
     * @return renvoie vraie si le fichier est sauvegardé et false sinon
     */
    public boolean sauvegarder(String nom) throws FileNotFoundException {

        try {
            // Ouvrir le fichier en écriture
            File file;

            if(!nom.contains(File.separator)){
                file = new File("Savefile"+File.separator+nom+".sav");
            }else{
                file = new File(nom+".sav");
            }

            // Créer les fichier nécésaire.
            File dossierParent = file.getParentFile();
            if (dossierParent != null && !dossierParent.exists()) {
                boolean created = dossierParent.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directory: " + dossierParent.getAbsolutePath());
                }
            }

            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            //Signature
            out.writeUTF("ONITAMA_SAVE_Version_1_2");

            //Sauvegarder le mode su jeu (Ia vs IA , Joueur vs Joueur ou Joueur vs Ia)
            out.writeUTF(modeJeu);

            //Sauvegarder les cases jouables à ce tour
            int taille_cases_jouables_tour_c1=cases_jouables_tour_c1.size();
            int taille_cases_jouables_tour_c2=cases_jouables_tour_c2.size();

            Point tmp;
            out.writeInt(taille_cases_jouables_tour_c1);
            for (int i = 0; i < taille_cases_jouables_tour_c1; i++) {
                tmp=cases_jouables_tour_c1.get(i);
                out.writeInt(tmp.x);
                out.writeInt(tmp.y);
            }


            out.writeInt(taille_cases_jouables_tour_c2);
            for (int i = 0; i < taille_cases_jouables_tour_c2; i++) {
                tmp=cases_jouables_tour_c2.get(i);
                out.writeInt(tmp.x);
                out.writeInt(tmp.y);
            }

            //Sauvegarder la case Precedent
            //On commence par sauvegarder si la casePrecedent est null ou non si oui on sauvegarde la case précédente.
            if(casePrecedente==null){
                out.writeBoolean(true);
            }else {
                out.writeBoolean(false);
                out.writeInt(casePrecedente.x);
                out.writeInt(casePrecedente.y);
            }

            //Sauvegarder le niveau de l'IA Rouge
            out.writeInt(niveauIARouge);

            //Sauvegarder le niveau de l'IA Bleu
            out.writeInt(niveauIABleu);

            // Sauvegarder le joueur courant
            out.writeBoolean(joueurCourant);
            // Sauvegarder les cartes du joueur 1
            joueurs[0].Sauvegarder(out);

            // Sauvegarder les cartes du joueur 2
            joueurs[1].Sauvegarder(out);


            // Sauvegarder la carte de partie
            cartePartie.Sauvegarder(out);

            // Sauvegarder tous les pions rouges
            out.writeInt(Pion_Rouge.size());

            for (Pion pion : Pion_Rouge) {
                pion.Sauvegarde(out);
            }

            // Sauvegarder tous les pions bleus
            out.writeInt(Pion_Bleu.size());
            for (Pion pion : Pion_Bleu) {
                pion.Sauvegarde(out);
            }


            // Sauvegarde de l’historique
            historique.Sauvegarder(out);

            // Sauvegarder l'état de jeu
            out.writeBoolean(peut_jouer);

            out.close();
            return true;
        } catch (IOException e) {
            System.err.println("Une Erreur : " + e.getMessage());
            return false;
        }
    }

    /**
     * Permet de charger une partie depuis le fichier {nom}
     * @param nom le nom du fichier
     * @return true s'il a chargé depuis le fichier {nom} sinon false
     * @throws FileNotFoundException si le fichier ne peu être ouvert renvoie false.
     */
    public boolean charger(String nom) throws FileNotFoundException {
        try {
            File file;

            if(!nom.contains(File.separator)){
                file = new File("Savefile"+File.separator+nom+".sav");
            }else {
                file = new File(nom+".sav");
            }

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            //Signature
            String Signature=in.readUTF();
            if(!Signature.equals("ONITAMA_SAVE_Version_1_2")){
                UCC.alerte("Sauvegarde incompatible");
                return false;
            }
            //Charger le mode su jeu (Ia vs IA , Joueur vs Joueur ou Joueur vs Ia)
            modeJeu=in.readUTF();

            //Charger les cases jouables à ce tour
            int taille_cases_jouables_tour_c1=in.readInt();
            cases_jouables_tour_c1.clear();
            for (int i = 0; i < taille_cases_jouables_tour_c1; i++) {
                cases_jouables_tour_c1.add(new Point(in.readInt(), in.readInt()));
            }

            int taille_cases_jouables_tour_c2=in.readInt();
            cases_jouables_tour_c2.clear();
            for (int i = 0; i < taille_cases_jouables_tour_c2; i++) {
                cases_jouables_tour_c2.add(new Point(in.readInt(), in.readInt()));
            }

            //Charger la case Precedent
            //On commence par charger si la casePrecedent est null ou non (vraie ou non) si oui on charge la case précédente.
            if(in.readBoolean()){
                casePrecedente=null;
            }else {
                casePrecedente=new Point(in.readInt(), in.readInt());
            }

            //Charger le niveau de l'IA Rouge
            niveauIARouge=in.readInt();
            //Charger le niveau de l'IA Bleu
            niveauIABleu=in.readInt();
            //Charger le joueur courant
            joueurCourant=in.readBoolean();

            //Charger les cartes du joueur 1
            joueurs[0]=new Joueur(Couleur.ROUGE);
            joueurs[0].Charger(in);

            //Charger les cartes du joueur 2
            joueurs[1]=new Joueur(Couleur.BLEU);
            joueurs[1].Charger(in);


            //Charger la carte de partie
            cartePartie=new CarteJeu(5,5);
            cartePartie.Charger(in);

            //Plateau mis à vide
            {
                int i=0;
                while (i<plateau.length) {
                    Arrays.fill(plateau[i], null);
                    i++;
                }
            }

            //Charger tous les pions rouges
            int nb_pion=in.readInt();

            Pion_Rouge.clear();
            Pion pion;
            Point tmp;
            for (int i=0;i<nb_pion;i++) {
                pion=new Pion(null,null,new Point());
                pion.charger(in);
                Pion_Rouge.add(pion);
                if(pion.getRole()==Role.ROI){
                    ROIRouge=pion;
                }
                if(pion.estActif()){
                    tmp=pion.getPosition();
                    plateau[tmp.y][tmp.x]=pion;
                }
            }

            //Charger tous les pions bleus
            nb_pion=in.readInt();
            Pion_Bleu.clear();
            for (int i=0;i<nb_pion;i++) {
                pion=new Pion(null,null,new Point());
                pion.charger(in);
                Pion_Bleu.add(pion);
                if(pion.getRole()==Role.ROI){
                    ROIBleu=pion;
                }
                if(pion.estActif()){
                    tmp=pion.getPosition();
                    plateau[tmp.y][tmp.x]=pion;
                }
            }


            //Charger de l’historique
            historique.charger(in);

            //Charger l'état de jeu
            peut_jouer=in.readBoolean();

            Map<String,CarteJeu> indexCartes = new HashMap<>();
            for (CarteJeu c : AllCartes) {
                indexCartes.put(c.getNomCarte(), c);
            }

// 2. Pour chaque joueur, replacez leur carte chargée par l'instance canonique
            for (int j = 0; j < 2; j++) {
                for (int pos = 0; pos < 2; pos++) {
                    CarteJeu chargée = joueurs[j].getCarte(pos);
                    CarteJeu canonique = indexCartes.get(chargée.getNomCarte());
                    if (canonique != null) {
                        joueurs[j].setCarte(canonique, pos);
                    } else {
                        System.err.println("Carte non trouvée dans AllCartes : "
                                + chargée.getNomCarte());
                    }
                }
            }

            // 3. Refaire aussi pour la carte centrale (cartePartie)
            CarteJeu partieChargée = cartePartie;
            CarteJeu partieCanonique = indexCartes.get(partieChargée.getNomCarte());
            if (partieCanonique != null) {
                cartePartie = partieCanonique;
            } else {
                System.err.println("Carte de partie non trouvée : "
                        + partieChargée.getNomCarte());
            }



            in.close();
            return true;
        } catch (IOException e) {
            System.out.println("Une Erreur : " + e.getMessage());
            return false;
        }
    }


    /**
     * Permet de swap une carte entre le joueur qui joue et celui qui est sur le côté.
     * @param carte carte choisie par le joueur.
     */
    @SuppressWarnings("unused")
    public void swapCartes(CarteJeu carte){
        int position;
        int joueur=joueurCourant?1:0;
        position=joueurs[joueur].getpositionCarte(carte);
        joueurs[joueur].setCarte(cartePartie,position);
        if(commande_jouer==0){
            historique.coup_jouer(commande_to_historique(0,position,joueur,commande_jouer));
        }else {
            historique.coup_jouer(commande_to_historique(1,position,joueur,commande_jouer));
        }
        joueurCourant=!joueurCourant;
        commande_jouer=0;
        pion_capturer=0;
        peut_jouer=false;
        cartePartie=carte;

    }//a utiliser dans jouer avec les cartes des 2joueurs+CP

    /**
     * Permet d'obtenir le gagnant
     * Précondition la partie est terminé.
     * @return ROUGE ou BLEU {@link Couleur}
     */
    @SuppressWarnings("unused")
    public Couleur getGagnant(){
        if(Testvictoire_Rouge()){
            return Couleur.ROUGE;
        }else{
            return Couleur.BLEU;
        }
    }

    //get and set + tostring.

    /**
     * Permet d'obtenir le tableau.
     * @return un tableau de pion.
     */
    @SuppressWarnings("unused")
    public Pion[][] getPlateau(){
        return plateau;
    }

    /**
     * Permet d'obtenir le joueur_1 {@link Joueur}
     * @return un Joueur
     */
    @SuppressWarnings("unused")
    public Joueur getJoueur1(){
        return joueurs[0];
    }

    /**
     * Permet d'obtenir le joueur_2 {@link Joueur}
     * @return un Joueur
     */
    @SuppressWarnings("unused")
    public Joueur getJoueur2(){
        return joueurs[1];
    }

    /**
     * Permet d'obtenir tous les joueurs {@link Joueur}
     * @return tableau de joueur
     */
    @SuppressWarnings("unused")
    public Joueur[] getJoueurs(){
        return joueurs;
    }

    /**
     * Permet d'obtenir la carte mise sur le côté.
     * @return une carte {@link CarteJeu}
     */
    @SuppressWarnings("unused")
    public CarteJeu getCartePARTIE(){
        return cartePartie;
    }

    /**
     * Permet de savoir qui joue.
     * @return vraie si joueur 2 joue et faux si c'est le joueur 1
     */
    public boolean getjoueurCourant(){
        return joueurCourant;
    }

    /**
     * Permet de récupérer toutes les cartes du jeu.
     * @return tableau de carte
     */
    @SuppressWarnings("unused")
    public CarteJeu[] getCartes() {
        return AllCartes;
    }

    /**
     * Permet d'obtenir historique
     * @return historique.
     */
    @SuppressWarnings("unused")
    public Historique get_historique(){
        return historique;
    }

    @SuppressWarnings("unused")
    public void afficherCommandeDecodee(int historique) {
        int[] champs = historique_to_commande(historique);

        String actionStr = (champs[0] == 0) ? "Jeter" : "Jouer";
        String pionMangerStr = (champs[1] == 0) ? "Non" : "Oui";
        String joueurStr = (champs[2] == 0) ? "Joueur 1" : "Joueur 2";
        String carteStr = (champs[3] == 0) ? "Carte 1" : "Carte 2";

        System.out.println("===== Décodage de la commande =====");
        System.out.println("Action       : " + actionStr);
        System.out.println("Pion mangé   : " + pionMangerStr);
        System.out.println("Joueur       : " + joueurStr);
        System.out.println("Carte jouée  : " + carteStr);
        System.out.println("Départ (X,Y) : (" + champs[4] + ", " + champs[5] + ")");
        System.out.println("Arrivée (X,Y): (" + champs[6] + ", " + champs[7] + ")");
        System.out.println("===================================");
    }


    /**
     * Permet d'obtenir une version string du jeu.
     * @return String.
     */
    @Override
    public String toString() {
        int nbtabulation=0;
        StringBuilder result=new StringBuilder();

        result.append("partie Terminer = ");
        result.append(estTermine());
        result.append("\n");
        result.append("tour du joueur : ");
        if (getjoueurCourant()){
            result.append("joueur2\n");
        }else {
            result.append("joueur1\n");
        }
        result.append("carte de coté :\n");
        result.append(cartePartie.toString());
        result.append("\nCarte du joueur1 (Rouge) :\n");
        toStringCarte_inverse(result, joueurs[0].getCarte(0), joueurs[0].getCarte(1));
        result.append("\nPlateau : \n");
        result.append(" j 0 1 2 3 4\n");
        result.append("i\n");
        for (int i=0;i<5;i++){
            result.append(i).append("  ");
            for (int j=0;j<5;j++){

                if(plateau[i][j]==null){
                    result.append(".");
                }else{
                    if(plateau[i][j].getCouleur()==Couleur.ROUGE) {
                        if (plateau[i][j].getRole() == Role.ROI) {
                            result.append("R");
                        } else {
                            result.append("r");
                        }
                    }else if(plateau[i][j].getCouleur()==Couleur.BLEU){
                        if (plateau[i][j].getRole() == Role.ROI) {
                            result.append("B");
                        }else{
                            result.append("b");
                        }
                    }
                    else{
                        result.append("?");
                    }
                }
                if(j!=4){result.append(" ");}else{result.append("\n");}
            }
        }

        result.append("\nCarte du joueur2 (Bleu):\n");
        toStringCarte(nbtabulation, result, joueurs[1].getCarte(0), joueurs[1].getCarte(1));
        return result.toString();
    }



    private void toStringCarte(int nbtabulation, StringBuilder result, CarteJeu carte1, CarteJeu carte2) {
        String[] carte1String = (carte1 != null) ? carte1.toString().split("\n") : new String[]{"[Carte null]"};
        String[] carte2String = (carte2 != null) ? carte2.toString().split("\n") : new String[]{"[Carte null]"};
        int i = 0;
        while(i<6){
            if(i==0){
                result.append(carte1String[i]).append("\t");
                nbtabulation=(carte1String[i].length()-9)/4+1;
                result.append(carte2String[i]).append("\n");
            }else {
                result.append(carte1String[i]);
                result.append("\t".repeat(Math.max(0, nbtabulation + 1)));
                result.append(carte2String[i]).append("\n");
            }

            i++;
        }
    }

    private void toStringCarte_inverse(StringBuilder result, CarteJeu carte1, CarteJeu carte2) {
        String[] carte1String=(carte1 != null) ? carte1.toString().split("\n") : new String[]{"[Carte null]"};
        String[] carte2String=(carte2 != null) ? carte2.toString().split("\n") : new String[]{"[Carte null]"};
        result.append(carte1String[0]).append("\t");
        int nbtabulation = (carte1String[0].length() - 9) / 4 + 1;
        result.append(carte2String[0]).append("\n");

        int i = 5;

        while(i>0){
            result.append(inverserString(carte1String[i]));
            result.append("\t".repeat(Math.max(0, nbtabulation + 1)));
            result.append(inverserString(carte2String[i])).append("\n");


            i--;
        }
    }

    private String inverserString(String input) {
        return new StringBuilder(input).reverse().toString();
    }


    /* zone pour les fonctions de tests des fonctions privée*/
    @SuppressWarnings("unused")
    public void set_plateau(Pion[][] pion,Pion Roi_rouge,Pion ROIBleu){
        plateau=pion;
        this.ROIRouge=Roi_rouge;
        this.ROIBleu=ROIBleu;
        Pion_Rouge.clear();
        Pion_Bleu.clear();
        Pion_Rouge.add(Roi_rouge);
        Pion_Bleu.add(ROIBleu);
    }

    @SuppressWarnings("unused")
    public void set_joueurCourant(boolean joueurCourant){
        this.joueurCourant=joueurCourant;
    }

    public void set_est_jouable(boolean estJouable){
        this.peut_jouer=estJouable;
    }

    @SuppressWarnings("unused")
    public void set_un_Pion_Rouge(Pion pion){
        Pion_Rouge.add(pion);
    }

    @SuppressWarnings("unused")
    public void set_un_Pion_Bleu(Pion pion){
        Pion_Bleu.add(pion);
    }


    // --- POUR L'IA  ---
    @Override
    public Jeu clone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Jeu clone = (Jeu) ois.readObject();
            ois.close();
            return clone;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur lors du clonage du jeu : " + e, e);
        }
    }

    public Point getCasePrecedente() {
        return casePrecedente;
    }

    public void setCasePrecedente(Point p) {
        this.casePrecedente = p;
    }

    public ArrayList<Pion> getPion_Rouge() {
        return Pion_Rouge;
    }
    public ArrayList<Pion> getPion_Bleu(){
        return Pion_Bleu;
    }

    public Pion getROIRouge(){
        for (Pion pion : Pion_Rouge){
            if(pion.getRole()==Role.ROI){
                return pion;
            }
        }
        return null;
    }


    public Pion getROIBleu(){
        for (Pion pion : Pion_Bleu){
            if(pion.getRole()==Role.ROI){
                return pion;
            }
        }
        return null;
    }

    public int getNiveauIARouge() { return niveauIARouge; }
    public int getNiveauIABleu()  { return niveauIABleu;  }

    public void setNiveauIARouge(int n) { niveauIARouge = n; }
    public void setNiveauIABleu (int n) { niveauIABleu  = n; }



    // __________ ajouter pour les effets avec la vue _____________________

    public void trouverToutesCasesJouables(Point pos) {
        init_cases_jouables_tour_c1();
        init_cases_jouables_tour_c2();

        boolean joueurCourant = getjoueurCourant();
        Couleur coul = joueurCourant ? Couleur.BLEU : Couleur.ROUGE;

        CarteJeu c1 = joueurCourant
                ? getJoueur2().getCarte(0)
                : getJoueur1().getCarte(0);
        CarteJeu c2 = joueurCourant
                ? getJoueur2().getCarte(1)
                : getJoueur1().getCarte(1);


            for (Point delta : c1.getDeplacementsRelatifs()) {
                int dx = delta.x, dy = delta.y;
                if (coul == Couleur.BLEU) { dx = -dx; dy = -dy; }
                int cx = pos.x - dx, cy = pos.y - dy;
                if (est_dans_terrain(cx, cy)) {
                    Pion cible = plateau[cy][cx];
                    if (cible == null || cible.getCouleur() != coul) {
                        cases_jouables_tour_c1.add(new Point(cx, cy));
                    }
                }
            }


            for (Point delta : c2.getDeplacementsRelatifs()) {
                int dx = delta.x, dy = delta.y;
                if (coul == Couleur.BLEU) { dx = -dx; dy = -dy; }
                int cx = pos.x - dx, cy = pos.y - dy;
                if (est_dans_terrain(cx, cy)) {
                    Pion cible = plateau[cy][cx];
                    if (cible == null || cible.getCouleur() != coul) {
                        cases_jouables_tour_c2.add(new Point(cx, cy));
                    }
                }
            }


    }



    public void trouverCasesJouables(CarteJeu carte, Point position_curr) {

        // vider la liste avant de commencer
        init_cases_jouables_tour_c1();
        init_cases_jouables_tour_c2();

        Joueur J = (getjoueurCourant())? getJoueur2(): getJoueur1();
        boolean estc1;
       if (J.getCarte(0)==carte){
           estc1 =true;
       }else{
           estc1 =false;

       }

        //déterminer la couleur du joueur actif
        Couleur couleurJoueur = joueurCourant ? Couleur.BLEU : Couleur.ROUGE;

        //récupérer la liste des déplacements relatifs de la carte
        List<Point> deplacementsRelatifs = carte.getDeplacementsRelatifs();

        // pour chaque déplacement relatif, calculer la cible
        for (Point delta : deplacementsRelatifs) {
            int dx = delta.x;
            int dy = delta.y;

            // inversion pour le joueur blue puisque il est en bas -movement
            if (couleurJoueur == Couleur.BLEU) {
                dx = -dx;
                dy = -dy;
            }

            int colDest = position_curr.x - dx;
            int ligDest = position_curr.y - dy;

            // vérifier terrain et occupation
            if (est_dans_terrain (colDest , ligDest)) {
                Pion cible = plateau[ligDest][colDest];

                //case vide ou occupée par l'adversaire
                if (cible == null || cible.getCouleur() != couleurJoueur) {
                    if(estc1){
                        cases_jouables_tour_c1.add(new Point(colDest, ligDest));
                    }else{
                        cases_jouables_tour_c2.add(new Point(colDest, ligDest));
                    }
                }
            }
        }
    }


    public List<Point> get_cases_jouables_tour_c1(){
        return cases_jouables_tour_c1;
    }
    public List<Point> get_cases_jouables_tour_c2(){
        return cases_jouables_tour_c2;

    }
    public void init_cases_jouables_tour_c1(){
        cases_jouables_tour_c1.clear();
    }
    public void init_cases_jouables_tour_c2(){
        cases_jouables_tour_c2.clear();
    }







    /******************************* Réseaux ******************************************************************/
    /**********************************************************************************************************/

    /** Retourne l’index (0-15) de la carte passée, selon AllCartes. */
    public byte indexCarte(CarteJeu c) {
        for (byte i = 0; i < AllCartes.length; i++) {
            if (AllCartes[i] == c) return i;      // comparaison par RÉFÉRENCE
        }
        throw new IllegalArgumentException("Carte inconnue !");
    }

    /**********************************************************************************************************/
    /**********************************************************************************************************/


    /** Donne la carte locale à partir de l’index reçu du réseau. */
    public CarteJeu carteParIndex(byte idx) {
        if (idx < 0 || idx >= AllCartes.length)
            throw new IllegalArgumentException("Idx hors-plage : " + idx);
        return AllCartes[idx];
    }


    /**********************************************************************************************************/
    public void copyFrom(Jeu other) {

        /* ---------- Plateau ---------------------------------------------------- */
        if (this.plateau == null) this.plateau = new Pion[5][5];

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Pion src = other.plateau[y][x];
                this.plateau[y][x] = (src == null) ? null
                        : new Pion(src);   // constructeur de copie
            }
        }

        /* ---------- 2. Joueurs ---------------------------------------------------- */
        //  - le tableau existe déjà (créé dans le constructeur)
        //  - chaque Joueur possède une méthode copyFrom(Joueur)
        for (int i = 0; i < 2; i++) {
            this.joueurs[i].copyFrom(other.joueurs[i]);
        }
        System.out.println("Main joueur1 : " + Arrays.toString(this.getJoueur1().getCartes()));
        System.out.println("Main joueur2 : " + Arrays.toString(this.getJoueur2().getCartes()));


        /* ---------- 3. Champs scalaires ------------------------------------------ */
        this.joueurCourant = other.joueurCourant;
        this.cartePartie   = other.cartePartie;          // cartes immuables ⇒ référence OK
        this.commande_jouer = other.commande_jouer;
        this.pion_capturer  = other.pion_capturer;
        this.peut_jouer     = other.peut_jouer;
        this.modeJeu        = other.modeJeu;
        this.niveauIARouge  = other.niveauIARouge;
        this.niveauIABleu   = other.niveauIABleu;
        this.casePrecedente = (other.casePrecedente == null)
                ? null : new Point(other.casePrecedente);

        /* ---------- 4. Historique ------------------------------------------------- */
        if (this.historique == null)
            this.historique = new Historique();
        this.historique.copyFrom(other.historique);      // ou = new Historique(other)

        /* ---------- 5. Tableau des cartes ---------------------------------------- */
        this.AllCartes = Arrays.copyOf(other.AllCartes, other.AllCartes.length);

        /* ---------- 6. Rois et listes de pions ----------------------------------- */
        this.ROIRouge = new Pion(other.ROIRouge);
        this.ROIBleu  = new Pion(other.ROIBleu);

        this.Pion_Rouge.clear();
        for (Pion p : other.Pion_Rouge) this.Pion_Rouge.add(new Pion(p));

        this.Pion_Bleu.clear();
        for (Pion p : other.Pion_Bleu)  this.Pion_Bleu.add(new Pion(p));

        /* ---------- 7. Cases jouables du tour ------------------------------------ */
        this.cases_jouables_tour_c1.clear();            // la liste est finale, on la ré-utilise
        this.cases_jouables_tour_c1.addAll(other.cases_jouables_tour_c1);
        this.cases_jouables_tour_c2.clear();            // la liste est finale, on la ré-utilise
        this.cases_jouables_tour_c2.addAll(other.cases_jouables_tour_c2);



    }
    /**********************************************************************************************************/
    /**********************************************************************************************************/

    public synchronized boolean apply(Multijoueur.MoveCmd m) {
        this.tour_est_jouable();           // met peut_jouer = true (ou false…)

        // Plus de vérification sur le joueur, on fait confiance à l'ordre FIFO
        CarteJeu carte = carteParIndex(m.getCi());
        System.out.printf(
                "[DEBUG jouerCoup] courant=%s from=(%d,%d) to=(%d,%d) carte=%s%n",
                (joueurCourant ? "BLEU" : "ROUGE"), m.getFx(), m.getFy(), m.getTx(), m.getTy(), carte.getNomCarte());
        return jouerCoup(m.getFx(), m.getFy(), m.getTx(), m.getTy(), carte);
    }

    public boolean get_estRougeReseaux(){
        return estRougeReseaux;
    }

    public void set_estRougeReseaux(boolean estRougeReseaux){
        this.estRougeReseaux = estRougeReseaux;
    }

}

