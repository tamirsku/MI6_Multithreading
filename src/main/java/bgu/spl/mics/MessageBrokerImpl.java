package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.subscribers.Moneypenny;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
    private static MessageBrokerImpl instance = new MessageBrokerImpl();
    private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> mapMessagesQueue; //holds the queue for every subscriber
    private ConcurrentHashMap<Class<? extends Message>, Pair> registeredSubscribersMap;
    private ConcurrentHashMap<Event, Future> eventsFutureMap;

    private MessageBrokerImpl() {
        mapMessagesQueue = new ConcurrentHashMap<>();
        registeredSubscribersMap = new ConcurrentHashMap<>();
        eventsFutureMap = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MessageBroker getInstance() {
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
        Pair addSubscriber = registeredSubscribersMap.computeIfAbsent(type, a -> new Pair(new ConcurrentLinkedQueue<>(), new Semaphore(1, true)));
        addSubscriber.getFirst().add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
        Pair addSubscriber = registeredSubscribersMap.computeIfAbsent(type, a -> new Pair(new ConcurrentLinkedQueue<>(), new Semaphore(1, true)));
        addSubscriber.getFirst().add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        eventsFutureMap.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (b.getClass() == FinalTickBroadcast.class) {
            for (Subscriber subscriber : registeredSubscribersMap.get(FinalTickBroadcast.class).getFirst()) {
                BlockingQueue<Message> subscriberQueue = mapMessagesQueue.get(subscriber);
                synchronized (subscriberQueue) {
                    cleanMsgQueue(subscriber);
                    subscriberQueue.add(b);
                }
            }
        } else {
            ConcurrentLinkedQueue<Subscriber> subscribers = registeredSubscribersMap.get(b.getClass()).getFirst();
            if (subscribers != null) {
                Iterator<Subscriber> iter = subscribers.iterator();
                while (iter.hasNext()) {
                    Subscriber userToNotify = iter.next();
                    BlockingQueue<Message> msgQueue = mapMessagesQueue.get(userToNotify);
                    msgQueue.add(b);
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> answer = new Future<>();
        eventsFutureMap.put(e, answer);
        ConcurrentLinkedQueue<Subscriber> queue = registeredSubscribersMap.get(e.getClass()).getFirst();
        if (queue != null) {
            Semaphore semaphore = registeredSubscribersMap.get(e.getClass()).getSecond();
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
            }
            Subscriber todoPerson = queue.poll();
            if (todoPerson != null) {
                synchronized (todoPerson) {
                    BlockingQueue<Message> msgQueue = mapMessagesQueue.get(todoPerson);
                    if(msgQueue!=null) {
                        mapMessagesQueue.get(todoPerson).add(e);
                        queue.add(todoPerson);
                    }
                    else
                        answer.resolve(null);
                }
            } else {
                answer.resolve(null);
            }

            semaphore.release();
            return answer;
        }
        return null;
    }


    @Override
    public void register(Subscriber m) {
        mapMessagesQueue.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    private void cleanMsgQueue(Subscriber m) {
        BlockingQueue<Message> mQueue = mapMessagesQueue.get(m);
        for (Message msg : mQueue) {
            if (eventsFutureMap.contains(msg)) {
                eventsFutureMap.get(msg).resolve(null);
                mQueue.poll();
            }
        }

    }

    @Override
    public void unregister(Subscriber m) {
        if (mapMessagesQueue.containsKey(m)) {
            cleanMsgQueue(m);
            synchronized (m) {
                mapMessagesQueue.remove(m);
            }
        }
        Set<Class<? extends Message>> allMessagesSets = registeredSubscribersMap.keySet();
        for (Class messageType : allMessagesSets) {
            registeredSubscribersMap.get(messageType).getFirst().remove(m);
        }

    }

    @Override
    public Message awaitMessage(Subscriber m) throws InterruptedException {
        return mapMessagesQueue.get(m).take();
    }

}
