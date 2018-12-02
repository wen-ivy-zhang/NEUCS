package wenzhang.cs6650.homework2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import wenzhang.cs6650.homework2.server.DataSource;

import java.sql.*;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Alive!";
    }
    
    @POST
	@Consumes(MediaType.TEXT_PLAIN)
	public String postText(String content) {
		//return (content.length());
    	return content;
	}
    
    public static void main(String[] args) throws InterruptedException {
    	try {
    		// Create a connection and statement
    		Connection myConn = DataSource.getInstance().getConnection();
        	Statement myStmt = myConn.createStatement();
        	
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
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		String reason = e.getMessage();
    		System.out.println("Reason: " + reason);
    	}
    	
    	System.out.println("Initial WDDM Database connected!");
    	
 /*   	try {
    		// Get connection to Database
    		String dbUrl = "jdbc:mysql://localhost:3306/Wearable_devices";
        	String user = "root2";
        	String pass = "root2";
        	Connection myConn = DriverManager.getConnection(dbUrl, user, pass);
        	
        	// Create a statement
        	Statement myStmt = myConn.createStatement();
        	
        	// Execute SQL query
        	//ResultSet myRs = myStmt.executeQuery("SELECT * from employee");
        	ResultSet myRs = myStmt.executeQuery("SELECT * FROM Users WHERE UserId = 1");
        	
        	// Process results
//        	while (myRs.next()) {
//        		System.out.println("id: " + myRs.getString("id") + " name: " + myRs.getString("name") 
//        		                          + " DOB: " + myRs.getString("dob") +  " Email: " + myRs.getString("email"));
//        	}
        	
//        	// Insert Data into table
//        	String userData1 = "INSERT INTO employee (id, name, dob, email)"
//        			         + " VALUES (5, 'Edward', '2015-05-05', 'Edward@gmail.com')";
//        	
//        	myStmt.executeUpdate(userData1);
//        	System.out.println("Insert Complete");
        	
        	// Update Users table
        	if (myRs.next()) {
        		System.out.println("checkpoint1");
        	    int id = myRs.getInt("UserId");
        	    System.out.println("id: " + id);
        	}
        	else {
        		// UserId doesn't exist yet
    	    	String UserIDQuery = "INSERT INTO Users(UserId) VALUES(1)";
    	    	myStmt.executeUpdate(UserIDQuery);
    	    	System.out.println("checkpoint2");
        	}
        	
        	// Update Days table
        	myRs = myStmt.executeQuery("SELECT * FROM Days WHERE UserId = 1 AND DAY = 1");
        	if (myRs.next()) {
        		System.out.println("checkpoint11");
        	    int day = myRs.getInt("DAY");
        	    int stepCount = myRs.getInt("StepCount");
        	    System.out.println("day: " + day + " stepCount: " + stepCount);
        	}
        	else {
        		// UserId doesn't exist yet
    	    	String UserDayQuery = "INSERT INTO Days (DAY, UserId, StepCount) VALUES(1, 1, 2000)";
    	    	myStmt.executeUpdate(UserDayQuery);
    	    	System.out.println("checkpoint12");
        	}
        	
        	
        	// Update TimeIntervals table
        	myRs = myStmt.executeQuery("SELECT * FROM Intervals WHERE UserId = 1 AND DAY = 1 AND Intervals");
        	if (myRs.next()) {
        		System.out.println("checkpoint11");
        	    int day = myRs.getInt("DAY");
        	    int stepCount = myRs.getInt("StepCount");
        	    System.out.println("day: " + day + " stepCount: " + stepCount);
        	}
        	else {
        		// UserId doesn't exist yet
    	    	String UserDayQuery = "INSERT INTO Days (DAY, UserId, StepCount) VALUES(1, 1, 2000)";
    	    	myStmt.executeUpdate(UserDayQuery);
    	    	System.out.println("checkpoint12");
        	}
        	
        	System.out.println("checkpoint13");
        	
    	}
    	catch (SQLException e) {
    		//e.printStackTrace();
    		String reason = e.getMessage();
    		System.out.println("Insertion Fail, reason: " + reason);
    	}  */

    }
}
