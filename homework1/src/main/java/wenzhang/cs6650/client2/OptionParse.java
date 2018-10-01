package wenzhang.cs6650.client2;

public class OptionParse {
	
	Options ParseOptions(String[]args){
		Integer maxnumThreads;
		Integer numIterations;
		String  IPaddress;
		Integer Port;
		String  SeverURL1;
		int n = args.length;
		if(n != 4) {
			System.out.println("Invalid input,Please enter the following arguments in this order:"
					+ " 1. Maximum number of threads(please enter d for default value)"
					+ " 2.Number of iterations per thread (please enter d for default value)"
					+ " 3.IP address of server"
					+ " 4.Port used on server(please enter d for default value)");
		}
		String first = args[0];
		if(first.equals("d")) {maxnumThreads = 50;}
		else {
			try{
				maxnumThreads = Integer.valueOf(first);
			}
			catch(NumberFormatException ex) {
				maxnumThreads = 50;
			}
		}
		
		
		String second = args[1];
		if(second.equals("d")) {numIterations = 100;}
		else {
			try {
				numIterations = Integer.valueOf(second);
			}
			catch(NumberFormatException ex) {
				numIterations = 100;
			}
		}
		
		
		
		String third = args[2];
		IPaddress = third;
		
		String forth = args[3];
		if(forth.equals("d")) {Port = 8080;}
		else {
			try {Port = Integer.valueOf(args[3]);}
			catch(NumberFormatException ex) {
				Port = 8080;
			}
		}
		
		SeverURL1 = "http://" + IPaddress + ":" + Port + "/homework1/webapi/myresource";
		//SeverURL1 = "https://qb8qmgix7i.execute-api.us-west-2.amazonaws.com/Prod/ping";
		
		Options result = new  Options(maxnumThreads, numIterations, IPaddress, Port, SeverURL1);
		
		return result;
	}

}
