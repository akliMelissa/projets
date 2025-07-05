package Modele;//package Tests_class;

import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

class JeuTest {
    UCC ucc=UCC.instance();
    Jeu jeu;

    CarteJeu TigerCarte;
    CarteJeu DragonCarte;
    CarteJeu ElephantCarte;
    CarteJeu CrabCarte;

    @BeforeEach
    void setUp() {
        boolean[][] TigerMovement=new boolean[5][5];
        TigerMovement[0][2]=true;
        TigerMovement[3][2]=true;
        TigerCarte=new CarteJeu("Tiger",TigerMovement,Couleur.BLEU);

        boolean[][] DragonMovement=new boolean[5][5];
        DragonMovement[1][0]=true;
        DragonMovement[1][4]=true;
        DragonMovement[3][1]=true;
        DragonMovement[3][3]=true;
        DragonCarte=new CarteJeu("Dragon",DragonMovement,Couleur.ROUGE);

        boolean[][] ElephantMovement=new boolean[5][5];
        ElephantMovement[1][1]=true;
        ElephantMovement[1][3]=true;
        ElephantMovement[2][1]=true;
        ElephantMovement[2][3]=true;
        ElephantCarte=new CarteJeu("Elephant",ElephantMovement,Couleur.ROUGE);

        boolean[][] CrabMovement=new boolean[5][5];
        CrabMovement[1][2]=true;
        CrabMovement[2][0]=true;
        CrabMovement[2][4]=true;
        CrabCarte=new CarteJeu("Crab",CrabMovement,Couleur.BLEU);

        ucc.setMode_test(true);
        jeu=new Jeu();
    }

    @AfterEach
    void tearDown() {
        ucc.setMode_test(false);
    }

    @Test
    void estTermine_false() {
        System.out.println(jeu.toString());
        Assertions.assertFalse(jeu.estTermine());
    }

    @Test
    void estTermine_true_bleu_roi_position() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,0));

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==1&&j==2){
                    pion[i][j]=roi_rouge;
                }else if(i==0&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        Assertions.assertTrue(jeu.estTermine());
    }

    @Test
    void estTermine_true_red_kill() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        roi_rouge.capturer();
        methode(pion, roi_bleu);
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        Assertions.assertTrue(jeu.estTermine());
    }

    @Test
    void estTermine_true_red_roi_position() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,4));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==4&&j==2){
                    pion[i][j]=roi_rouge;
                }else if(i==3&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        Assertions.assertTrue(jeu.estTermine());
    }

    @Test
    void estTermine_true_bleu_kill() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        methode(pion, roi_bleu);
        roi_bleu.capturer();
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        Assertions.assertTrue(jeu.estTermine());
    }

    private void methode(Pion[][] pion, Pion roi_bleu) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==1&&j==2){
                    pion[i][j]=null;
                }else if(i==3&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
    }

    @Test
    void deplacerPiece() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==1&&j==2){
                    pion[i][j]=roi_rouge;
                }else if(i==3&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        jeu.deplacerPiece(2,1,2,2);
        Pion[][] pion2=jeu.getPlateau();
        Assertions.assertNotNull(pion2[2][2]);
    }

    @Test
    void deplacerPiece_capture() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==1&&j==2){
                    pion[i][j]=roi_rouge;
                }else if(i==3&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        jeu.deplacerPiece(2,1,2,3);
        Pion[][] pion3=jeu.getPlateau();
        System.out.println(pion3[3][2]);
        Assertions.assertNotNull(pion3[3][2]);
        Assertions.assertTrue(jeu.estTermine());
    }

    @Test
    void est_dans_terrain_pass(){
        Assertions.assertTrue(jeu.est_dans_terrain(2,3));
    }

    @Test
    void est_dans_terrain_fail1() {
        Assertions.assertFalse(jeu.est_dans_terrain(-1,3));
    }

    @Test
    void est_dans_terrain_fail2() {
        Assertions.assertFalse(jeu.est_dans_terrain(6,3));
    }

    @Test
    void est_dans_terrain_fail3() {
        Assertions.assertFalse(jeu.est_dans_terrain(2,-1));
    }

    @Test
    void est_dans_terrain_fail4() {
        Assertions.assertFalse(jeu.est_dans_terrain(2,6));
    }

    @Test
    void estPositionValide() {
        creation_plateau_test();
        Assertions.assertTrue(jeu.estPositionValide(2,1,Couleur.BLEU));
    }
    @Test
    void estPosition_fail1_hors_terrain(){

        Assertions.assertFalse(jeu.estPositionValide(2,-1,Couleur.BLEU));
    }

    @Test
    void estPosition_fail2_case_occupe_par_pion_m_colour() {
        creation_plateau_test();
        Assertions.assertFalse(jeu.estPositionValide(2,1,Couleur.ROUGE));
    }

    private void creation_plateau_test() {
        Pion[][] pion=new Pion[5][5];
        Pion roi_rouge=new Pion(Role.ROI, Couleur.ROUGE,new Point(2,1));
        Pion roi_bleu=new Pion(Role.ROI, Couleur.BLEU,new Point(2,3));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(i==1&&j==2){
                    pion[i][j]=roi_rouge;
                }else if(i==3&&j==2){
                    pion[i][j]=roi_bleu;
                }else {
                    pion[i][j]=null;
                }
            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
    }

    @Test
    void jouerCoup_vraie() {
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(true);
        Assertions.assertTrue(jeu.jouerCoup(2,1,2,0,TigerCarte));
    }

    @Test
    void jouerCoup_false_pas_coup_possible(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(false);
        Assertions.assertFalse(jeu.jouerCoup(2,1,2,0,TigerCarte));
    }

    @Test
    void jouerCoup_false_pas_de_piece_select(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(true);
        Assertions.assertFalse(jeu.jouerCoup(0,0,2,0,TigerCarte));
    }

    @Test
    void jouerCoup_false_piece_ennemie(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(true);
        Assertions.assertFalse(jeu.jouerCoup(2,3,2,2,TigerCarte));
    }
    @Test
    void jouerCoup_false_mauvais_deplacement(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        System.out.println(jeu);
        jeu.set_est_jouable(true);
        Assertions.assertFalse(jeu.jouerCoup(2,1,2,2,TigerCarte));

    }

    @Test
    void jouerCoup_false_piece_non_valide(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(true);
        Assertions.assertFalse(jeu.jouerCoup(2,1,-2,0,TigerCarte));

    }

    @Test
    void jouerCoup_false_out_of_bound(){
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        jeu.set_est_jouable(true);
        Assertions.assertFalse(jeu.jouerCoup(-2,1,2,0,TigerCarte));
    }

    void creation_plateau_test_tour_jouable_et_joue_piece(){
        Pion[][] plateau=new Pion[5][5];
        for (int i=0;i<5;i++){
            for (int j=0;j<5;j++){
                plateau[i][j]=null;
            }
        }
        Pion roi_rouge=new Pion(Role.ROI,Couleur.ROUGE,new Point(2,2));
        plateau[2][2]=roi_rouge;
        Pion roi_bleu =new Pion(Role.ROI,Couleur.BLEU,new Point(3,0));
        plateau[0][3]=roi_bleu;
        Pion pion_b_1 =new Pion(Role.PION,Couleur.BLEU,new Point(0,0));
        plateau[0][0]=pion_b_1;
        Pion pion_b_2 =new Pion(Role.PION,Couleur.BLEU,new Point(1,0));
        plateau[0][1]=pion_b_2;
        Pion pion_b_3 =new Pion(Role.PION,Couleur.BLEU,new Point(2,0));
        plateau[0][2]=pion_b_3;
        Pion pion_b_4 =new Pion(Role.PION,Couleur.BLEU,new Point(4,0));
        plateau[0][4]=pion_b_4;
        jeu.set_plateau(plateau,roi_rouge,roi_bleu);
        jeu.set_un_Pion_Bleu(pion_b_1);
        jeu.set_un_Pion_Bleu(pion_b_2);
        jeu.set_un_Pion_Bleu(pion_b_3);
        jeu.set_un_Pion_Bleu(pion_b_4);

        int tmp=0;
        int carte_select=0;
        CarteJeu[] listes_cartes= jeu.getCartes();
        Joueur joueur2=jeu.getJoueur2();
        while (carte_select!=2&&tmp<listes_cartes.length){
            if(listes_cartes[tmp].getNomCarte().equals("Crab")||listes_cartes[tmp].getNomCarte().equals("Boar")){
                joueur2.setCarte(listes_cartes[tmp],carte_select);
                carte_select++;
            }
            tmp++;
        }
    }

    @Test
    void tour_est_jouable_vraie() {
        creation_plateau_test_tour_jouable_et_joue_piece();
        jeu.set_joueurCourant(false);
        Assertions.assertTrue(jeu.tour_est_jouable());
    }

    @Test
    void tour_est_jouable_false() {
        creation_plateau_test_tour_jouable_et_joue_piece();
        jeu.set_joueurCourant(true);
        Assertions.assertFalse(jeu.tour_est_jouable());
    }

    @Test
    void test_tout_coup_possible_vraie() {
        creation_plateau_test();
        jeu.set_joueurCourant(false);
        Joueur joueur1=jeu.getJoueur1();

        joueur1.setCarte(TigerCarte,0);
        joueur1.setCarte(DragonCarte,1);
        Assertions.assertTrue(jeu.test_tout_coup_possible(TigerCarte));
    }

    @Test
    void test_tout_coup_possible_false(){
        Joueur joueur1=jeu.getJoueur1();
        joueur1.setCarte(ElephantCarte,0);
        joueur1.setCarte(CrabCarte,1);
        plateau_un_injouable();
        jeu.set_joueurCourant(false);
        System.out.println(jeu);
        Assertions.assertFalse(jeu.test_tout_coup_possible(ElephantCarte));
    }

    private void plateau_un_injouable(){
        Pion[][] pion=new Pion[5][5];
        ArrayList<Pion> pion_bleu=new ArrayList<>();
        ArrayList<Pion> pion_rouge=new ArrayList<>();
        Pion roi_rouge=null;
        Pion pion_tmp;
        Pion roi_bleu=null;
        for (int i = 0; i < 5; i++) {
            if (i == 1) {
                roi_bleu= new Pion(Role.ROI, Couleur.BLEU, new Point(i, 0));
                pion[0][i] = roi_bleu;
                roi_rouge = new Pion(Role.ROI, Couleur.ROUGE, new Point(i, 4));
                pion[4][i] = roi_rouge;
            } else {
                pion_tmp = new Pion(Role.PION, Couleur.BLEU, new Point(i, 0));
                pion[0][i] = pion_tmp;
                pion_bleu.add(pion_tmp);
                pion_tmp = new Pion(Role.PION, Couleur.ROUGE, new Point(i, 4));
                pion[4][i] = pion_tmp;
                pion_rouge.add(pion_tmp);

            }
        }
        jeu.set_plateau(pion,roi_rouge,roi_bleu);
        while(!pion_rouge.isEmpty()){
            jeu.set_un_Pion_Rouge(pion_rouge.remove(0));
        }

        while (!pion_bleu.isEmpty()) {
            jeu.set_un_Pion_Bleu(pion_bleu.remove(0));
        }

    }

    @Test
    void piece_est_jouable_vraie_vide() {
        creation_plateau_test_tour_jouable_et_joue_piece();
        jeu.set_joueurCourant(false);
        Assertions.assertTrue(jeu.piece_est_jouable(0,1));
    }

    @Test
    void piece_est_jouable_vraie_occupe() {
        creation_plateau_test_tour_jouable_et_joue_piece();
        jeu.set_joueurCourant(false);
        Assertions.assertFalse(jeu.piece_est_jouable(0,-2));
    }

    @Test
    void piece_est_jouable_false() {
        creation_plateau_test_tour_jouable_et_joue_piece();
        jeu.set_joueurCourant(false);
        Assertions.assertFalse(jeu.piece_est_jouable(0,-3));
    }

    @Test
    void sauvgarder() {
        // à faire après implémentation
    }

    @Test
    void swapCartes1() {
        Joueur joueur1=jeu.getJoueur1();

        jeu.set_joueurCourant(false);
        CarteJeu jeu1=joueur1.getCarte(0);
        jeu.swapCartes(jeu1);
        Assertions.assertEquals(true,jeu.getjoueurCourant());
    }

    @Test
    void swapCartes2() {
        Joueur joueur2=jeu.getJoueur2();
        jeu.set_joueurCourant(true);
        CarteJeu jeu2=joueur2.getCarte(0);
        jeu.swapCartes(jeu2);
        Assertions.assertEquals(false,jeu.getjoueurCourant());
    }

}