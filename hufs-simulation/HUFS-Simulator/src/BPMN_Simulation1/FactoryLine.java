package BPMN_Simulation1;

import java.text.SimpleDateFormat;

public class FactoryLine {
	private int id;
	private String process;
	private String machine;
	private String time;
	private String status;
	//private String status;
	
	public FactoryLine(int SelectID, String SelectActivity, String SelectMachine, String Time, String Status){
		this.id = SelectID;
		this.process = SelectActivity;
		this.machine = SelectMachine;
		this.time = Time;
		this.status = Status;
		//this.status = Status;
	}
	
	public int getProductID(){
		return id;
	}
	
	public String getProcess(){
		return process;
	}
	
	public String getMachine(){
		return machine;
	}
	
	public String getTime(){
		return time;
	}
	
	public String getStatus(){
		return status;
	}
}
