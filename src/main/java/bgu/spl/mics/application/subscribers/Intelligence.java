package bgu.spl.mics.application.subscribers;


import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	List<MissionInfo> missionsInfo;
	private MessageBroker msg;
	private int currtime;
	private CountDownLatch latch;
	public Intelligence(List<MissionInfo> missionsInfo, CountDownLatch latch) {
		super("Intelligence");
		this.missionsInfo=missionsInfo;
		msg= MessageBrokerImpl.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class,call->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->{
			currtime = tickBroadcast.getTick();
			for(MissionInfo curr: missionsInfo){
				if(currtime==curr.getTimeIssued()){
					MissionReceivedEvent missionReceivedEvent=new MissionReceivedEvent(curr);
					Future<Boolean> future=msg.sendEvent(missionReceivedEvent);
				//	complete(missionReceivedEvent,future.get());
				}
			}
		});
		latch.countDown();
	}
}
