package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SquadTest {
    private Squad squad;
    private Agent agent1;
    private Agent agent2;
    private List<String> serials;
    @BeforeEach
    public void setUp(){
        squad=Squad.getInstance();
        agent1=new Agent();
        agent2=new Agent();
        agent1.setName("a");
        agent1.setSerialNumber("007");
        agent2.setName("b");
        agent2.setSerialNumber("008");
        serials = new ArrayList<String>();
        serials.add(agent1.getSerialNumber());
        serials.add(agent2.getSerialNumber());
    }
    @AfterAll
    public void tearDown(){
        squad=null;
    }
    @Test
    public void loadTest(){
        List<String> check=squad.getAgentsNames(serials);
        if(check.contains(agent1.getName())==false)
            fail("load test fail, "+agent1.getName()+" is missing");
        if(check.contains(agent2.getName())==false)
            fail("load test fail, "+agent2.getName()+" is missing");
    }
    @Test
    public void getAgentsTest(){
        if(squad.getAgents(serials)==false)
            fail("get agents test fail,one or more of the agents are missing");
    }
    @Test
    public void getAgentsNamesTest(){
        List<String> agentsNames=squad.getAgentsNames(serials);
        if(agentsNames.contains(agent1)==false)
            fail("get agents names test fail,"+agent1.getName()+" is missing");
        if(agentsNames.contains(agent2)==false)
            fail("get agents names test fail,"+agent2.getName()+" is missing");
    }
    @Test
    public void testRelease(){
        Agent[] agents={agent1,agent2};
        squad.load(agents);
        squad.releaseAgents(serials);
        if(squad.getAgentsNames(serials).size()!=0)
            fail("not removed");
    }
    @Test
    public void testSend() {
        squad.sendAgents(serials,1000);
        try { TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
        assertEquals(true,squad.getAgents(serials));
        squad.sendAgents(serials,10000);
        assertEquals(false,squad.getAgents(serials));
    }


}
