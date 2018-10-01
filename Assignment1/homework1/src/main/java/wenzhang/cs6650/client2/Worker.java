package wenzhang.cs6650.client2;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Worker implements Runnable{
	private final CountDownLatch startSignal;
	private final CountDownLatch finishSignal;
	private Integer numIteration;
	private WebTarget webtarget;
	
	Worker(CountDownLatch startSignal, CountDownLatch doneSignal, Integer numIteration, WebTarget webtarget){
		this.startSignal = startSignal;
		this.finishSignal = doneSignal;
		this.numIteration = numIteration;
		this.webtarget = webtarget;
	}
	
	@Override
	public void run(){
		try {
			startSignal.await();
			for(int i = 0; i < this.numIteration; i++) {
				long getstart = System.currentTimeMillis();
				String getres = this.getIt();
				if(getres.equals("Alive!")) {
					long getend = System.currentTimeMillis();
					long getlatency = getend - getstart;
					client2.synlatencylist.add(getlatency);
				}
				
				long poststart = System.currentTimeMillis();
				String postres = this.postIt("post input");	
				if(postres.equals("post input")) {
					long postend = System.currentTimeMillis();
					long postlatency = postend - poststart;
					client2.synlatencylist.add(postlatency);
				}
			}	
			
		}catch(InterruptedException ex) {}
		finally {
			this.finishSignal.countDown();	
		}			   	
	}
	
	
	String getIt()throws ClientErrorException{
		WebTarget target = this.webtarget;
		return target.request(MediaType.TEXT_PLAIN).get(String.class);
	}
	
	String postIt(String text)throws ClientErrorException{
		Response response = this.webtarget.request().post(Entity.text(text));
		String result = response.readEntity(String.class);
		return result;	
	}

}
