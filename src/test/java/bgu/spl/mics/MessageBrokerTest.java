package bgu.spl.mics;

import bgu.spl.mics.application.subscribers.M;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {
    private SimpleSubscriber sub1;
    private SimpleEvent event;
    private SimpleBroadcast broadcast;
    private MessageBroker msg1;
    @BeforeEach
    public void setUp(){
        sub1=new SimpleSubscriber("sub1");
        event = new SimpleEvent("Yair");
        broadcast = new SimpleBroadcast("1");
        msg1 = MessageBrokerImpl.getInstance();
    }
    @Test
    public void testGetInstance(){
        assertNotNull(MessageBrokerImpl.getInstance());
    }
    @Test
    public void testSubscribeEvent(){
        msg1.subscribeEvent(SimpleEvent.class,sub1);
        Future<String> stringFuture = msg1.sendEvent(event);
        assertTrue(event.returnGotThere());
        assertEquals(stringFuture.get(),"Finished!");
    }
    @Test
    public void testSubscribeBroadcast(){
        msg1.subscribeBroadcast(SimpleBroadcast.class,sub1);
        msg1.sendBroadcast(broadcast);
        assertTrue(broadcast.returnGotThere());
    }
}

