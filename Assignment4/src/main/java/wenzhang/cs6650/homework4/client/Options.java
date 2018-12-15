package wenzhang.cs6650.homework4.client;

public class Options {
	Integer maxnumThreads;
	String serverURL;
	Integer day;
	long userPopulation;
	Integer numTestsPerPhase;
	
	public Options(Integer maxnumThreads, String serverURL, Integer day, long userPopulation, Integer numTestsPerPhase) {
		this.maxnumThreads = maxnumThreads;
		this.serverURL = serverURL;
		this.day = day;
		this.userPopulation = userPopulation;
		this.numTestsPerPhase = numTestsPerPhase;
	}
}
