import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.net.UnknownHostException;


public class rmiClient{
	
	ReceiveMessageInterface rmiServer;
  	Registry registry;
  	static String myName;
  	static String myIP;
	
	Thread rServer = null;
	ServerSocket receive;
	int oPort = 22223;
	public boolean doNotQuit = true;
	public String list = null;
	
	int maxNumber = 3000;
	public static int top = 0;
	

	  Timer timer, timer2;

			int i;
		    class RemindTask extends TimerTask {
		        public void run() {
	        		try {
						if(!rmiServer.heartbeat(myName))
							System.err.println("fail to register heartbeat, will try again in 3 seconds");
							
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		           }//run
		    }//RemindTask

		    class timerList extends TimerTask {
		        public void run() {
	        		
		        	try {
		        		String temp = (rmiServer.getClientList());
		        		if(!temp.equals(list)){
		        			list = temp;
		        			System.out.println("list update: "+ list);
		        		}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		           }//run
		    }//RemindTask

		
		
	
	public void chatToClient(String ip, String msg) throws NumberFormatException, UnknownHostException, IOException{
		
		if(ip==null){
			System.out.println("seems that the user is nolonger online");
			return;
			
		}
		Socket s = new Socket(ip, 22223);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write("chat@"+myName+ "@"+msg);
		wr.flush();
		wr.close();
		s.close();
		
		System.out.println("msg sent");
		
	}

	class receiveServer extends Thread {
		
		public receiveServer() throws IOException {
			
			receive = new ServerSocket(oPort);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Socket temp = null;
			System.out.println("Listening server set up, waiting for server response");
			while (doNotQuit) {
				try {
					temp = receive.accept();
					
					//System.out.println("my ip is " + myIP);
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(temp.getInputStream()));
					// sample message from server:
					// server@heartbeat
					// client@clientName@message

					String str = null;
					String id;// either server or client
					String name; // for clientName
					String message; // for clientMessage
					String instruction; // serverInstruction
					

					// server@list@name1$ip1$port1@
					// server@waning@messageBody
					// server@heartbeat

					while ((str = rd.readLine()) != null) {
						
						//System.out.println("receive: " + str);
						Scanner t1 = new Scanner(str).useDelimiter("@");
						id = t1.next();
						if (id.equals("server")) {
							
							instruction = t1.next();
							
							if(instruction.equals("heartbeat")){
								
								System.out.println("server request heratbeat");
							}
							else if(id.equals("list")){
								
								System.out.println(t1.next());

							}
							
						}
						
						else if (id.equals("chat")){
							String tn = t1.next();
							String msg = t1.next();
							System.out.println(tn + ": " + msg);
							
							}
						}
					

					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("something wrong when receiving message from client");
				}

			}//
		}

	}// receive
	
	public rmiClient(String n, ReceiveMessageInterface rS, Registry r ) throws IOException{
		 rmiServer = rS;
	  	 registry = r;
	  	 myName = n;
	  	 
	  	InetAddress thisIp =InetAddress.getLocalHost();
	  	myIP  = thisIp.getHostAddress();
	  	
		rServer = new receiveServer();
		rServer.start();
		
		  timer = new Timer();		  
		  timer2 = new Timer();
		  timer2.scheduleAtFixedRate(new timerList(), 2000, 2*1000);
		  timer.scheduleAtFixedRate(new RemindTask(), 0, 1000*3);
		
	}
	
	
	//java rmiClient lore.cs.purdue.edu 22222 thisisJin
  static public void main(String args[]) throws NumberFormatException, IOException{
  
	  	ReceiveMessageInterface rmiServer = null;
	  	Registry registry;
	  	rmiClient client = null;
	  	
	    InetAddress addr = InetAddress.getLocalHost();
	    String ip = addr.toString();
	    
	    String str;
	    
	    //lore.cs.purdue.edu 22222
	  	String serverAddress=args[0];
	  	String serverPort=args[1];
		Scanner s = new Scanner(System.in);
		
		
		System.out.println("input your name: ");
		String name = s.nextLine();
	  	System.out.println ("sending " + name + " to " +serverAddress + ":" + serverPort);
	  	
	  	
	  	try{
			  registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
			  rmiServer=(ReceiveMessageInterface)(registry.lookup("rmiServer"));
			  
			  client = new rmiClient (name, rmiServer, registry);
			  
			  // call the remote method
			  //rmiServer.receiveMessage(name);
			  
			  if(!rmiServer.isFull()){
				  if(!rmiServer.addClient(name, client.myIP, String.valueOf(22223))){
					  System.out.println("name taken, try with another name, connect again");
					  System.exit(1);
				  }
				  
			  }
			  else{
				  
				  System.out.println("server full, try again later");
				  System.exit(1);
			  }
		 }
		 catch(RemoteException e){
			 e.printStackTrace();
		 }
		 catch(NotBoundException e){
		    System.err.println(e);
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	
	  
	  	while (true)
		 {
			System.out.println("enter your commend, enter 'quit' to disconnect. to chat, type \"chat@[clienName]@[your message]\"");
			client.list = rmiServer.getClientList();
			System.out.println(client.list);
			//s.next()  quit or chat@1
			str = s.nextLine();
			if(str.equals("quit")){
				rmiServer.removeMe(client.myName);
				client.rServer.stop();
				client.doNotQuit = false;
				client.timer.cancel();
				System.exit(1);
			}
			else if(str.contains("chat")){
				Scanner c =new Scanner (str).useDelimiter("@");
				c.next();
				String clientName = c.next();
				String clientIP = rmiServer.getClient(clientName);
				System.out.println("client ip is : " + clientIP);
				
				client.chatToClient(clientIP, c.next());
				
			}
			else{
				System.out.println("unrecognized command");
			}

		} // @quit@name
	  	
	  	
  }//main
} //class