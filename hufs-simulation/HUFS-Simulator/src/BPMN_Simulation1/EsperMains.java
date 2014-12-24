package BPMN_Simulation1;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;


public class EsperMains {
	public static void main(String[] args){
		// 구성하기
		Configuration config = new Configuration();
		config.addEventTypeAutoName("BPMN_Simulation1");
		
		// Query 문 만들기
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
		String epl = "select * from FactoryLine";// output all every 2 seconds
		EPStatement statement = epService.getEPAdministrator().createEPL(epl);
		
		// 출력하기
		EventListener listener = new EventListener();
		statement.addListener(listener);
	
		// 출력할 Event 생성하기
		Runnable r2 = new FirstProcess(epService);
		Thread t1= new Thread(r2);
		t1.start();
				
	}
}
