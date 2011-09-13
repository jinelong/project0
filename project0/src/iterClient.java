import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

// java iterClient [serverPort] [ownPort]

public class iterClient {

	
	int maxNumber = 4;
	clientInfo  clientList [] = new clientInfo[maxNumber];
	int serverPort;
	int ownPort;
	public int counter = 0;
	
	public iterClient(int sPort, int oPort) throws IOException{
		serverPort = sPort;
		ownPort = oPort;
		
		
		//setup listening server for server info and client message
		receiveServer local = new receiveServer(ownPort);
		local.run();
		
		
	}
	class clientInfo{
		
		public String name;
		public String ip;
		public String chatPort;
	}
	
	
	
	
	
	
	
	class receiveServer implements Runnable{
		ServerSocket server = null;
		int port;
		
		public receiveServer(int i) throws IOException{
			port = i;
			server = new ServerSocket(port);
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Socket temp = null;
			while(true){
			try {
				 temp = server.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			  try {
			        BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));
			        // sample message from server:
			        // server@heartbeat
			        
			        // client@clientName@message
			        
			        
			        String str= null;
			        String id;// either server or client
			        String name; //for clientName
			        String message; // for clientMessage
			        String instruction; // serverInstruction
			        
			        // server@list@name1$ip1$port1@
			        // client@name@message@
			        
			        
			        while ((str = rd.readLine()) != null) {
			        	int top =0;
			        	
			        	Scanner t1 = new Scanner(str).useDelimiter("@");
			        	id = t1.next();
			        	if(id.equals("server")){
			        		instruction = t1.next();
			        		if(instruction.equals("list")){
			        			
			        			// server send client user list
			        			String s1;
			        			s1 = t1.next();
			        			Scanner dollarParser = new Scanner(s1).useDelimiter("$");
			        			String n = dollarParser.next();//name
			        			String i = dollarParser.next();//ip
			        			String p = dollarParser.next();//port
			        			
			        			clientInfo tmpClient = new clientInfo();
			        			tmpClient.name = n;
			        			tmpClient.ip = i;
			        			tmpClient.chatPort = p;
			        			
			        			clientList[top++] = tmpClient;
			        		}
			        		if(instruction.equals("heartbeat")){
			        			//server request heartbeat
			        			
			        		}
			        		
			        		
			        	}
			        	else{
			        	
					        name = t1.next();
					        message = t1.next();//port
					    	 
				        }
			         }
			       
			        rd.close();
			    } catch (IOException e) {
			    	System.err.println("something wrong when receiving message from client");
			    }
			
			}//
		}

	}//receive
	
	
	
	
	public static void main(String[] args){
		
		  Socket socket=null;
		  
		 
		  
		if(args[0].equals(null) || args[0].equals("") ||args[1].equals(null) || args[1].equals("")){
			System.err.println("useage: iterClient [serverName] [userName]");
		}
				  int port = 22222;
		try {		    
		
				socket = new Socket(args[0],port);
				socket.setSoTimeout(5000);
			    
		    
		} catch (SocketTimeoutException e) {
			System.err.println("socket timeout, please check server port and address");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//receiving
		    /*try {
		        BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		        String str;
		        while ((str = rd.readLine()) != null) {
		            System.out.println("receiving message from server: "+ str);
		        }
		        rd.close();
		    } catch (IOException e) {
		    	System.err.println("something wrong when receiving message from client");
		    }*/
		
		//sending
			try{
	    		//send connection confirmation
		    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    		
		    		//get local ip address
		    		InetAddress localHost = InetAddress.getLocalHost();
		    		String ip = localHost.getHostAddress();
		    		
					System.out.println(localHost.getHostName());
					System.out.println(localHost.getHostAddress()); 
					
					
				    wr.write("n5"+"@"+ip+"@"+"23433235@");
				    wr.flush();
				    wr.close();
		    	}
	    		catch(IOException e){
	    			System.err.println("something wrong when sending welcome info to client");
	    		}
			    /*client
			     * 
			     * InetAddress localHost = InetAddress.getLocalHost();
					System.out.println(localHost.getHostName());
					System.out.println(localHost.getHostAddress()); 

			     * 
			     * */
			    
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("quit");
	}//main
}//iterClient
