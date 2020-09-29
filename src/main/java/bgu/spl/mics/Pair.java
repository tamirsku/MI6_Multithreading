package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class Pair {
    private ConcurrentLinkedQueue<Subscriber> first;
    private Semaphore second;

    public Pair(ConcurrentLinkedQueue<Subscriber> queue, Semaphore semaphore) {
        first = queue;
        second = semaphore;
    }

    public ConcurrentLinkedQueue<Subscriber> getFirst() {
        return first;
    }

    public Semaphore getSecond() {
        return second;
    }
}
