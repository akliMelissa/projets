package Modele;

import Global.UCC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JoueurTest {
    private Joueur joueur;

    CarteJeu TigerCarte;
    CarteJeu DragonCarte;


    UCC ucc=UCC.instance();

    @BeforeEach
    void setUp() {
        joueur = new Joueur(Couleur.ROUGE);
        boolean[][] TigerMovement=new boolean[5][5];
        TigerMovement[0][2]=true;
        TigerMovement[3][2]=true;
        TigerCarte=new CarteJeu("Tiger",TigerMovement,Couleur.BLEU);

        boolean[][] DragonMovement=new boolean[5][5];
        DragonMovement[1][0]=true;
        DragonMovement[1][4]=true;
        DragonMovement[3][1]=true;
        DragonMovement[3][4]=true;
        DragonCarte=new CarteJeu("Dragon",DragonMovement,Couleur.ROUGE);

        joueur.setCarte(TigerCarte,0);
        joueur.setCarte(DragonCarte,1);

        ucc.setMode_test(true);

    }

    @AfterEach
    void tearDown() {
        ucc.setMode_test(false);
    }

    @Test
    void test_get_cartes(){

        CarteJeu[] cartes=joueur.getCartes();
        Assertions.assertEquals(cartes.length,2);
        Assertions.assertEquals(cartes[0],TigerCarte);
        Assertions.assertEquals(cartes[1],DragonCarte);
    }

    @Test
    void test_get_position_0(){
        Assertions.assertEquals(0,joueur.getpositionCarte(TigerCarte));
    }

    @Test
    void test_get_position_1(){
        Assertions.assertEquals(1, joueur.getpositionCarte(DragonCarte));
    }

    @Test
    void test_get_position_Null(){
        Assertions.assertEquals(-1, joueur.getpositionCarte(null));
    }

    @Test
    void test_get_position_not_in(){
        CarteJeu fail=new CarteJeu("fail_cart",null,Couleur.BLEU);
        Assertions.assertEquals(-1, joueur.getpositionCarte(fail));
    }

    @Test
    void test_get_carte(){
        Assertions.assertEquals(TigerCarte,joueur.getCarte(0));
    }

    @Test
    void test_get_carte_null(){

        CarteJeu test=joueur.getCarte(-1);
        Assertions.assertNull(test);
    }

    @Test
    void test_get_colour_null(){
        Assertions.assertEquals(Couleur.ROUGE,joueur.getCouleur());
    }

    @Test
    void test_set_carte(){
        CarteJeu test_carte=new CarteJeu("carte",null,Couleur.BLEU);
        joueur.setCarte(test_carte,0);
        Assertions.assertEquals(test_carte,joueur.getCarte(0));
    }

    @Test
    void test_set_carte_null(){
        CarteJeu test_carte=new CarteJeu("carte",null,null);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            joueur.setCarte(test_carte,2);
        });
    }

}