package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int timeLeft;
    private int timePassed;
    public TickBroadcast(int timePassed,int timeLeft){
        this.timeLeft = timeLeft;
        this.timePassed = timePassed;
    }
    public int getTick(){return timePassed;}
    public int getTimeLeft(){return timeLeft;}
}