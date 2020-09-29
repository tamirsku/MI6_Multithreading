package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class GadgetAvailableEvent implements Event<Integer> {
    private String gadgetName;
    public GadgetAvailableEvent(String gadgetName){
        this.gadgetName = gadgetName;
    }
    public String getGadgetName(){ return gadgetName; }
}
