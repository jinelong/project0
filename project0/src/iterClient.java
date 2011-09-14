import java.net.*;
import java.util.Scanner;
import java.io.*;

// java iterClient [serverPort] [ownPort]

public class iterClient {

	
	int maxNumber = 4;
	String myName;
	
	clientInfo  clientList [] = new clientInfo[maxNumber];
	int serverPort;
	int ownPort;
	String serverAddr;
	public int counter = 0;
	
	
	public iterClient(String addr, int sPort, String myName, int oPort) throws IOException{
		serverPort = sPort;
		ownPort = oPort;
		serverAddr = addr;
		
		//setup listening server for server info and client message
		receiveServer local = new receiveServer(ownPort);
	//	local.run();
		//enter@name@port@
		String message = "enter@"+myName+"@"+ ownPort+"@";

		send(serverAddr, serverPort, message );
		
		
	}
	
	public void send(String serverAddr, int serverPort, String message){
		
		Socket socket = null;
		
		try {		    
			
			socket = new Socket(serverAddr,serverPort);
			socket.setSoTimeout(5000);
		    
	    
	} catch (SocketTimeoutException e) {
		System.err.println("socket timeout, please check server port and address");
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	
		try{
    		//send connection confirmation
	    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    		
	    		//get local ip address
	    		InetAddress localHost = InetAddress.getLocalHost();
	    		String ip = localHost.getHostAddress();
	    		
				System.out.println(localHost.getHostName());
				System.out.println(localHost.getHostAddress()); 
				
				wr.write(message);
				// wr.write("n5"+"@"+ip+"@"+"23433235@");
			    wr.flush();
			    wr.close();
	    	}
    		catch(IOException e){
    			System.err.println("something wrong when sending welcome info to client");
    		}
		  
		    
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			System.out.println("Listening server set up, waiting for server response");
			while(true){
				
			  try { temp = server.accept();} catch (IOException e) { e.printStackTrace(); }
			
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
			        // server@waning@messageBody
			        // server@heartbeat
			        
			        
			        
			        
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
			        		;	
			        		}
			        		if(instruction.equals("warning"))
			        		{
			        			System.err.println(t1.next());
			        			System.exit(1);
			        			
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
	
	
	
	
	public static void main(String[] args) throws InterruptedException{
		
		  Socket socket=null;
		  int serverPort;
		  int ownPort;
		  String name;
		  String serverAddr;
		  
		  Scanner s = new Scanner(System.in);
		  
		  
		 
		  // javac iterClient [serverIP/serverName] [serverPort] [myName] [ownPort] 
		//if(args[0].equals(null) || args[0].equals("") ||args[1].equals(null) || args[1].equals("")){
	//		System.err.println("useage: iterClient [serverName] [port]");
	//	}
		// args[0] = serverAddr;
		// args[1] = serverPort
		// args[2] = ownPort
		
		
		serverAddr = args[0];
		serverPort = Integer.parseInt(args[1]);
		ownPort = Integer.parseInt(args[2]);
		
		System.out.print("please input your name: ");
		name = s.nextLine();
		
		try {
		
			iterClient user = new iterClient(serverAddr, serverPort, name, ownPort);
			user.myName = name;
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	//	while(true){
	//		System.out.println("test");
	//		Thread.sleep(2000);
	//	}
		
				  
	
		
	}//main
}//iterClient
