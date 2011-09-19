

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.net.UnknownHostException;



import java.rmi.*;
import java.util.Timer;
import java.util.TimerTask;

public class rmiServer extends 
  java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface{
	
	
  String address;
  static Registry registry; 
  static int top = 0;
  final static  int maxNum = 2000;
  
  static Client clientList[] = new Client[maxNum];
  
  
  
  Timer timer;

		int i;
	    class RemindTask extends TimerTask {
	        public void run() {
        		
	        	for(int i =0; i<top;i++){
	        		
	        		if(!clientList[i].online){
	        			try {
							removeMe(clientList[i].name);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        		else{
	        			clientList[i].online = false;
	        		}
	        	}
	           }//run
	    }//RemindTask

	
	
  
  
  
  public boolean isFull(){
		if(top > maxNum || top == maxNum ) return true;
		else
			return false;
			
	}
	
  
  class Client{
		
		public String name;
		public String ip;
		public String chatPort;
		public boolean online = false;
		
		public Client(){
			name = null;
			ip = null;
			chatPort = null;
		}
		
		
		public boolean setPort(String n){
			chatPort = n;
			return true;
		}
		public boolean setIP(String n){
			ip = n;
			return true;
		}
		
		public boolean setName (String n){
			for(int i=0;i<top;i++){
				if(n.equals(clientList[i].name)){
					return false;
				}
			}
			name = n;
			return true;
		}
		
	}//client

  
public  String getClient (String name){
	
	for(int i=0;i<top;i++){
		if(clientList[i].name.equals(name)){
			return clientList[i].ip;
		}
		
	}
	return null;
	
}

public String removeMe(String name) throws RemoteException{
	
	Socket s;
	
	removeByName(name);
	
	String retval= "server@list@" + name + " signed off, list of online users: ";
	if(top == 1){
		retval =   "you are the only one online";
	}
	else {
		for(int i=0;i<top;i++){			
			retval = retval + clientList[i].name + " ";
		}
		for(int i = 0 ; i<top; i++){
			
			try {
				s = new Socket(clientList[i].ip, 22223);
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				wr.write(retval);
				wr.flush();
				wr.close();
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("current online user: " + top);
	}//else
	
	System.out.println("removeMe done");

	return retval;
}//removeMe

public void removeByName(String name) throws RemoteException{
	System.out.println("remove callled");
	
	if(top==1 && name.equals(clientList[0].name)){
		String n = clientList[0].name;
		
		clientList[0]=null;
		top --;
		System.out.println(n + " quit from server");
		return;
		
	}
	else if(name.equals(clientList[top-1].name)){
		clientList[top-1] = null;
		top --;
		System.out.println(name + " quit from server");
		return;
	}
	
	else{	
		for(int i=0;i<top;i++){
			if(clientList[i].name.equals(name)){
					String n =clientList[i].name;
					clientList[i].name = clientList[top-1].name;
					clientList[i].chatPort = clientList[top-1].chatPort;
					clientList[i].ip = clientList[top-1].ip;
				
				
				top--;
				System.out.println(n + " quit from server");

				return;
			}
			
		}//for
	}
	
}

public String getClientList(){
	
	String retval= "Client List: ";
	if(top == 1){
		return "you are the only one online";
		
	}
	for(int i=0;i<top;i++){
		
		retval = retval + clientList[i].name + " ";
	}
	return retval;
}

  
  public boolean addClient(String name, String ip, String port) throws RemoteException{
	  Socket s;
	  if(!isFull()){
		  
		  clientList[top] = new Client();
		  clientList[top].setName(name);
		  clientList[top].setIP(ip);
		  clientList[top].setPort(port);
		  clientList[top].online = true;
		  
		  
		  System.out.println(clientList[top].name + " join the server");
		  top++;
		  
			
		  return true;
		 
	  }
	  return false;
  }

  public void receiveMessage(String x) throws RemoteException{
	  System.out.println(x);
  }
  
  public boolean heartbeat(String name) throws RemoteException{
	  
	  for(int i = 0; i<top; i++){
		  if(clientList[i].name.equals(name)){
			  clientList[i].online = true;
			  return true;
		  }
	  }
	  return false;
	  
  }
  public rmiServer() throws RemoteException{
	  try{  
		  address = (InetAddress.getLocalHost()).toString();
	  }
	  catch(Exception e){
	 	 System.out.println("can't get inet address.");
	  }
	  
	  int port=22222; 
	  
	  System.out.println("this address=" + address +  ",port=" + port);
	  
		  try{
			  registry = LocateRegistry.createRegistry(port);
			  
			  registry.rebind("rmiServer", this);
		  }
		  catch(RemoteException e){
			  System.out.println("remote exception"+ e);
		  }
		  
		  timer = new Timer();
		  timer.scheduleAtFixedRate(new RemindTask(), 0, 1000*20);
	  }
  
  
  //java RmiServer  this address=roseindi/192.168.10.104,port=3232
static public void main(String args[]){
	
		int port = Integer.parseInt(args[0]);
	  try{
		  rmiServer server = new rmiServer();
		  
	  }
	  catch (Exception e){
		  e.printStackTrace();
		  System.exit(1);
		}
  }
}