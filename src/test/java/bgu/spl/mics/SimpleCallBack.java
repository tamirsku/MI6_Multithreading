package bgu.spl.mics;

public class SimpleCallBack implements Callback<SimpleEvent> {
    @Override

    public void call(SimpleEvent c) {
        c.changeGotThere();
    }

}
