package BPMN_Simulation1;

import java.util.ArrayList;

class Activity{
	  String Id;
	  String act;
	  ArrayList<String> machine = new ArrayList();
	   
	  @Override
	  public String toString() {
	    return "( "+act+" ) " + "Id : " + Id + "machine : " + machine ; //+age+" - "+name+" - "+gender+" - "+role;
	    
	  }
	}

