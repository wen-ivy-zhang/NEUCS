package wenzhang.cs6650.homework2.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Worker implements Runnable{
	private final CountDownLatch startSignal;
	private final CountDownLatch finishSignal;
	private Options options;
	private Client client;
	private Integer phase; // phase number: 1. warmup; 2. loading; 3. peak; 4. cooldown
	private Integer phaseStartHour;
	private Integer phaseLen;
	private BufferedWriter bw;
	
	Worker(CountDownLatch startSignal, CountDownLatch doneSignal, Options options, Client client, Integer phase, BufferedWriter bw){
		this.startSignal = startSignal;
		this.finishSignal = doneSignal;
		this.options = options;
		this.client = client;
		this.phase = phase;
		this.bw = bw;
		
		if (this.phase == 1) { // warmup phase
			phaseStartHour = 0;
			phaseLen = 3;
		}
		else if (this.phase == 2) { // loading phase
			phaseStartHour = 3;
			phaseLen = 5;
		}
		else if (this.phase == 3) { // peak phase
			phaseStartHour = 8;
			phaseLen = 11;
		}
		else { // cooldown phase
			phaseStartHour = 19;
			phaseLen = 5;
		}
	}
	
	@Override
	public void run(){
		try {
			int numIteration = this.options.numTestsPerPhase * this.phaseLen;
		    long leftLimit = 0L;
		    long rightLimit = this.options.userPopulation;
			Random rand = new Random();
			List<Long> latencylist = new ArrayList<Long>(); // latencylist used to store all the request data within this thread execution
			List<Integer> throughput = new ArrayList<Integer>(); // throughput list used to store all the relative request time stamp within this thread execution
			startSignal.await(); // wait for the main thread start signal

			for(int i = 0; i < numIteration; i++) {
				// Generate 3 random User data
				long userID1 = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
				long userID2 = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
				long userID3 = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
				Integer timeInterval1 = rand.nextInt(this.phaseLen) + this.phaseStartHour;
				Integer timeInterval2 = rand.nextInt(this.phaseLen) + this.phaseStartHour;
				Integer timeInterval3 = rand.nextInt(this.phaseLen) + this.phaseStartHour;
				Integer stepCount1 = rand.nextInt(5001); // StepCount is an integer between 0 and 5000
				Integer stepCount2 = rand.nextInt(5001); // StepCount is an integer between 0 and 5000
				Integer stepCount3 = rand.nextInt(5001); // StepCount is an integer between 0 and 5000
				
				// Generate the corresponding request URLs
				String postURL1 = this.options.serverURL + "/" + userID1 + "/" + this.options.day + "/" + timeInterval1 + "/" + stepCount1;
				String postURL2 = this.options.serverURL + "/" + userID2 + "/" + this.options.day + "/" + timeInterval2 + "/" + stepCount2;
				String postURL3 = this.options.serverURL + "/" + userID3 + "/" + this.options.day + "/" + timeInterval3 + "/" + stepCount3;
				String getURL1 = this.options.serverURL + "/current/" + userID1;
				String getURL2 = this.options.serverURL + "/single/" + userID2 + "/" + this.options.day;
				
				// Now performing corresponding requests
				long poststart1 = System.currentTimeMillis();
				long rq1 = (poststart1 - WDDMClient.clStartTime) / 1000; // get current request time stamp and scale it to second relative to client start time
				throughput.add((int) rq1); // add the relative time stamp into throughput list
				String postres1 = this.postIt(postURL1);	
				if(postres1.equals("Success")) {
					long postend1 = System.currentTimeMillis();
					long postlatency1 = postend1 - poststart1;
					//WDDMClient.synlatencylist.add(postlatency1);
					latencylist.add(postlatency1);
				}
				
				long poststart2 = System.currentTimeMillis();
				long rq2 = (poststart2 - WDDMClient.clStartTime) / 1000; // get current request time stamp and scale it to second relative to client start time
				throughput.add((int) rq2); // add the relative time stamp into throughput list
				String postres2 = this.postIt(postURL2);	
				if(postres2.equals("Success")) {
					long postend2 = System.currentTimeMillis();
					long postlatency2 = postend2 - poststart2;
					//WDDMClient.synlatencylist.add(postlatency2);
					latencylist.add(postlatency2);
				}
				
				long getstart1 = System.currentTimeMillis();
				long rq3 = (getstart1 - WDDMClient.clStartTime) / 1000; // get current request time stamp and scale it to second relative to client start time
				throughput.add((int) rq3); // add the relative time stamp into throughput list
				String getres1 = this.getIt(getURL1);
				if(getres1.equals("Success")) {
					long getend1 = System.currentTimeMillis();
					long getlatency1 = getend1 - getstart1;
					//WDDMClient.synlatencylist.add(getlatency1);
					latencylist.add(getlatency1);
				}
				
				long getstart2 = System.currentTimeMillis();
				long rq4 = (getstart2 - WDDMClient.clStartTime) / 1000; // get current request time stamp and scale it to second relative to client start time
				throughput.add((int) rq4); // add the relative time stamp into throughput list
				String getres2 = this.getIt(getURL2);
				if(getres2.equals("Success")) {
					long getend2 = System.currentTimeMillis();
					long getlatency2 = getend2 - getstart2;
					//WDDMClient.synlatencylist.add(getlatency2);
					latencylist.add(getlatency2);
				}
				
				long poststart3 = System.currentTimeMillis();
				long rq5 = (poststart3 - WDDMClient.clStartTime) / 1000; // get current request time stamp and scale it to second relative to client start time
				throughput.add((int) rq5); // add the relative time stamp into throughput list
				String postres3 = this.postIt(postURL3);	
				if(postres3.equals("Success")) {
					long postend3 = System.currentTimeMillis();
					long postlatency3 = postend3 - poststart3;
					//WDDMClient.synlatencylist.add(postlatency3);
					latencylist.add(postlatency3);
				}
			}
			
			// we have executed all the tests in this thread, now we write all the data into file for later processing and analysis.
			// The reason for storing the data into test file is to NOT utilize memory too much as each client test generates millions of data
			// The reason for storing each latency into a temporary list and then write them all together into file is to minimize the
			// File IO effect on each HTTP request, since each File IO is quite slow and we don't want it to slow down request generation. 
			try {
				int size = latencylist.size();
				synchronized (bw) { // synchronize buffer writer here for thread safe writing
					for (int i = 0; i < size; i++) {
						bw.write(Long.toString(latencylist.get(i)) + ","); // each latency is separated with a comma
					}
				}
			}
			catch (IOException e)
		    {
		        e.printStackTrace();
		        System.out.println("File write error, the latencies collected might be partially written into test file!!!");
		    }
			
			// now add the relative throughput data into the throughput map for later generating the XYLine Chart
			for (int i = 0; i < throughput.size(); i++) {
				int timeStamp = throughput.get(i);
				if (WDDMClient.synThroughputData.containsKey(timeStamp)) {
					int preVal = WDDMClient.synThroughputData.get(timeStamp);
					WDDMClient.synThroughputData.put(timeStamp, preVal + 1);
				}
				else {
					WDDMClient.synThroughputData.put(timeStamp, 1);
				}
			}
			
		}
		catch(InterruptedException ex) {}
		finally {
			this.finishSignal.countDown();	
		}
	}
	
	
	String getIt(String requestURL) {
		WebTarget target = this.client.target(requestURL);
		String result = "Success";
		try {
			Response response = target.request().get(Response.class);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				String userData = response.readEntity(String.class);
				//System.out.println("GET UserData: " + userData);
				result = "Success";
			}
			else {
				result = "Fail";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	String postIt(String requestURL) {
		WebTarget target = this.client.target(requestURL);
		String result = "Success";
		
		try {
			Response response = target.request().post(null);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				String userData = response.readEntity(String.class);
				//System.out.println("POST UserData " + userData);
				result = "Success";
			}
			else {
				result = "Fail";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
