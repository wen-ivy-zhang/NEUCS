package wenzhang.cs6650.homework4.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.*;

public class WDDMClientHW4 {
	private static List<Long> latencylist = new ArrayList<Long>();
	//public static List<Long> synlatencylist = Collections.synchronizedList(latencylist);
	
	private static Map<Integer, Integer> throughputData = new HashMap<>();
	public static Map<Integer, Integer> synThroughputData =  Collections.synchronizedMap(throughputData);
	public static long clStartTime;
    
	public static void main(String[] args) throws InterruptedException {
		WDDMClientHW4 wddmCl = new WDDMClientHW4();
		OptionParse optionparse = new OptionParse();
		Options options = optionparse.ParseOptions(args); //Parse the inputs and store in options object
		if (options == null) { //invalid input, directly return here since the instructions have already been printed
			return;
		}
		System.out.println("url: " + options.serverURL);  //verify the server URL
		
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 65534);
        clientConfig.property(ClientProperties.READ_TIMEOUT, 200000);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 200000);
        JerseyClient client = JerseyClientBuilder.createClient(clientConfig);
		
		
		//Client client = ClientBuilder.newClient(); // create the HTTP request client
	    WebTarget initialWebtarget = client.target(options.serverURL);
	    // send the initial post request for server to connect to the database and create WDDM table
	    //Response response = initialWebtarget.request().post(Entity.json("")); // GCP request
	    Response response = initialWebtarget.request().post(null); // AWS request
	    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
			String userData = response.readEntity(String.class);
			System.out.println("Initial POST request succeeds : " + userData);
		}
	    else {
			String userData = response.readEntity(String.class);
			System.out.println("Initial POST request fails " + userData);
	    }
	    
		// Create the file object, this file will be used to store all the requests data for later processing
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File("testData.txt");
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile(), true); // append data at the end of the file
			bw = new BufferedWriter(fw);
			//bw.newLine();
			//bw.write("New Client sesson test data collection!");
			//bw.newLine();
		}
		catch (IOException e)
	    {
	        e.printStackTrace();
	        System.out.println("File open error, no data will be collected for this run!!!");
	    }
	        
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Client starting.....Time: " + dtf.format(now));
		
		long starttime = System.currentTimeMillis(); // client start time
		clStartTime = starttime;
		wddmCl.warmup(options, client, bw);
		wddmCl.loading(options, client, bw);
		wddmCl.peak(options, client, bw);
		wddmCl.cooldown(options, client, bw);
		long endtime = System.currentTimeMillis(); // client end time
		long duration = endtime - starttime;
		System.out.println("Client Execution Complete. \n");
		
		// close the BufferedWriter resource
 	    if (bw != null) {
 	    	try {
 	    		bw.close();
 	    	} catch (IOException e) {
 		        e.printStackTrace();
 		        System.out.println("File IO error while closing file!!!");
 		    }
 	    }
		
		System.out.println("======================Execution Summary==========================");
		int totalrequest = ((options.maxnumThreads / 10) * options.numTestsPerPhase * 3 * 5)
				         + ((options.maxnumThreads / 2) * options.numTestsPerPhase * 5 * 5)
				         + ((options.maxnumThreads) * options.numTestsPerPhase * 11 * 5)
				         + ((options.maxnumThreads / 4) * options.numTestsPerPhase * 5 * 5);
		//System.out.println("Total number of request sent: " + totalrequest );
		//System.out.println("Total number of Successful responses: " + WDDMClient.latencylist.size());
		//System.out.println("Test Wall Time: " +  (double)duration / 1000 + " seconds");
		
		// Generate XYLineChart for the overall throughput
		System.out.println("Generating XYLineChart for the overall throughput...");
		if (wddmCl.clientThroughputChart(options) == true) {
			System.out.println("XYLineChart WDDMThroughput.jpeg generated successfully.");
		}
		else {
			System.out.println("Fail to generate XYLineChart for the overall throughput.");
		}
		
		wddmCl.LatencyMeasurement(totalrequest, duration);
 	    
// 	   if (fw != null) {
//	    	try {
//	    		fw.close();
//	    	} catch (IOException e) {
//		        e.printStackTrace();
//		        System.out.println("File IO error while closing file!!!");
//		    }
//	    }
	}
	
	
	public void warmup(Options options, Client client, BufferedWriter bw) throws InterruptedException {
		int numThreads = options.maxnumThreads / 10;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, options, client, 1, bw)).start();
		}
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Warmup phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Warmup phase complete: Time " + (double)duration / 1000 + " seconds");
	}
	
	
	public void loading(Options options, Client client, BufferedWriter bw) throws InterruptedException{
		int numThreads = options.maxnumThreads / 2;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, options, client, 2, bw)).start();
		}	
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Loading phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Loading phase complete: Time " + (double)duration / 1000 + " seconds");	
	}
	
	
	public void peak(Options options, Client client, BufferedWriter bw) throws InterruptedException{
		int numThreads = options.maxnumThreads;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, options, client, 3, bw)).start();
		}
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Peak phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Peak phase complete: Time " + (double)duration / 1000 + " seconds");		
	}
	
	
	public void cooldown(Options options, Client client, BufferedWriter bw) throws InterruptedException{
		int numThreads = options.maxnumThreads / 4;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, options, client, 4, bw)).start();
		}	
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Cooldown phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Cooldown phase complete: Time " + (double)duration / 1000 + " seconds");	
	}
	
	
	public boolean clientThroughputChart(Options options) {
		JfreeChart xyChart = new JfreeChart();
		xyChart.createXYSeries();
		
		for (Map.Entry<Integer, Integer> entry : WDDMClientHW4.synThroughputData.entrySet()) { // loop through the throughput map to add points to the chart
			Integer x = entry.getKey();
			Integer y = entry.getValue();
			xyChart.addPoint(x, y);
		}
		
		int numThreads = (options.maxnumThreads / 10) + (options.maxnumThreads / 2) + (options.maxnumThreads) + (options.maxnumThreads / 4);
		boolean res = xyChart.createChart(numThreads, options.numTestsPerPhase);
		
		return res;
	}
	
	
	public void LatencyMeasurement(Integer totalrequest, long duration) {
		System.out.println("Test Wall Time: " +  (double)duration / 1000 + " seconds");
		System.out.println("Total number of request sent: " + totalrequest );
		
		// now read the testData.txt file and process its stored latencies
		String csvFile = "testData.txt";
	    BufferedReader br = null;
	    String line = "";
	    String cvsSplitBy = ",";
	    
	    try {
	    	br = new BufferedReader(new FileReader(csvFile));
	        while ((line = br.readLine()) != null) {
	        	// use comma as separator
	            String[] latencies = line.split(cvsSplitBy);
	            for (int i = 0; i < latencies.length; i++) {
	            	WDDMClientHW4.latencylist.add(Long.valueOf(latencies[i])); // translate each data to long value of latency
	            }
	        }
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    	System.out.println("testData.txt File Not Found!!!");
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("File IO error while reading file!!!");
	    } finally {
	    	if (br != null) {
	    		try {
	    		    br.close();
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	        }
	    }
		
		System.out.println("Total number of Successful responses: " + WDDMClientHW4.latencylist.size());
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		System.out.println("Overall throughput across all phases: " +  df.format(totalrequest / ((double)duration / 1000)) + " requests per second");
		
		WDDMClientHW4.latencylist.sort((Long x,Long y)->(x.compareTo(y))); // sort the latencies to calculate the mean, median, and percentile information
		long sum = 0;
		int size = WDDMClientHW4.latencylist.size();
		for(int i = 0; i < size; i++) {
			sum = sum + WDDMClientHW4.latencylist.get(i);
		}
		System.out.println("Mean latencies for all requests: " + sum / size + " MilliSeconds*");
		int medianindex = (size - 1) / 2;
		System.out.println("Median latencies for all requests: " + WDDMClientHW4.latencylist.get(medianindex) + " MilliSeconds*");
		int percentindex95 = (int) (size * 0.95);
		int percentindex99 = (int) (size * 0.99);
		System.out.println("95th percentile latency for all requests: " + WDDMClientHW4.latencylist.get(percentindex95) + " MilliSeconds*");
		System.out.println("99th percentile latency for all requests: " + WDDMClientHW4.latencylist.get(percentindex99) + " MilliSeconds*");
	}
}
