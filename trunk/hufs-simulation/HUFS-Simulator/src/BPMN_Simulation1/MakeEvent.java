package BPMN_Simulation1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

// 추가 해야할것 : 각 Machine 시간 있어야함.
public class MakeEvent {

	Multimap<String, String> Data_Activity = ArrayListMultimap.create(); // Activity
	Multimap<String, String> Data_ActivityTime = ArrayListMultimap.create(); // 각
																				// Activity의
																				// 걸리는
																				// 시간
																				// ->
																				// 각
																				// Machine
																				// 타임으로
																				// 바꿔야함
	Multimap<String, String> Data_ActivityMachine = ArrayListMultimap.create(); // 각
																				// Activity에
																				// 해당하는
																				// Machine

	Multimap<String, String> Data_Machine = ArrayListMultimap.create(); // 전체
																		// Machine
	Multimap<String, String> Data_Process = ArrayListMultimap.create(); // 전체
																		// Process

	ArrayList<String> StartPoint = new ArrayList<String>(); // 공정의 첫 부분 Activity

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

		// Queue에 있다가 나온 상황
		if (CheckStatusWait == true) {
			BeforeProcess = activityID;
		}

		// 첫 공정 Activity 선택
		else if (TheNumOfProcess == 0) {
			int RandomStartPoint = rand.nextInt(StartPoint.size());
			activityID = (String) StartPoint.get(RandomStartPoint);
			BeforeProcess = activityID;
			// System.out.println("Test");
		}

		// 2번째 공정 Activity 선택
		else {

			Set<String> keys = Data_Process.keySet();
			int RandomProcess;

			for (String key : keys) {
				// System.out.println("Test");
				if (key.equals(BeforeProcess)) {

					RandomProcess = rand.nextInt(Data_Process.get(key).size()); // 이부분
																				// Capacity해결

					ArrayList ToArrayProcess = new ArrayList();
					ToArrayProcess.addAll(Data_Process.get(key));

					activityID = (String) ToArrayProcess.get(RandomProcess);

					break;
				}

			}
		}

		// Activity ID -> Name으로 전환
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

		// Processing Time 구하기
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
						* (MaxT - MinT + 1)) + MinT); // Uniform 계산

				ProcessingTime = FinalTime;
				break;
			}
		}

		// SelectActivity에 해당하는 Machine 선택
		Set<String> keys = Data_ActivityMachine.keySet();
		for (String key : keys) {
			if (key.equals(activityID)) {
				ArrayList ToArrayMachine = new ArrayList();
				ToArrayMachine.addAll(Data_ActivityMachine.get(key));
				BeforeProcess = activityID;

				// 사용가능한 Machine이 있을때
				for (int i = 0; i < ToArrayMachine.size(); i++) {
					if (!machineInUse.contains(ToArrayMachine.get(i))) {

						// Queue에 있던 Item 우선순위
						if (CheckStatusWait == true) {
							MachineID = (String) ToArrayMachine.get(i);
							machineInUse.add(MachineID);

							break;
						}

						// Queue에 있을 시 Machine seize 못함.
						else if (SecondProcess.QueueData.get(activityID)
								.isEmpty() == true) {
							MachineID = (String) ToArrayMachine.get(i);
							machineInUse.add(MachineID);

							break;
						}
					}
				}

				// 사용가능한 Machine이 없을때
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

		// Machine ID - > Name으로 전환
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