package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	private static Inventory instance = new Inventory();
	private Inventory(){
		gadgets = new ArrayList<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return instance;
	}

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
		for(String item:inventory){
			gadgets.add(item);
		}
	}
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget){
		synchronized (this) {
			int index = gadgets.indexOf(gadget);
			if (index != -1) {
				gadgets.remove(index);
				return true;
			}
			return false;
		}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Gadget> which is a
	 * List of all the Gadgets in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
			Gson g = new Gson();
			String inventory = g.toJson(gadgets);
			try (Writer write = new FileWriter(filename)){
				write.write(inventory);
			}
			catch (Exception e){
				System.out.println("filename incorrect");
			}
	}

}
