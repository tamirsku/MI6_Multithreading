package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int uniqueID;
	private Diary diary;
	private MessageBroker msg;
	private Integer time;
	private Integer Qtime;
	private CountDownLatch latch;
	private Integer remainingTime;
	public M(int uniqueID, CountDownLatch latch) {
		super("M");
		this.uniqueID=uniqueID;
		this.diary=Diary.getInstance();
		this.msg= MessageBrokerImpl.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class,call->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
			time = tick.getTick();
			remainingTime = tick.getTimeLeft();
		});
		subscribeEvent(MissionReceivedEvent.class,(MissionReceivedEvent missionReceivedEvent)-> {

			AgentsAvailableEvent agentsAvailableEvent = new AgentsAvailableEvent(missionReceivedEvent.getMissionInfo().getSerialAgentsNumbers(),missionReceivedEvent.getMissionInfo().getDuration());
			diary.incrementTotal();
			Future<Object[]> agentsResult = msg.sendEvent(agentsAvailableEvent);
			Object[] moneyPennyAnswer = agentsResult.get(remainingTime*100,TimeUnit.MILLISECONDS);
			if(moneyPennyAnswer!=null && ((Boolean)moneyPennyAnswer[2]))
			{
				GadgetAvailableEvent gadgetAvailableEvent = new GadgetAvailableEvent(missionReceivedEvent.getMissionInfo().getGadget());
				Future<Integer> gadgetResult = msg.sendEvent(gadgetAvailableEvent);
				Qtime = gadgetResult.get(remainingTime*100,TimeUnit.MILLISECONDS);
				if (Qtime!=null && Qtime!= -1) {    //gadget available
					if (missionReceivedEvent.getMissionInfo().getTimeExpired() > time) {    //mission's expiry time doesn't passed
						((Future<Boolean>)moneyPennyAnswer[3]).resolve(true);
						MissionInfo missionInfo = missionReceivedEvent.getMissionInfo();
						Report reportToAdd=new Report(missionInfo.getMissionName(), uniqueID, ((Integer)moneyPennyAnswer[0]), missionInfo.getSerialAgentsNumbers(),((List<String>)moneyPennyAnswer[1]) , missionInfo.getGadget(), missionInfo.getTimeIssued(), Qtime, time);
						diary.addReport(reportToAdd);
						complete(missionReceivedEvent, true);
					}
					else {
						((Future<Boolean>)moneyPennyAnswer[3]).resolve(false);
						complete(missionReceivedEvent, false);
					}
				}
				else {
					((Future<Boolean>)moneyPennyAnswer[3]).resolve(false);
					complete(missionReceivedEvent, false);
				}
			}
			else {
				if(moneyPennyAnswer!=null)
					((Future<Boolean>)moneyPennyAnswer[3]).resolve(null);
				complete(missionReceivedEvent,false);
			}
		});
		latch.countDown();

	}
}

