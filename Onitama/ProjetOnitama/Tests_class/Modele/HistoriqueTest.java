package Modele;


import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoriqueTest {
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
    public void coup_joue(){
        Historique historique=new Historique();
        historique.coup_jouer(10);
        Assertions.assertEquals(historique.get_top_joue(),10);
    }

    @Test
    public void peut_annuler_false(){
        Historique historique=new Historique();
        Assertions.assertEquals(historique.peut_annuler(),false);
    }

    @Test
    public void peut_annuler_true(){
        Historique historique=new Historique();
        historique.coup_jouer(20);
        Assertions.assertEquals(historique.peut_annuler(),true);
    }

    @Test
    public void annuler_coup(){
        Historique historique=new Historique();
        historique.coup_jouer(10);
        Assertions.assertEquals(historique.annuler_coup(),10);
        Assertions.assertEquals(historique.get_top_annuler(),10);
    }

    @Test
    public void peut_refaire_false(){
        Historique historique=new Historique();
        Assertions.assertEquals(historique.peut_refaire(),false);
    }

    @Test
    public void peut_refaire_true(){
        Historique historique=new Historique();
        historique.coup_jouer(20);
        historique.annuler_coup();
        Assertions.assertEquals(historique.peut_refaire(),true);
    }

    @Test
    public void refaire_coup(){
        Historique historique=new Historique();
        historique.coup_jouer(10);
        historique.annuler_coup();
        Assertions.assertEquals(historique.refaire_coup(),10);
        Assertions.assertEquals(historique.get_top_joue(),10);
    }

}
