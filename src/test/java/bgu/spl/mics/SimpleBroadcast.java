package bgu.spl.mics;

public class SimpleBroadcast implements Broadcast {
    private String senderId;
    private Boolean gotThere;
    public SimpleBroadcast(String senderId) {
        this.senderId = senderId;
        gotThere = false;
    }

    public String getSenderId() {
        return senderId;
    }
    public void changeGotThere() { gotThere = true; }
    public Boolean returnGotThere() { return gotThere; }

}
