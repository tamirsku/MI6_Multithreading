package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Squad;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private int serial;
	private Squad squad;
	private CountDownLatch latch;
	int time;
	int remainingTime;
	public Moneypenny(int uniqueSerial, CountDownLatch latch) {
		super("moneypenny");
		squad=Squad.getInstance();
		serial=uniqueSerial;
		this.latch = latch;
	}
	public int getUniqueSerial(){return serial; }
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
			time = tick.getTick();
			remainingTime = tick.getTimeLeft();
		});

		subscribeBroadcast(FinalTickBroadcast.class, call->{
			this.terminate();
		});
		this.subscribeEvent(AgentsAvailableEvent.class, (AgentsAvailableEvent agentsAvailableEvent)->{
			Object[] objects = new Object[4];
			objects[0] = serial;
			List<String> agentsToCheck=agentsAvailableEvent.getAgentSerials();
			Boolean resultIfAgentsExist=squad.getAgents(agentsToCheck);
			if(resultIfAgentsExist){
				objects[1] = squad.getAgentsNames(agentsToCheck);
			}
			else {objects[1] = null;}
			objects[2] = resultIfAgentsExist;
			objects[3] = new Future<Boolean>();
			complete(agentsAvailableEvent,objects);
			Boolean answer = ((Future<Boolean>)objects[3]).get(remainingTime*100, TimeUnit.MILLISECONDS);
			if(answer!=null && answer) {
				squad.sendAgents(agentsToCheck, agentsAvailableEvent.getDuration());
			}
			else {
				if(resultIfAgentsExist)
					squad.releaseAgents(agentsToCheck);
			}
		});
		latch.countDown();
	}
}
