package Modele;

import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CouleurTest {
    UCC ucc=UCC.instance();

    @BeforeEach
    public void setUp() throws Exception {
        ucc.setMode_test(true);
    }

    @AfterEach
    public void tearDown() throws Exception {
        ucc.setMode_test(false);
    }

    @Test
    public void testToString_ROUGE() {
        Couleur couleur=Couleur.ROUGE;
        Assertions.assertEquals(couleur.toString(),"Rouge");

    }

    @Test
    public void testToString_BLEU() {
        Couleur couleur=Couleur.BLEU;
        Assertions.assertEquals(couleur.toString(),"Bleu");
    }

    @Test
    public void toCouleur_ROUGE() {
        Couleur couleur;
        couleur=Couleur.toCouleur("Rouge");
        Assertions.assertTrue(couleur.equals(Couleur.ROUGE));



    }

    @Test
    public void toCouleur_BLEU() {
        Couleur couleur=Couleur.toCouleur("Bleu");
        Assertions.assertTrue(couleur.equals(Couleur.BLEU));
    }

    @Test
    public void toCouleur_INCORRECT() {
        Couleur couleur=Couleur.toCouleur("vert");
        Assertions.assertNull(couleur);
    }
    @Test
    public void toCourleur_NULL(){
        Couleur couleur=Couleur.toCouleur(null);
        Assertions.assertNull(couleur);
    }
}