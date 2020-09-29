package bgu.spl.mics;

public class SimpleEvent implements Event<String> {
    private String senderName;
    private Boolean gotThere;
    public SimpleEvent(String senderName) {
        this.senderName = senderName;
        gotThere = false;
    }
    public String getSenderName() {
        return senderName;
    }
    public void changeGotThere() { gotThere = true; }
    public Boolean returnGotThere() { return gotThere; }
}

