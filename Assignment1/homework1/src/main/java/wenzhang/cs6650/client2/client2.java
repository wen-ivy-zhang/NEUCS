package wenzhang.cs6650.client2;


import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class client2 {
	
	private static List<Long> latencylist = new ArrayList<Long>();
	public static List<Long> synlatencylist = Collections.synchronizedList(latencylist);
	
    
	public static void main(String[] args) throws InterruptedException {
		client2 cl2 = new client2();
		OptionParse optionparse = new OptionParse();
		Options options = optionparse.ParseOptions(args);	
		Client client = ClientBuilder.newClient();
	    WebTarget webtarget = client.target(options.SeverURL1);
	    System.out.println("url: " + options.SeverURL1);
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Client starting.....Time: " + dtf.format(now));
		
		long starttime = System.currentTimeMillis();
		cl2.warmup(options.maxnumThreads, options.numIterations, webtarget);
		cl2.loading(options.maxnumThreads, options.numIterations, webtarget);
		cl2.peak(options.maxnumThreads, options.numIterations, webtarget);
		cl2.cooldown(options.maxnumThreads, options.numIterations, webtarget);
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		
		System.out.println("======================Sept4==========================");
		int totalrequest = (options.maxnumThreads/ 10 + options.maxnumThreads/2 + options.maxnumThreads + options.maxnumThreads/4)
				* options.numIterations * 2; 
		System.out.println("Total number of request sent: " + totalrequest );
		System.out.println("Total number of Successful responses: " + client2.latencylist.size());
		System.out.println("Test Wall Time: " +  (double)duration/1000 + " seconds");
		
		System.out.println("======================Sept5==========================");
		cl2.LatencyMeasurement(totalrequest,duration);
		
 	    
	}
	
	
	public void warmup(Integer maxnumThreads, Integer numIteration, WebTarget webtarget) throws InterruptedException {
		int numThreads = maxnumThreads / 10;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, numIteration, webtarget)).start();
		}
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Warmup phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Warmup phase complete: Time " + (double)duration/1000 + " seconds");
	}
	
	
	public void loading(Integer maxnumThreads, Integer numIteration, WebTarget webtarget) throws InterruptedException{
		int numThreads = maxnumThreads / 2;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, numIteration, webtarget)).start();
		}	
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Loading phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Loading phase complete: Time " + (double)duration/1000 + " seconds");	
	}
	
	
	public void peak(Integer maxnumThreads, Integer numIteration, WebTarget webtarget) throws InterruptedException{
		int numThreads = maxnumThreads;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, numIteration, webtarget)).start();
		}
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Peak phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Peak phase complete: Time " + (double)duration/1000 + " seconds");		
	}
	
	
	public void cooldown(Integer maxnumThreads, Integer numIteration, WebTarget webtarget) throws InterruptedException{
		int numThreads = maxnumThreads / 4;
		CountDownLatch cdls = new CountDownLatch(1);
		CountDownLatch cdle = new CountDownLatch(numThreads);
		for(int i = 0; i < numThreads; i++) {
			new Thread(new Worker(cdls, cdle, numIteration, webtarget)).start();
		}	
		cdls.countDown();
		long starttime = System.currentTimeMillis();
		System.out.println("Cooldown phase: All threads running.....");
		cdle.await();
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		System.out.println("Cooldown phase complete: Time " + (double)duration/1000 + " seconds");	
		
	}
	
	public void LatencyMeasurement(Integer totalrequest, long duration) {
		System.out.println("Test Wall Time: " +  (double)duration/1000 + " seconds");
		System.out.println("Total number of request sent: " + totalrequest );
		System.out.println("Total number of Successful responses: " + client2.latencylist.size());
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		System.out.println("Overall throughput across all phases:" +  df.format(totalrequest/((double)duration/1000)) + " requests per second");
		client2.latencylist.sort((Long x,Long y)->(x.compareTo(y)));
		long sum = 0;
		int size = client2.latencylist.size();
		for(int i = 0; i < size; i++) {
			sum = sum + client2.latencylist.get(i);
		}
		System.out.println("Mean latencies for all requests: " + sum/size + " MilliSeconds*");
		int medianindex = (size-1)/2;
		System.out.println("median latencies for all requests: " + client2.latencylist.get(medianindex) + " MilliSeconds*");
		int percentindex95 = (int) (size * 0.95);
		int percentindex99 = (int) (size * 0.99);
		System.out.println("95th percentile latency for all requests: " + client2.latencylist.get(percentindex95) + " MilliSeconds*");
		System.out.println("99th percentile latency for all requests: " + client2.latencylist.get(percentindex99) + " MilliSeconds*");	
	}
	
	
	
	
	
	
	
	
	
	

}
