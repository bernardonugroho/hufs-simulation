package BPMN_Simulation1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

// �߰� �ؾ��Ұ� : �� Machine �ð� �־����.
public class MakeEvent {

	Multimap<String, String> Data_Activity = ArrayListMultimap.create(); // Activity
	Multimap<String, String> Data_ActivityTime = ArrayListMultimap.create(); // ��
																				// Activity��
																				// �ɸ���
																				// �ð�
																				// ->
																				// ��
																				// Machine
																				// Ÿ������
																				// �ٲ����
	Multimap<String, String> Data_ActivityMachine = ArrayListMultimap.create(); // ��
																				// Activity��
																				// �ش��ϴ�
																				// Machine

	Multimap<String, String> Data_Machine = ArrayListMultimap.create(); // ��ü
																		// Machine
	Multimap<String, String> Data_Process = ArrayListMultimap.create(); // ��ü
																		// Process

	ArrayList<String> StartPoint = new ArrayList<String>(); // ������ ù �κ� Activity

	private int TheNumOfProcess;
	private int ProductID;
	private String activityID;
	private String WaitingActivity;
	private ArrayList<String> availableMachine = new ArrayList<String>();
	private String MachineID;
	private String ProcessTime, MinTime, MaxTime;

	private FactoryLine event;
	private double ProcessingTime;
	private String EndPointID;
	private String BeforeProcess; // Before Process
	private String Status;
	private boolean CheckWait = false;

	private ArrayList<String> machineInUse = new ArrayList<String>();

	String SelectActivity;
	String SelectMachine;

	Random rand = new Random();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");

	String Path;

	public MakeEvent(int RandomProductID, String Path) {
		this.Path = Path; // by the way, what is this "Path" for? - so far it's
							// not used -

		SAXParserData SAXParsing = new SAXParserData();
		SAXParsing.ReadData();

		Data_Activity = SAXParsing.getActivity();
		Data_ActivityTime = SAXParsing.getActivityTime();
		Data_ActivityMachine = SAXParsing.getActivityMachine();

		Data_Machine = SAXParsing.getMachine();
		Data_Process = SAXParsing.getProcess();

		StartPoint = SAXParsing.getStartPoint();
		
		this.ProductID = RandomProductID;
	}

	public void MakeTheEvent(String BFProcess, int TheNumOfProcess,
			boolean CheckStatusWait) {
		BeforeProcess = BFProcess;
		machineInUse = MachineRegistrar.machineInUse;
		this.TheNumOfProcess = TheNumOfProcess;
		MachineID = null;
		Status = "Start";
		// System.out.println(BeforeProcess);
		Date nowDate = new Date();

		// Queue�� �ִٰ� ���� ��Ȳ
		if (CheckStatusWait == true) {
			BeforeProcess = activityID;
		}

		// ù ���� Activity ����
		else if (TheNumOfProcess == 0) {
			int RandomStartPoint = rand.nextInt(StartPoint.size());
			activityID = (String) StartPoint.get(RandomStartPoint);
			BeforeProcess = activityID;
			// System.out.println("Test");
		}

		// 2��° ���� Activity ����
		else {

			Set<String> keys = Data_Process.keySet();
			int RandomProcess;

			for (String key : keys) {
				// System.out.println("Test");
				if (key.equals(BeforeProcess)) {

					RandomProcess = rand.nextInt(Data_Process.get(key).size()); // �̺κ�
																				// Capacity�ذ�

					ArrayList ToArrayProcess = new ArrayList();
					ToArrayProcess.addAll(Data_Process.get(key));

					activityID = (String) ToArrayProcess.get(RandomProcess);

					break;
				}

			}
		}

		// Activity ID -> Name���� ��ȯ
		Set<String> Act = Data_Activity.keySet();
		for (String ActKey : Act) {
			if (ActKey.equals(activityID)) {
				ArrayList ActivityList = new ArrayList();
				ActivityList.addAll(Data_Activity.get(ActKey));

				SelectActivity = (String) ActivityList.get(0);

				if (SelectActivity.equals("End")) {
					EndPointID = activityID;
					BeforeProcess = activityID;
					Status = "Complete";
				}
				break;
			}
		}

		// Processing Time ���ϱ�
		Set<String> AT = Data_ActivityTime.keySet();
		for (String key : AT) {
			if (key.equals(activityID)) {

				ArrayList ATList = new ArrayList();
				ATList.addAll(Data_ActivityTime.get(key));

				ProcessTime = (String) ATList.get(0);
				MinTime = (String) ATList.get(1);
				MaxTime = (String) ATList.get(2);

				double MinT = Double.parseDouble(MinTime);
				double MaxT = Double.parseDouble(MaxTime);
				double FinalTime = (Math.floor(Math.random()
						* (MaxT - MinT + 1)) + MinT); // Uniform ���

				ProcessingTime = FinalTime;
				break;
			}
		}

		// SelectActivity�� �ش��ϴ� Machine ����
		Set<String> keys = Data_ActivityMachine.keySet();
		for (String key : keys) {
			if (key.equals(activityID)) {
				ArrayList ToArrayMachine = new ArrayList();
				ToArrayMachine.addAll(Data_ActivityMachine.get(key));
				BeforeProcess = activityID;

				// ��밡���� Machine�� ������
				for (int i = 0; i < ToArrayMachine.size(); i++) {
					if (!machineInUse.contains(ToArrayMachine.get(i))) {

						// Queue�� �ִ� Item �켱����
						if (CheckStatusWait == true) {
							MachineID = (String) ToArrayMachine.get(i);
							machineInUse.add(MachineID);

							break;
						}

						// Queue�� ���� �� Machine seize ����.
						else if (SecondProcess.QueueData.get(activityID)
								.isEmpty() == true) {
							MachineID = (String) ToArrayMachine.get(i);
							machineInUse.add(MachineID);

							break;
						}
					}
				}

				// ��밡���� Machine�� ������
				if (MachineID == null && Status.equals("Start")) {
					WaitingActivity = activityID;

					availableMachine.addAll(ToArrayMachine);
					ProcessingTime = 0;
					String Time = sdf.format(nowDate);
					Status = "Waiting";
					SelectMachine = "Wait";
					event = new FactoryLine(ProductID, SelectActivity,
							SelectMachine, Time, Status);
					return;
				}

			}
		}

		// Machine ID - > Name���� ��ȯ
		Set<String> Mach = Data_Machine.keySet();
		for (String MachKey : Mach) {
			if (MachKey.equals(MachineID)) {
				ArrayList MachineList = new ArrayList();
				MachineList.addAll(Data_Machine.get(MachKey));

				SelectMachine = (String) MachineList.get(0);
				break;
			}
		}

		String Time = sdf.format(nowDate);
		if (SelectActivity.equals("Start") || SelectActivity.equals("End")) {
			SelectMachine = "X";
		}
		event = new FactoryLine(ProductID, SelectActivity, SelectMachine, Time,
				Status);

	}

	public FactoryLine getEvent() {
		return event;
	}

	public double getProcessTime() {
		return ProcessingTime;
	}

	public String getEndPointID() {
		return EndPointID;
	}

	public String getBeforeProcess() {
		return BeforeProcess;
	}

	public String getMachineID() {
		return MachineID;
	}

	public String getProductStatus() {
		return Status;
	}

	public String getWaitActivity() {
		return WaitingActivity;
	}

	/**
	 * note: this is "not" getting "unused" machine(s),
	 * but getting "assigned" machine(s) for a particular activityID
	 * (regardless whether they are currently being used or not)
	 */
	public ArrayList<String> getAvailableMachine() {
		return availableMachine;		
	}
}