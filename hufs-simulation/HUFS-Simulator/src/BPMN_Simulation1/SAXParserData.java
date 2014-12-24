package BPMN_Simulation1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SAXParserData {
	
	ArrayList<String> TotalTimeList = new ArrayList();
	ArrayList<String> ProcessTimeList = new ArrayList();
	ArrayList<String> MaxTimeList = new ArrayList();
	ArrayList<String> MinTimeList = new ArrayList();
	ArrayList StartPoint = new ArrayList();										// 맨 처음 시작되는 Activity들

	Multimap<String, String> Activity = ArrayListMultimap.create();				// 총 Activity
	Multimap<String, String> ActivityTime = ArrayListMultimap.create();			// 각 Activity 시간 (Key : 해당 Activity, Value : Processing Time, Min, Max)
	Multimap<String, String> ActivityMachine = ArrayListMultimap.create();		// 각 Activity의 Machine들 (Key : 해당 Activity, Value : Machine)

	Multimap<String, String> Machine = ArrayListMultimap.create();				// 전체 Machine
	Multimap<String, String> Process = ArrayListMultimap.create();				// 전체 Process (Key : 해당 Activity, Value : 다음 번 갈 수 있는 Activity)
	
	public void ReadData() {

		try {
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			SAXHandler handler = new SAXHandler();
			parser.parse("Diagram 2.xpdl ", handler);//diagram 2 ElectronicMfg
			// parser.parse(ClassLoader.getSystemResourceAsStream("xml/employee.xml"),
			// handler);

			// Printing the list of employees obtained from XML
			

			for (Time emp : handler.TimeList) {
				TotalTimeList.add(emp.ActivityTime);
				
			}
						
			// ProcessTime, MaxTime, MinTime input Data (parsing) from XPDL
			for(int i=0; i<TotalTimeList.size()-2;i++){
				int j = i + 1;
				int k = i + 2;
				
				ProcessTimeList.add(TotalTimeList.get(i));
				MinTimeList.add(TotalTimeList.get(j));
				MaxTimeList.add(TotalTimeList.get(k));
				
				i = k;
			}
			
			//System.out.println(ProcessTimeList);
			//System.out.println(MinTimeList);
			//System.out.println(MaxTimeList);
			
			int i = 0;
			//System.out.println(ProcessTimeList);
			for (Activity emp : handler.ActivityList) {
				Activity.put(emp.Id, emp.act);
				if (!emp.act.equalsIgnoreCase("Start") && !emp.act.equalsIgnoreCase("End")) {
					ActivityMachine.putAll(emp.Id, emp.machine);
					ActivityTime.put(emp.Id, ProcessTimeList.get(i));
					ActivityTime.put(emp.Id, MinTimeList.get(i));
					ActivityTime.put(emp.Id, MaxTimeList.get(i));
					//System.out.println(i);
					//System.out.println(ActivityTime);
					i++;
				}
				
			}
			
			//System.out.println(ActivityTime);
			
			for (Machine emp : handler.MachineList) {
				Machine.put(emp.Id, emp.Machine);
			}

			for (Transition emp : handler.TransitionList) {
				Process.put(emp.From, emp.To);
				if (!StartPoint.contains(emp.From))
					StartPoint.add(emp.From);
			}

			for (int j = 0; j < Process.values().size(); j++) {
				for (int j1 = 0; j1 < StartPoint.size(); j1++) {
					if (Process.containsValue(StartPoint.get(j1)) == true) {
						StartPoint.remove(StartPoint.get(j1));
					}
				}
			}
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
		}
	}
	
	public Multimap getActivity(){
		return Activity;
	}
	
	public Multimap getActivityTime(){
		return ActivityTime;
	}
	
	public Multimap getActivityMachine(){
		return ActivityMachine;
	}
	
	public Multimap getMachine(){
		return Machine;
	}
	
	public Multimap getProcess(){
		return Process;
	}
	
	public ArrayList getStartPoint(){
		return StartPoint;
	} 
	
	
	// SAXHandler Class
	class SAXHandler extends DefaultHandler {

		List<Activity> ActivityList = new ArrayList<>();
		List<Machine> MachineList = new ArrayList<>();
		List<Time> TimeList = new ArrayList<>();
		List<Transition> TransitionList = new ArrayList<>();

		Activity proc = null;
		Machine mach = null;
		Time ProcTime = null;
		Transition Tran = null;

		String content = null;
		String ProcessTime;
		String MaxTime, MinTime;

		@Override
		// Triggered when the start of tag is found.
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			switch (qName) {
			// Create a new Employee object when the start tag is found
			case "Activity":
				proc = new Activity();
				proc.Id = attributes.getValue("Id");
				proc.act = attributes.getValue("Name");
				break;
			case "Participant":
				mach = new Machine();
				mach.Id = attributes.getValue("Id");
				mach.Machine = attributes.getValue("Name");
				break;
			case "ExtendedAttribute":
				ProcessTime = attributes.getValue("Value");
				
				if (ProcessTime != null) {
					ProcTime = new Time();
					ProcTime.ActivityTime = ProcessTime;
				}
				break;
			case "Transition":
				Tran = new Transition();
				Tran.From = attributes.getValue("From");
				Tran.To = attributes.getValue("To");
				break;
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			switch (qName) {
			// Add the employee to list once end tag is found
			case "Activity":
				ActivityList.add(proc);
				break;
			// For all other end tags the employee has to be updated.
			case "Performer":
				proc.machine.add(content);
				break;
			case "Participant":
				MachineList.add(mach);
				break;
			case "ExtendedAttribute":
				if (ProcessTime != null) {
					TimeList.add(ProcTime);
				}
				break;
			case "Transition":
				TransitionList.add(Tran);
				break;
			/*
			 * case "name": proc.name = content; break; case "gender":
			 * proc.gender = content; break; case "role": proc.role = content;
			 * break;
			 */
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			content = String.copyValueOf(ch, start, length).trim();
		}
		
		
	}
}
