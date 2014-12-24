package BPMN_Simulation1;

import java.util.ArrayList;
import java.util.HashMap;

import com.espertech.esper.client.EPServiceProvider;

/**
 * This class holds the (global) reference to machines object.
 * (each activity will have one (single) global list-of-machines object).
 * 
 * @author Yohan
 *
 */
public class MachineRegistrar {
	//just moving this variable from "SecondProcess" class to this class for organizing things a bit..
	public static volatile ArrayList<String> machineInUse = new ArrayList<String>(); // list of machine in use by the running processes
	
	private static HashMap<String, ArrayList<MyThread>> qThreads = new HashMap<>();
	private static HashMap<Integer, MyThread> listThreads = new HashMap<>();
	
	public static synchronized void registerQueingThread(String activityID, int runnablePID){
		ArrayList<MyThread> qt = qThreads.get(activityID);
		if(qt==null){
			qt = new ArrayList<>();
			qThreads.put(activityID, qt);
		}
		qt.add(listThreads.get(runnablePID));
	}
	
	public static synchronized void notifyNextRunnableInQueue(String activityID){
		ArrayList<MyThread> q = qThreads.get(activityID);
		if(q!=null && q.size()>0){
			q.get(0).doNotify();
			q.remove(0);
		}
	}
	
	public static void createNewProductThread(EPServiceProvider epService, int ProductID){
		Runnable Run = new SecondProcess(epService, ProductID);
		MyThread t1 = new MyThread(Run);
		listThreads.put(ProductID, t1);
		t1.start();
	}
}


