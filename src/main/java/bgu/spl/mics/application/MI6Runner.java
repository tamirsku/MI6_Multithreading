package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    private static CountDownLatch latch;
    private static final int QSize = 1;
    private static int duration;
    public static void main(String[] args) {
        List<Thread> threadList = new LinkedList<>();
        if (args.length != 0) {
            parseJson(args[0],threadList);
            for(Thread m:threadList) { m.start(); }
            try { latch.await(); } catch (InterruptedException e) {}
            TimeServiceStarter(duration);
            for(Thread m:threadList){ try{ m.join();
            } catch (InterruptedException ex){} }
            outputFiles(args[1],args[2]);
        }
    }


    private static void parseJson(String path,List<Thread> threadList) {
        FileReader f = null;
        try {
            f = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(f);
        Gson g = new Gson();
        JsonObject relent = g.fromJson(buffer, JsonObject.class);
        JsonArray inventory = relent.getAsJsonArray("inventory");
        JsonObject services = relent.getAsJsonObject("services");
        JsonArray squad = relent.getAsJsonArray("squad");
        addSquad(squad);
        addInventory(inventory);
        addServices(services,threadList);
    }
    private static void addSquad(JsonArray arr){
        Agent[] agentsArr = new Agent[arr.size()];
        for(int i =0;i<arr.size();i++){
            JsonObject squadInfo = arr.get(i).getAsJsonObject();
            String serialNum = squadInfo.get("serialNumber").getAsString();
            String name = squadInfo.get("name").getAsString();
            Agent toAdd = new Agent();
            toAdd.setName(name);
            toAdd.setSerialNumber(serialNum);
            agentsArr[i] = toAdd;
        }
        Squad.getInstance().load(agentsArr);
    }

    private static void addInventory(JsonArray arr) {
        String[] gadgets = new String[arr.size()];
        int i =0;
        for (JsonElement element : arr) {
            String name = element.getAsString();
            gadgets[i] = name;
            i++;
        }
        Inventory.getInstance().load(gadgets);
    }
    private static void addServices(JsonObject obj,List<Thread> threadList){
        int M = obj.get("M").getAsInt();
        int moneyPenny = obj.get("Moneypenny").getAsInt();
        duration = obj.get("time").getAsInt();
        JsonArray intlij = obj.get("intelligence").getAsJsonArray();
        int countTotalThread = M + moneyPenny + QSize + intlij.size();
        latch = new CountDownLatch(countTotalThread);
        for(int i=0;i<intlij.size();i++) {
            List<MissionInfo> missionInfoList = new LinkedList<>();
            JsonObject mission = intlij.get(i).getAsJsonObject();
            JsonArray missionsArr = mission.getAsJsonArray("missions");
            for (int k = 0; k < missionsArr.size(); k++) {
                JsonObject missionInfo = missionsArr.get(k).getAsJsonObject();
                JsonArray serialARR = missionInfo.getAsJsonArray("serialAgentsNumbers");
                List<String> agentsRequiered = new LinkedList<String>();
                for (int j = 0; j < serialARR.size(); j++) {
                    agentsRequiered.add(serialARR.get(j).getAsString());
                }
                int missionDuration = missionInfo.get("duration").getAsInt();
                String gadget = missionInfo.get("gadget").getAsString();
                String missionName = missionInfo.get("name").getAsString();
                int timeIssued = missionInfo.get("timeIssued").getAsInt();
                int timeExpired = missionInfo.get("timeExpired").getAsInt();
                MissionInfo m = new MissionInfo(missionName,agentsRequiered,gadget,timeIssued,timeExpired,missionDuration);
                missionInfoList.add(m);
            }
            IntelligenceThreadStarter(missionInfoList,threadList);
        }
        QThreadStarter(threadList);
        for(int m = 0;m<M;m++){ //Create M's
            MThreadStarter(m,threadList);
        }
        for(int penny = 0;penny<moneyPenny;penny++){ //Create MoneyPenny's
            MonneyPennyThreadStarter(penny,threadList);

        }
    }
    private static void MThreadStarter(int id,List<Thread> threadList){
        Thread t1 = new Thread(new M(id,latch));
        t1.setName("M - "+id);
        threadList.add(t1);
    }
    private static void MonneyPennyThreadStarter(int id,List<Thread> threadList){
        Thread t1 = new Thread(new Moneypenny(id,latch));
        t1.setName("MoneyPenny - "+id);
        threadList.add(t1);
    }
    private static void QThreadStarter(List<Thread> threadList){
        Thread QThread = new Thread(new Q(latch));
        threadList.add(QThread);
        QThread.setName("Q");
    }
    private static void IntelligenceThreadStarter(List missionInfoList, List<Thread> threadList){
        Thread IntlijThread = new Thread(new Intelligence(missionInfoList,latch));
        IntlijThread.setName("IntlijThread");
        threadList.add(IntlijThread);
    }
    private static void TimeServiceStarter(int duration){
        Thread timeThread = new Thread(new TimeService(duration));
        timeThread.setName("timeThread");
        timeThread.start();
    }

    public static void outputFiles (String inventoryPath, String diaryPath){
        Inventory.getInstance().printToFile(inventoryPath);
        Diary.getInstance().printToFile(diaryPath);
    }

}