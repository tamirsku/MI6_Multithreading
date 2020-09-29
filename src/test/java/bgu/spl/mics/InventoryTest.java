package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;


public class InventoryTest {
    private Inventory inv;
    @BeforeEach
    public void setUp(){
        inv = Inventory.getInstance();
    }
    @AfterAll
    public void tearDown(){
        inv=null;
    }

    @Test
    public void loadTest(){
        String[] gadg={"gun1","gun2","grande1"};
        inv.load(gadg);
        for (String s : gadg) {
            if (inv.getItem(s) == false)
                fail("load test fail, " + s + " is missing");
        }
    }
    @Test
    public void getItemTest(){
        String[] gadg={"gun1"};
        inv.load(gadg);
        if(inv.getItem(gadg[0])==false)
            fail("getItem test fail, " + gadg[0] + "return false");
    }

}
