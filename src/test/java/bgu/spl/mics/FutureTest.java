package bgu.spl.mics;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<String> future;
    private Future<String> nofuture;
    private String str;
    @BeforeEach
    public void setUp(){
        future = new Future<>();
        nofuture = new Future<>();
        str = "Done";
    }
    @Test
    public void testGet(){
        Thread t1 = new Thread(() -> {
            synchronized (this) {
                String ans = future.get();
                try {
                    this.wait(2000);
                } catch (InterruptedException e) {
                }
                assertEquals(str, ans);
            }
        });
        t1.start();
        future.resolve(str);
        try { t1.join(); } catch (InterruptedException e) { }
    }
    @Test
    public void testResolve(){
        future.resolve(str);
        assertEquals(str,future.get());
    }
    @Test
    public void testGetTimeLegal() {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                future.resolve(str);
            }
            catch (InterruptedException e) { }
        });
        t1.start();
        String ans = future.get(2,TimeUnit.SECONDS);
        try { t1.join();} catch (InterruptedException e) { }
        assertEquals(ans,str);
    }
    @Test
    public void testGetTimeIllegal(){
        String ans = nofuture.get(1,TimeUnit.SECONDS);
        Thread t1 = new Thread(() -> {
            try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { }
            nofuture.resolve(str);
        });
        t1.start();
        assertNotEquals(ans,str);

    }
    @Test
    public void testisDone(){
        future.resolve(str);
        assertEquals(true,future.isDone());
        assertEquals(false,nofuture.isDone());
    }


}

