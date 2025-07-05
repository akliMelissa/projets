package Modele;

import Global.UCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RoleTest {
    UCC ucc=new UCC().instance();

    @BeforeEach
    public void setUp() throws Exception {
        ucc.setMode_test(true);
    }

    @AfterEach
    public void tearDown() throws Exception {
        ucc.setMode_test(false);
    }

    @Test
    public void testToString_ROI() {
        Role role=Role.ROI;
        Assertions.assertEquals(role.toString(),"Roi");
        role=Role.PION;
    }

    @Test public void testToString_PION() {
        Role role=Role.PION;
        Assertions.assertEquals(role.toString(),"Pion");
    }

    @Test
    public void toRole() {
        Role role=Role.toRole("Pion");
        Assertions.assertEquals(role,Role.PION);


    }

    @Test public void toRole_ROI(){
        Role role=Role.toRole("Roi");
        Assertions.assertEquals(role,Role.ROI);
    }

    @Test public void toRole_INCORRECT(){
        Role role=Role.toRole("fou");
        Assertions.assertNull(role);
    }

    @Test public void toRole_NULL(){
        Role role=Role.toRole(null);
        Assertions.assertNull(role);
    }
}