package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private final int singleTick = 100;
	private int duration;
	private int currTick;

	public TimeService(int duration) {
		super("Time_Service");
		currTick=0;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		run();
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (currTick > duration) {
					Broadcast b = new FinalTickBroadcast();
					getSimplePublisher().sendBroadcast(b);
					timer.cancel();
					timer.purge();
				} else {
					Broadcast b = new TickBroadcast(currTick, duration - currTick);
					getSimplePublisher().sendBroadcast(b);
					currTick++;
				}

			}
		};
		timer.schedule(task,singleTick,singleTick);
	}

}
