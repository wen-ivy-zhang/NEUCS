package wenzhang.cs6650.homework4.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hw4server")
public class WDDMServerHW4 {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
	public String dbCreate() {
    	
    	Connection myConn = null;
    	Statement myStmt = null;
		String res;
    	try {
    		// Create a connection and statement
    		myConn = DataSource.getInstance().getConnection();
        	myStmt = myConn.createStatement();
        	
        	// Create WDDM database and UserData table
        	String dbCreate = "CREATE SCHEMA IF NOT EXISTS WDDM";
        	myStmt.executeUpdate(dbCreate);
        	String useDb = "USE WDDM";
        	myStmt.executeUpdate(useDb);
        	String TableCreate = "CREATE TABLE IF NOT EXISTS UserData( " + 
        						 "UserDataId INT AUTO_INCREMENT, " + 
        						 "UserId INT, " + 
        						 "DayId INT, " + 
        						 "TimeInterval INT, " + 
        						 "StepCount INT, " + 
        						 "CONSTRAINT pk_UserData_UserDataId PRIMARY KEY(UserDataId)" + 
        						 ")";
        	myStmt.executeUpdate(TableCreate);
        	
        	// Set the UserId as the Secondary index to speed up the lookup processes
        	String CreateIndex = "CREATE INDEX idx_userId ON UserData (UserId)";
        	myStmt.executeUpdate(CreateIndex);
        	
        	res = "Initial WDDM Database connected!";
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		res = e.getMessage();
    		//System.out.println("Reason: " + res);
    	}
    	finally {
    		try { if (myStmt != null) myStmt.close(); } catch (Exception e) {res = e.getMessage();}
    		try { if (myConn != null) myConn.close(); } catch (Exception e) {res = e.getMessage();}
    	}
    	
    	return res;
	}
    
    @POST
    @Path("/{userID}/{day}/{timeInterval}/{stepCount}")
    @Produces(MediaType.APPLICATION_JSON)
	public String postUserData(@PathParam("userID") long id, 
			               @PathParam("day") int day, 
			               @PathParam("timeInterval") int timeInterval, 
			               @PathParam("stepCount") int stepCount) {
    	
    	Connection myConn = null;
    	Statement myStmt = null;
    	ResultSet myRs = null;
    	String res;
    	try {
    		// Create a connection and statement
    		myConn = DataSource.getInstance().getConnection();
        	myStmt = myConn.createStatement();
        	String useDb = "USE WDDM";
        	myStmt.executeUpdate(useDb);

        	// No need to check for duplicate entries since in real time this is not going to happen. Let's assume valid inputs.
//        	// Check if the same entry already exists to avoid duplicate
//        	String checkEntry = "SELECT * FROM UserData WHERE UserId = " + id + " AND DayId = " + day + " AND TimeInterval = " + timeInterval;
//        	myRs = myStmt.executeQuery(checkEntry);
//        	if (myRs.next()) {
//        		return "User data: userID: " + id + ", day: " + day + ", timeInterval: " + timeInterval + " already exist!";
//        	}
        	
        	// Continue to insert the user data since it doesn't exist in DB yet
        	String singleUserData = "INSERT INTO UserData (UserId, DayId, TimeInterval, StepCount) " + 
        			                "VALUES (" + id + ", " + day + ", " + timeInterval + ", " + stepCount + ")";
        	myStmt.executeUpdate(singleUserData);
        	res = "POST User data: userID: " + id + ", day: " + day + ", timeInterval: " + timeInterval + ", stepCount: " + stepCount 
        			+ " Successful!";
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		res = e.getMessage();
    		//System.out.println("Reason: " + res);
    	}
    	finally {
    		try { if (myRs != null) myRs.close(); } catch (Exception e) {res = e.getMessage();}
    		try { if (myStmt != null) myStmt.close(); } catch (Exception e) {res = e.getMessage();}
    		try { if (myConn != null) myConn.close(); } catch (Exception e) {res = e.getMessage();}
    	}
    	
    	return res;
	}
	
	@GET
	@Path("/current/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public int getCurrentUserData(@PathParam("userID") long id) {
		
    	Connection myConn = null;
    	Statement myStmt = null;
    	ResultSet myRs = null;
    	String reason;
    	int res = 0;
    	try {
    		// Create a connection and statement
    		myConn = DataSource.getInstance().getConnection();
        	myStmt = myConn.createStatement();
        	String useDb = "USE WDDM";
        	myStmt.executeUpdate(useDb);
        	
        	// get the cumulative number of steps for the most recent day stored for the user
        	String getUserData = "SELECT DayId, SUM(StepCount) AS Steps"
        			           + " FROM UserData WHERE UserId = " + id
        			           + " GROUP BY DayId"
        			           + " ORDER BY DayId DESC LIMIT 1";
        	myRs = myStmt.executeQuery(getUserData);
        	
        	// Process the result
        	if (myRs.next()) {
        		res = myRs.getInt("Steps");
        	}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		reason = e.getMessage();
    		//System.out.println("Reason: " + reason);
    	}
    	finally {
    		try { if (myRs != null) myRs.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myStmt != null) myStmt.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myConn != null) myConn.close(); } catch (Exception e) {reason = e.getMessage();}
    	}
    	
		return res;
	}
	
	@GET
	@Path("/single/{userID}/{day}")
	@Produces(MediaType.APPLICATION_JSON)
	public int getSingleDayUserData(@PathParam("userID") long id, 
			                           @PathParam("day") int day) {
		
    	Connection myConn = null;
    	Statement myStmt = null;
    	ResultSet myRs = null;
    	String reason;
    	int res = 0;
    	try {
    		// Create a connection and statement
    		myConn = DataSource.getInstance().getConnection();
        	myStmt = myConn.createStatement();
        	String useDb = "USE WDDM";
        	myStmt.executeUpdate(useDb);
        	
        	// get the cumulative number of steps for the specified day
        	String getUserData = "SELECT SUM(StepCount) AS Steps FROM UserData WHERE UserId = " + id + " AND DayId = " + day;
        	myRs = myStmt.executeQuery(getUserData);
        	
        	// Process the result
        	if (myRs.next()) {
        		res = myRs.getInt("Steps");
        		
        		//System.out.println("UserId: " + myRs.getInt("UserId") + " DayId: " + myRs.getInt("DayId") + 
        		//		" TimeInterval: " + myRs.getInt("TimeInterval") +  " StepCount: " + tempSteps);
        	}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		reason = e.getMessage();
    		//System.out.println("Reason: " + reason);
    	}
    	finally {
    		try { if (myRs != null) myRs.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myStmt != null) myStmt.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myConn != null) myConn.close(); } catch (Exception e) {reason = e.getMessage();}
    	}
    	
		return res;
	}
	
	@GET
	@Path("/range/{userID}/{startDay}/{numDays}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getRangeDaysUserData(@PathParam("userID") long id, 
			                                 @PathParam("startDay") int startDay, 
			                                 @PathParam("numDays") int numDays) {
		
    	Connection myConn = null;
    	Statement myStmt = null;
    	ResultSet myRs = null;
    	String reason;
		List<Integer> res = new ArrayList<>();
		for (int i = 0; i < numDays; i++) { // initialize the res array to 0 for the numDays
			res.add(0);
		}
		
    	try {
    		// Create a connection and statement
    		myConn = DataSource.getInstance().getConnection();
        	myStmt = myConn.createStatement();
        	String useDb = "USE WDDM";
        	myStmt.executeUpdate(useDb);
        	
        	// get the cumulative number of steps for each specified day
        	int endDay = startDay + numDays - 1;
        	String getUserData = "SELECT DayId, SUM(StepCount) AS Steps"
			                   + " FROM UserData WHERE UserId = " + id
			                   + " AND DayId >= " + startDay + " AND DayId <= " + endDay
			                   + " GROUP BY DayId";
        	myRs = myStmt.executeQuery(getUserData);
        	
        	// Process the results
        	while (myRs.next()) {
        		int tempDay = myRs.getInt("DayId");
        		int tempSteps = myRs.getInt("Steps");
        		
        		int index = tempDay - startDay; // get the index of the specific day
        		//tempSteps += res.get(index);
        		res.set(index, tempSteps); // update the step count at the index for the specific day
        		
        		//System.out.println("UserId: " + myRs.getInt("UserId") + " DayId: " + tempDay + 
        		//		" TimeInterval: " + myRs.getInt("TimeInterval") +  " StepCount: " + tempSteps);
        	} 
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		reason = e.getMessage();
    		//System.out.println("Reason: " + reason);
    	}
    	finally {
    		try { if (myRs != null) myRs.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myStmt != null) myStmt.close(); } catch (Exception e) {reason = e.getMessage();}
    		try { if (myConn != null) myConn.close(); } catch (Exception e) {reason = e.getMessage();}
    	}
    	
		return res;
	}
}
