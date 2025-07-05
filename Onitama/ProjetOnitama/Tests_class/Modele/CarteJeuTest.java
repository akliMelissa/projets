package Modele;

import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.util.List;



public class CarteJeuTest {
    UCC ucc=UCC.instance();
    CarteJeu test_carte;
    boolean[][] TigerMovement=new boolean[5][5];

    @BeforeEach
    public void setUp() throws Exception {
        ucc.setMode_test(true);

        TigerMovement[0][2]=true;
        TigerMovement[3][2]=true;
        test_carte=new CarteJeu("Tiger",TigerMovement,Couleur.BLEU);
    }

    @AfterEach
    public void tearDown() throws Exception {
        ucc.setMode_test(false);
    }

    @Test
    public void getNomCarte() {
        Assertions.assertEquals(test_carte.getNomCarte(),"Tiger");
    }

    @Test
    public void getGrilleDeplacements() {
        Assertions.assertEquals(test_carte.getGrilleDeplacements(),TigerMovement);
    }

    @Test
    public void estCouleurRouge() {
        Assertions.assertEquals(test_carte.estCouleurRouge(),false);
    }

    @Test
    public void deplacementPossible_false() {
       Assertions.assertEquals(test_carte.deplacementPossible(5,5),false);
    }

    @Test
    public void deplacementPossible_true() {
        Assertions.assertEquals(test_carte.deplacementPossible(0,1),true);
    }

    @Test
    public void getDeplacementsRelatifs() {
        List<Point> points=test_carte.getDeplacementsRelatifs();
        Point point=points.get(0);
        Point point_comparer=new Point(0,-2);
        Assertions.assertTrue(point.equals(point_comparer));
        point=points.get(1);
        point_comparer=new Point(0,1);
        Assertions.assertTrue(point.equals(point_comparer));
    }

    @Test
    public void getDeplacement() {
        List<int[]> points=test_carte.getDeplacement();
        int[] point=points.get(0);
        Assertions.assertEquals(point[0],0);
        Assertions.assertEquals(point[1],-2);
        point=points.get(1);
        Assertions.assertEquals(point[0],0);
        Assertions.assertEquals(point[1],1);
    }
}