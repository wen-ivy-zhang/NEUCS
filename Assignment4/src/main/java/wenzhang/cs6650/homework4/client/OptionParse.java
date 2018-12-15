package wenzhang.cs6650.homework4.client;

public class OptionParse {
	
	Options ParseOptions(String[]args){
		Integer maxnumThreads;
		String serverURL;
		Integer day;
		long userPopulation;
		Integer numTestsPerPhase;
		
		int n = args.length;
		if(n != 5) {
			System.out.println("Invalid input, Please enter the following arguments in this order:");
			System.out.println(" 1. Maximum number of threads (default 64)");
			System.out.println(" 2. URL of server");
			System.out.println(" 3. Day number to generate data for (default 1)");
			System.out.println(" 4. User Population (default 100,000)");
			System.out.println(" 5. Number of tests/phase (default 100)");
			return null;
		}
		
		// Maximum number of threads (default 64)
		try{
			maxnumThreads = Integer.valueOf(args[0]);
		}
		catch(NumberFormatException ex) {
			maxnumThreads = 64;
		}
		
		// URL of server
		serverURL = args[1];

		// Day number to generate data for (default 1)
		try {
			day = Integer.valueOf(args[2]);
		}
		catch(NumberFormatException ex) {
			day = 1;
		}	
		
		// User Population (default 100,000)
		try {
			userPopulation = Long.valueOf(args[3]);
		}
		catch(NumberFormatException ex) {
			userPopulation = 100000;
		}
		
		// Number of tests/phase (default 100)
		try {
			numTestsPerPhase = Integer.valueOf(args[4]);
		}
		catch(NumberFormatException ex) {
			numTestsPerPhase = 100;
		}
		
		//SeverURL1 = "http://" + serverURL + ":" + day + "/homework1/webapi/myresource";
		
		Options result = new Options(maxnumThreads, serverURL, day, userPopulation, numTestsPerPhase);
		
		return result;
	}

}
