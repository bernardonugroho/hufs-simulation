package BPMN_Simulation1;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;


public class EsperMains {
	public static void main(String[] args){
		// �����ϱ�
		Configuration config = new Configuration();
		config.addEventTypeAutoName("BPMN_Simulation1");
		
		// Query �� �����
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
		String epl = "select * from FactoryLine";// output all every 2 seconds
		EPStatement statement = epService.getEPAdministrator().createEPL(epl);
		
		// ����ϱ�
		EventListener listener = new EventListener();
		statement.addListener(listener);
	
		// ����� Event �����ϱ�
		Runnable r2 = new FirstProcess(epService);
		Thread t1= new Thread(r2);
		t1.start();
				
	}
}
