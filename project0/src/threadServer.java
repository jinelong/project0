import java.util.*;
import java.io.*;
import java.net.*;

public class threadServer extends Thread {
	
	/**
	 * @author Jin
	 * unit that stores online client info
	 * giHub addr: git@github.com:jinelong/project0.git
	 */
	
	public static int maxClient = 3000;
	public static Client clientList[] = new Client[maxClient]; 
	private static int top = 0;
	
	public int portC;
	private Socket temp;
	//public ServerSocket serverLink;
	public ServerSocket dummyServer;
	static boolean ifTimer = false;
	static Timer timer;
    
    

		
	   static class RemindTask extends TimerTask {
	        public void run() {
	        	int i = 0;
        		System.out.println("in timer");	
        		Socket s = null;
	        	for( i=0;i<top;i++){
	        		System.out.println("sending heartbeat requst to "+ clientList[i].name);
	        		try {
						 s = new Socket(clientList[i].ip, Integer.parseInt(clientList[i].chatPort));
						s.setSoTimeout(2000);
			    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@heartbeat@");
			    		wr.flush();
			    		wr.close();

					} catch (SocketTimeoutException e){
						System.out.println("client " + i + "did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
						
					}catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						
				//		e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						System.out.println("client " + i + " did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
					//	e.printStackTrace();
					} catch (IOException e) {
						System.out.println("client " + i + " did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
	        		try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        	}//for
	           }//run
	    }//RemindTask

	
	public void run(){
		
  	    // enter@name@2222@
	    try {
	        BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));

	        String str= null;
	        String name;
	        String ip = temp.getInetAddress().getHostAddress().toString();
	                 
	        
	        
	        String p;
	        String status;
	        while ((str = rd.readLine()) != null) {
	        //	System.out.println("read from socket: " + str);
	        	
	        	
	        	Scanner t1 = new Scanner(str).useDelimiter("@");
	        		
	        	status = t1.next();// either "enter" or "quit"
	        	name = t1.next();//name
	        	p = t1.next();//port
	        	
	        	if(status.equals("enter") && !isFull()){
			       			        
		        // server@list@name1$ip1$port1@
		        // server@waning@messageBody
		        // server@heartbeat			        
		        //from client: myName+"@ownPort+"@"
			       
		        	
			        createClient();
			        if(getCurrentClient().setName(name)){
			        	 getCurrentClient().setIP(ip);
			        	 getCurrentClient().setPort(p);
			        	 
			   
			        	 passUserList(ip, Integer.parseInt(p));
			        	 for(int i =0;i<top;i++){
			        		 passUserList(clientList[i].ip, Integer.parseInt(clientList[i].chatPort));
			        	 }
			        	 
			        		System.out.println("hello sent");
		        	}else{
		        		
		        		System.out.println("name already exist");
		        		Socket s = new Socket(ip, Integer.parseInt(p));
		        		
		        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@warning@Server: name is already taken, please change a name and reconnect@");
		        		wr.flush();
		        		wr.close();
		        		removeCurrentClient();
		        	}
		        
	         }else if(status.equals("quit")){
	        		
	        		removeByName(name);
	        		
	        		//for(int i =0; i<s1.top;i++){
	        			
	        		//	s1.passUserList(s1.clientList[i].ip, Integer.parseInt(s1.clientList[i].chatPort));
	        		//}
	        		
	        
	        }else if(status.equals("heartbeat")){
	        	
	        		System.out.println(name + " is alive");
	        	
	        }
	         
	         else{ 
	        	
	        		System.out.println("in full case");

		        	Socket s = new Socket(ip, Integer.parseInt(p));
		        	s.setSoTimeout(2000);
	        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		    		wr.write("server@warning@server full, come back later");
	        		wr.flush();
	        		wr.close();
	        		removeCurrentClient();
		        	dummyServer.close();
	        		System.out.println("system full in else");
	       		
		        	break; 
		        }
	  
	      }//while_readSocket
	       
	        rd.close();
	   
	    } catch(SocketTimeoutException e){
	    	System.err.println("dummyServer Timeout");
	    	
	    }catch (IOException e) {
	    	e.printStackTrace();
	    	System.err.println("something wrong when receiving message from client");
	    	
	    }
		 try {
			temp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	
	}//run


	    
	    
	
	public boolean isFull(){
		if(top > maxClient || top == maxClient ) return true;
		else
			return false;
			
	}
	
	
	public synchronized static void removeByName(String name){
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
	
	public synchronized void  passUserList(String addr, int port) throws UnknownHostException, IOException{
		Socket socket = new Socket(addr, port);
		
		try{
    		//send connection confirmation
	    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    		
	    		if(top==1)
	    			wr.write("server@list@you are the only one on the server\n");
	    		else{
	    			wr.write("server@list@");
	    		
		    		for(int i=0;i< top; i++){
		    			wr.write(clientList[i].name+"$"+clientList[i].ip+"$"+clientList[i].chatPort+"#");
		    			
		        	}
		    		
	    		}
				    
			    
			    wr.close();
	    	}
    		catch(IOException e){
    			System.err.println("something wrong when sending userList");
    		}
		   
		    
			try {socket.close();}
			catch (IOException e) {e.printStackTrace();}

	}//passUserList
	
	
	
	public synchronized boolean createClient() throws IOException{
		
		if(!isFull()){
			clientList[top] = new Client();
			top ++;
		}
		else {
			
			return false;
		}
			
		return true;
	}
	
	public threadServer(Socket t){
		
		 temp = t;
		 if(!ifTimer){
			 ifTimer=true;
			 createTimer();
		 }
	     //timer.schedule(new RemindTask(), 0, 3*1000);

		
	}
	 static void createTimer(){
		 timer = new Timer();
	     timer.scheduleAtFixedRate(new RemindTask(), 0, 1000*20);
		
	}
	
	public synchronized void removeCurrentClient(){
		--top;
	}
	
	public synchronized int getClientNum (){ return top;}
	

	
	class Client{
		
		public String name;
		public String ip;
		public String chatPort;
		
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
	//	public void close() throws IOException{
	//		temp.close();
	//	}
	}//client

	public synchronized Client getCurrentClient(){
			
		return clientList[top-1];
	}
	
		
    public static void main(String[] args) throws IOException {
    	
    	//args[0] =  "22222";
        int port = 22222;
    	//port =Integer.parseInt(args[0]);
		ServerSocket tempServer= new ServerSocket(port);

    	while(true){
    		System.out.println("waitting for incomeing connections");
    		Socket temp = tempServer.accept();
        	new Thread( new threadServer(temp)).start();
        	
    	}//while
	
    }//main
   
}//iterServ