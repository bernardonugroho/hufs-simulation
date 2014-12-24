package BPMN_Simulation1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EventListener2 implements UpdateListener {

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {

		// DB ¿˙¿Â
		Connection conn = null;
		String url = "jdbc:mysql://203.253.70.34:3306/bpi";
		String id = "cmpteam";
		String pw = "!cmpteam";
		String Data_label = "";
		Statement stmt = null;

		try {

			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(url, id, pw);

			stmt = conn.createStatement();

			int message = 0;

			EventBean[] event = new EventBean[newEvents.length];
			System.out.println("lenght : "+newEvents.length);
			
			for (int i = 0; i < newEvents.length; i++) {
				event[i] = newEvents[i];
			}

			// System.out.println(event1);"    Status : "+event[i].get("status")
			// 

			for (int i = 0; i < newEvents.length; i++) {
				//System.out.println("i ="+i);
				//System.out.println(event[i].get("productID"));
				String caseid = String.valueOf(event[i].get("productID"));
				String activity = (String) event[i].get("process");
				String machine = (String) event[i].get("machine");
				String starttime="", endtime="";
				int qty = (int) (Math.random() * 1000);
				while (qty == 0)
					qty = (int) (Math.random() * 1000);
				
				
				if(((String) event[i].get("status")).equalsIgnoreCase("start"))
				{
					starttime = (String) event[i].get("time");
					//insert
					String query_str = "insert into bpi.manufcomplete (caseid, activity, machine, starttime, inputquantity) values ('"
							+ caseid
							+ "','"
							+ activity
							+ "', '"
							+ machine
							+ "','"
							+ starttime + "'," + qty + ")";

					System.out.println(query_str);
					message = stmt.executeUpdate(query_str);
					
					
					
				} else if((((String) event[i].get("status")).equalsIgnoreCase("complete")))
				{
					endtime =(String) event[i].get("time");
					String query_str = "update bpi.manufcomplete set endtime = '"
							+ endtime
							+ "', outputquantity = '"
							+ qty
							+ "' where caseid = '"
							+ caseid
							+ "' AND activity ='"
							+ activity + "' AND machine = '" + machine+ "'";
					System.out.println(query_str);
					message = stmt.executeUpdate(query_str);
				}
				

				

				

				//String qqq = Integer.toString(qty);

			}			
		
			stmt.close();
			conn.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
