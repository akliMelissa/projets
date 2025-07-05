package Modele;

import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;


public class PionTest {
    UCC ucc=UCC.instance();
    Pion test_pion;

    @BeforeEach
    public void setUp() throws Exception {
        ucc.setMode_test(true);
        test_pion=new Pion(Role.ROI,Couleur.ROUGE,new Point(2,2));
    }

    @AfterEach
    public void tearDown() throws Exception {
        ucc.setMode_test(false);
    }

    @Test
    public void getRole() {
        Assertions.assertEquals(Role.ROI,test_pion.getRole());
    }

    @Test
    public void getCouleur() {
        Assertions.assertEquals(Couleur.ROUGE,test_pion.getCouleur());
    }

    @Test
    public void getPosition() {
        Assertions.assertTrue(test_pion.getPosition().equals(new Point(2,2)));
    }

    @Test
    public void estActif_vraie() {
        Assertions.assertTrue(test_pion.estActif());
    }

    @Test
    public void estActif_faux_et_capturer() {
        test_pion.capturer();
        Assertions.assertFalse(test_pion.estActif());
    }

    @Test
    public void setActif() {
        test_pion.capturer();
        test_pion.setActif();
        Assertions.assertTrue(test_pion.estActif());
    }

    @Test
    public void deplacer() {
        test_pion.deplacer(1,1);
        Assertions.assertTrue(test_pion.getPosition().equals(new Point(1,1)));
    }

    @Test
    public void calculerDistance() {
        Assertions.assertEquals(2,test_pion.calculerDistance(new Point(1,1)));
    }
}