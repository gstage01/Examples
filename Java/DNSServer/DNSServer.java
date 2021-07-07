/* Spring 2018 CSci4211: Introduction to Computer Networks
** This program serves as the server of DNS query.
** Written in Java. */

//Garett Stage
//stage043

// *** NOTE: this code contains skeleton code for creating socket connections. Comments were added to show the parts created by me. 

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.regex.*;
import java.util.*;

class DNSServer {
	public static void main(String[] args) throws Exception {
		int port = 5001;
		ServerSocket sSock = null;

		try {
			sSock = new ServerSocket(5001); // Try to open server socket 5001.
		} catch (Exception e) {
			System.out.println("Error: cannot open socket");
			System.exit(1); // Handle exceptions.
		}

		System.out.println("Server is listening...");
		new monitorQuit().start(); // Start a new thread to monitor exit signal.

		while (true) {
			new dnsQuery(sSock.accept()).start();
		}
	}
}

class dnsQuery extends Thread {
	//Begin code written by me
	Socket sSock = null;
	BufferedReader inputStream;
	PrintWriter outStream;
	String request = "";
	String path = "";
	File cache = new File("DNS_mapping.txt");
	boolean hostFound = false;
	Socket aSock = null;
	dnsQuery(Socket sSock) {
    	this.sSock = sSock;

    }
    public String IPselection(String[] ipList) {
		//checking the number of IP addresses in the cache
		//if there is only one IP address, return the IP address
		//if there are multiple IP addresses, select one and return.
		////bonus project: return the IP address according to the Ping value for better performance (lower latency)
		String currentBest = "";
		long bestTime = 50000;
		if (ipList.length == 1) {
			return ipList[0];
		} else {
			try {
				
				for (int i=0; i<ipList.length; i++) {
					//BONUS: checks ping time for each IP. Significantly lowers speed, however.
					long pingTime = System.currentTimeMillis();
					InetAddress.getByName(ipList[i]).isReachable(2000);
					pingTime = System.currentTimeMillis() - pingTime;
					//If one is faster than another, it returns that IP.
					if (pingTime < bestTime) {
						currentBest = ipList[i];
						bestTime = pingTime;
					}

				}
				return currentBest;
			} catch (Exception e) {}
			return "";
		}
    }
	@Override public void run() {

		try {
			//Open an input stream and an output stream for the socket
			inputStream = new BufferedReader(new InputStreamReader(sSock.getInputStream()));
			outStream = new PrintWriter(sSock.getOutputStream());
			//Read requested query from socket input stream
			request = inputStream.readLine();

			//Check for valid input
			Pattern regex = Pattern.compile("(www)?.[a-zA-Z0-9\\-]+.com");
			Matcher m = regex.matcher(request);
			boolean valid = m.matches();
			if (!valid) {
				outStream.print("Invalid Format\n");
				outStream.flush();
				inputStream.close();
				outStream.close();
				sSock.close();
				return;
			}
			//Parse input from the input stream

			//check the DNS_mapping.txt to see if the host name exists
			//set local file cache to predetermined file.
			//create file if it doesn't exist
			if (!cache.exists()) {
				cache.createNewFile();
			}

			//if it does exist, read the file line by line to look for a
			//match with the query sent from the client
			BufferedReader filecontents = new BufferedReader(new FileReader(cache));
			String current;
			String currName;
			boolean found = false;
			while ((current = filecontents.readLine()) != null) {
				currName = current.split(":")[0];
				if (currName.equals(request)) {
					found = true;
					break;
				}
			}
			if (!found) {
				//If no lines match, query the local machine DNS lookup to get the IP resolution
				//write the response in DNS_mapping.txt
				try {
					//Check the requested query
					request += ":" + InetAddress.getByName(request).getHostAddress();
				} catch (UnknownHostException e) {
					//If the host isn't found, send error and close all
					outStream.print("Host not found\n");
					outStream.flush();
					inputStream.close();
					outStream.close();
					sSock.close();
					return;
				}
				//Store in the cache
				//Send requested information
				PrintWriter store = new PrintWriter(new FileWriter(cache, true), true);
				store.write(request + "\n");
				store.flush();
				request = "Root DNS:" + request;
			} else {
				//If match, use the entry in cache.
				//Format input from cache, select response address, and send response
				String[] currList = current.split(":");
				String[] thisIpList = Arrays.copyOfRange(currList, 1, currList.length);

				//However, we may get multiple IP addresses in cache, so call dnsSelection to select one.
				String outAddress = IPselection(thisIpList);
				System.out.println(request);
				request = "Local DNS:" + request + ":" + outAddress;
			}


			try {

				//print response to the terminal
				//send the response back to the client
				System.out.println(request);
				outStream.print(request + "\n");
				outStream.flush();

			} catch (Exception e) {
				System.out.println("exception: " + e);
			}
			//Close the server socket.
			sSock.close();
			//Close the input and output streams.
			inputStream.close();
			outStream.close();
			
		//End code written by me
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Host not found.\n" + e);
		}
	}
}

class monitorQuit extends Thread {
	@Override
	public void run() {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(System.in)); // Get input from user.
		String st = null;
		while(true){
			try{
				st = inFromClient.readLine();
			} catch (IOException e) {
			}
            if(st.equalsIgnoreCase("exit")){
                System.exit(0);
            }
        }
	}
}

