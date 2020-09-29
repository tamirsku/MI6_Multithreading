package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;


import java.util.List;

public class AgentsAvailableEvent implements Event<Object[]> {
    private List<String> agentSerials;
    private int duration;
    public AgentsAvailableEvent(List<String> agentNumbers,int duration){
        this.agentSerials = agentNumbers;
        this.duration = duration;
    }
    public List<String> getAgentSerials(){return agentSerials;}
    public int getDuration(){return duration;}
}
