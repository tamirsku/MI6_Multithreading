package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;


/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private Inventory inventory;
	private int currTick;
	private CountDownLatch latch;

	public Q(CountDownLatch latch) {
		super("Q");
		this.inventory = Inventory.getInstance();
		currTick=0;
		this.latch = latch;
	}
	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class,call->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->{
			currTick=tickBroadcast.getTick();
		});
		subscribeEvent(GadgetAvailableEvent.class,(GadgetAvailableEvent gadgetAvailableEvent)->{
			if(inventory.getItem(gadgetAvailableEvent.getGadgetName()))
				complete(gadgetAvailableEvent,currTick);
			else{
				complete(gadgetAvailableEvent,-1);
			}
		});
		latch.countDown();
	}

}

