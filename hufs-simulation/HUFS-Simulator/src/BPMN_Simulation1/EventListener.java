package BPMN_Simulation1;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EventListener implements UpdateListener{

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		
    	
    	EventBean[] event = new EventBean[newEvents.length];
    	
    	for(int i=0; i<newEvents.length; i++){
    		event[i] = newEvents[i];
    	}
				
		//System.out.println(event1);
		
		for(int i =0; i< newEvents.length; i++){
		System.out.println("productID : "+event[i].get("productID")+
							"    process : "+event[i].get("process") + 
							"	 Machine : "+event[i].get("machine") + 
							"	 Status : "+event[i].get("status") + 
							"    Time : " + event[i].get("time"));
		}
		
    	
	}
	
	
}
