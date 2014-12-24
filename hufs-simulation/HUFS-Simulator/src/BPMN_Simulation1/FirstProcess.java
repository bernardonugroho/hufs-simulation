package BPMN_Simulation1;

import java.util.ArrayList;
import java.util.Random;

import com.espertech.esper.client.EPServiceProvider;

public class FirstProcess implements Runnable {
	private EPServiceProvider epService;
	private String Path;
	private int ProductID; // 제품 ID
	private static int NumOfProduct = 10;
	private long InterArrivalTime = 0; // Product공정에 들어오는 간격 시간
										// (Distribution)

	private ArrayList<Integer> uniquePID = new ArrayList<Integer>();

	public FirstProcess(EPServiceProvider epService) {
		this.epService = epService;
		this.Path = Path;

	}

	@Override
	public void run() {
		while (uniquePID.size() < NumOfProduct) {
			try {

				// 제품 랜덤으로 ID 생성
				Random rand = new Random();
				int RandomProductID = rand.nextInt(NumOfProduct) + 1;
				ProductID = RandomProductID;

				// 기존에 ID가 생성되어있는게 있는지 확인
				if (!uniquePID.contains(ProductID)) {
					uniquePID.add(ProductID);

					MachineRegistrar.createNewProductThread(epService,
							ProductID);
					
					/**
					 * What is InterArrivalTime?
					 * I assume this is "only" used for the unreliable queue mechanism using "Thread.sleep".
					 * Thus, I comment it now and you may remove it if my "assumption" is correct.
					 * Otherwise, please adjust accordingly.
					 */
					// InterArrivalTime = 3000;
				} else if (uniquePID.size() == NumOfProduct) {
					System.out.println("Complete FirstProcess");
				}
				
				/**
				 *  --> Waiting for Activity A to complete?
				 *  I assume this is going to be replaced by wait-notify mechanism.
				 *  (It's done in SecondProcess class)
				 */
				// Thread.sleep(InterArrivalTime);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
