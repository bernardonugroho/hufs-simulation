package BPMN_Simulation1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.espertech.esper.client.EPServiceProvider;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SecondProcess implements Runnable {

	protected static Multimap<String, Integer> QueueData = ArrayListMultimap
			.create();

	private ArrayList<String> SaveProcess = new ArrayList<String>(); // �� ��ǰ
																		// ����

	private EPServiceProvider epService;
	private int ProductID; // ���õ� Product ID
	private int TheNumOfProcess = 0; // ������ �� ��° ���� ��Ÿ��
	private long ProcessingTime = 1;
	private String EndPointID = "";

	private String BeforeProcess = "d";
	private String Status = "Complete";
	private String UsingMachineID;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");

	// Queue��Ȳ ��� Variables
	private boolean IsWaiting = false;
	private boolean EndCondition = false;
	private boolean IsWaitingStatus = false;
	private String waitingActivityID = "";

	private ArrayList<String> CheckingMachine = new ArrayList<String>();
	private ArrayList<Integer> EachQueue = new ArrayList<Integer>();

	FactoryLine Event, StartEvent, EndEvent, QueueEvent; // ���� ����� Event
	String Path; // XPDL ���

	public SecondProcess(EPServiceProvider epService, int ProductID) {
		this.epService = epService;
		this.Path = Path;
		this.ProductID = ProductID;
	}

	@Override
	// ESper Engine�� ���� ������
	public synchronized void run() {
		// ��ǰ ID�� ���� Event ����
		MakeEvent me = new MakeEvent(ProductID, Path);

		while (!EndCondition) {
			while (!IsWaiting) {
				try {
					me.MakeTheEvent(BeforeProcess, TheNumOfProcess,
							IsWaitingStatus);

					Event = me.getEvent();
					BeforeProcess = me.getBeforeProcess();
					Status = me.getProductStatus();

					// ���� Condition
					EndPointID = me.getEndPointID();
					if (BeforeProcess.equals(EndPointID)) {

						System.out.println("Check");

						StartEvent = Event;
						epService.getEPRuntime().sendEvent(StartEvent);
						EndCondition = true;

						System.out.println("Final Complete");
						break;
					}

					// Queue�� ���ִ� ��Ȳ
					else if (Status.equals("Waiting")) {
						QueueEvent = Event;
						epService.getEPRuntime().sendEvent(QueueEvent);
						waitingActivityID = me.getWaitActivity();

						// get the reference of the global machines object
						CheckingMachine = me.getAvailableMachine();

						QueueData.put(waitingActivityID, ProductID);

						IsWaiting = true;

						// Processing Time
						/*
						 * ProcessingTime = (long) (me.getProcessTime() * 1000);
						 * System.out.println("Processing Time : " +
						 * me.getProcessTime());
						 */

						// let's wait patiently until any
						// machine-assigned-for-this-activity is ready..
						// System.out.println("\n->current ActivityID" +
						// WaitActivityID);
						// System.out.println("Current Num of registered machine groups: "+MachineRegistrar.getNumOfMachines());
						MachineRegistrar.registerQueingThread(
								QueueEvent.getProcess(), ProductID);
						this.wait();
					}

					// Process ó������ ��Ȳ
					else {
						IsWaitingStatus = false;
						SaveProcess.add(BeforeProcess);

						// EventListener�� ������
						StartEvent = Event;
						epService.getEPRuntime().sendEvent(StartEvent);

						// Processing Time
						ProcessingTime = (long) (me.getProcessTime() * 1000);
						System.out.println("Processing Time : "
								+ me.getProcessTime());

						TheNumOfProcess++;

						//simulating a long process which takes time as long as "ProcessingTime" time unit
						Thread.sleep(ProcessingTime);

						Date nowDate = new Date();
						String EndTime = sdf.format(nowDate);
						Status = "Complete";

						EndEvent = new FactoryLine(StartEvent.getProductID(),
								StartEvent.getProcess(),
								StartEvent.getMachine(), EndTime, Status);
						// Product in Queue entered
						//
						epService.getEPRuntime().sendEvent(EndEvent);

						// Busy Machine ������Ʈ (Idle machine Id ����
						UsingMachineID = me.getMachineID();
						if (UsingMachineID != null) {
							Iterator<String> itr = MachineRegistrar.machineInUse
									.iterator();
							while (itr.hasNext()) {
								String MachineID = (String) itr.next();

								if (MachineID!=null && MachineID.equals(UsingMachineID))
									itr.remove();
							}
						}

						// seems one machine is idle here, let's call
						// the
						// next "patient".. =)

						MachineRegistrar.notifyNextRunnableInQueue(StartEvent
								.getProcess());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			while (IsWaiting == true) {
				EachQueue.addAll(QueueData.get(waitingActivityID));
				// edit: added safety-check -> "EachQueue.size()>0"
				if (EachQueue.size() > 0 && EachQueue.get(0) == ProductID) {
					for (int i = 0; i < CheckingMachine.size(); i++) {
						if (!MachineRegistrar.machineInUse
								.contains(CheckingMachine.get(i))) {

							// removing the first element in "EachQueue" if
							// exist
							Iterator<Integer> itr = EachQueue.iterator();
							while (itr.hasNext()) {
								itr.next();
								itr.remove();
								break;
							}

							QueueData.removeAll(waitingActivityID);

							QueueData.putAll(waitingActivityID, EachQueue);

							System.out.println("Update Queue : " + QueueData);

							EachQueue.clear();
							IsWaitingStatus = true;
							IsWaiting = false;
							break;
						}
					}
				}
				EachQueue.clear();
			}
		}

	}

}
