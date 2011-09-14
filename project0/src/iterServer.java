import java.util.*;
import java.io.*;
import java.net.*;
public class iterServer {
	
	/**
	 * @author Jin
	 * unit that stores online client info
	 * giHub addr: git@github.com:jinelong/project0.git
	 */
	
	public int maxClient = 4;
	private ServerSocket checkServer;
	public Client clientList[] = new Client[maxClient]; 
	public int portC;
	private int counter = 0;
	public ServerSocket serverLink;
	
	public boolean isFull(){
		return (counter>maxClient)? true:false;
			
	}
	public void removeByName(String name){
		
		for(int i=0;i<counter;i++){
			if(clientList[i].name.equals(name)){
				clientList[i].name = clientList[counter-1].name;
				clientList[i].chatPort = clientList[counter-1].chatPort;
				clientList[i].ip = clientList[counter-1].ip;
				
				counter--;
			}
			
			
		}
		
		
	}
	public void passUserList(String addr, int port) throws UnknownHostException, IOException{
		Socket socket = new Socket(addr, port);
		
		try{
    		//send connection confirmation
	    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    		
	    		if(counter==1)
	    			wr.write("you are the only one on the server\n");
	    		else
		    		for(int i=0;i< clientList.length-1; i++){
		    			wr.write("name: "+  clientList[i].name + "\n");
		    			wr.write("ip: " + clientList[i].ip + "\n");
		    			wr.write("port: " + clientList[i].chatPort + "\n");
		    			wr.write("--------------------------\n");
		        	}
				    
			    wr.flush();
			    wr.close();
	    	}
    		catch(IOException e){
    			System.err.println("something wrong when sending userList");
    		}
		   
		    
			try {socket.close();}
			catch (IOException e) {e.printStackTrace();}

	}//passUserList
	
	public ServerSocket checkServer(){
		return checkServer;
		
	} 
	public ServerSocket createClient(){
		
		if(counter<maxClient){
			clientList[counter] = new Client();
			return clientList[counter++].createServers();
		}
		else 
			return null;
		
	}
	
	public iterServer(int _port){
		portC = _port;
		
	}
	public void removeCurrentClient(){
		--counter;
	}
	public int getClientNum (){ return counter;}
	
	
	class Client{
		
		public String name;
		public String ip;
		public String chatPort;
		
		public Client(){
			name = null;
			ip = null;
			chatPort = null;
		}
		
		public ServerSocket createServers() {
			
			//make connection
	    	try{
	    		serverLink = new ServerSocket(portC);
		    	
	    	}
	    	catch (IOException e){
	    		System.err.print("error happened when creating server on port"+ portC+ "\n");
				e.printStackTrace();
				 		
	    	}
	    
	    	System.out.println("chatServer created, port is " + portC);
	    	System.out.println("current online players: " + (getClientNum()-1));
	    	return serverLink;
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
			
			
			for(int i=0;i<counter;i++){
				if(n.equals(clientList[i].name)){
					return false;
				}
			}
			name = n;
			return true;
		}
		public void close() throws IOException{
			serverLink.close();
		}
	}//client

	public Client getCurrentClient(){
		
		return clientList[counter-1];
	}
	
		
    public static void main(String[] args) throws IOException {
    	
    	//args[0] =  "22222";
        int port = 22222;
    	//port =Integer.parseInt(args[0]);
    	iterServer s1 = new iterServer(port);
    
    	while(true){
    		Socket temp = null;
    		
    	//	if(s1.isFull()){
    	//		System.out.println("server is full");
    	//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
    	//		continue;
    	//	}//if
    		
    		/*
    		 * while(s1.isFull()){
    		 * 
    		 * 	ServerScoket fullServer = new ServerSocket(port);
    		 *  Socket fullSocket = fullServer.accpet(); // this socket should receive remove info
    		 *  
    		 *  
    		 * 
    		 *  Thread.sleep(2000);
    		 * }
    		 * 
    		 * 
    		 * 
    		 * */
    		
    		
    		
    		
    	//	else{
    	    	
		  	    ServerSocket tempServer= s1.createClient();
		  	    temp = tempServer.accept();
		  	  
    	//	}
   
		  	    
		  	    // enter@name@2222@
		    try {
		        BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));

		        String str= null;
		        String name;
		        String ip = temp.getInetAddress().toString();
		        String p;
		        String status;
		        while ((str = rd.readLine()) != null) {
		        	Scanner t1 = new Scanner(str).useDelimiter("@");
		        		
		        	status = t1.next();// either "enter" or "quit"
		        	name = t1.next();//name
		        	p = t1.next();//port
		        	 
		        	if(status.equals("enter") && !s1.isFull()){
				        
				       
			        
			        // server@list@name1$ip1$port1@
			        // server@waning@messageBody
			        // server@heartbeat			        
			        //from client: myName+"@ownPort+"@"
		        		
				        if(s1.getCurrentClient().setName(name)){
				        	 s1.getCurrentClient().setIP(ip);
				        	 s1.getCurrentClient().setPort(p);
				        	 
				        	 System.out.println("name is " + name + " ip is " + ip);
			        	}else{
			        		
			        		System.out.println("name already exist");
			        		Socket s = new Socket(ip, Integer.parseInt(p));
			        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				    		wr.write("warnings@name is already taken, please change a name and reconnect");
			        		wr.flush();
			        		wr.close();
			        		s1.removeCurrentClient();
			        	}
			        
		        }else if(status.equals("quit")){
		        		s1.removeByName(name);
		        }else{ 
		        	
			        	Socket s = new Socket(ip, Integer.parseInt(p));
		        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server full, come back later");
		        		wr.flush();
		        		wr.close();
		        		s1.removeCurrentClient();
			        	
			        	break; 
			        }
		  
		      }//while_readSocket
		       
		        rd.close();
		    } catch (IOException e) {
		    	System.err.println("something wrong when receiving message from client");
		    }
    		 temp.close();
    		 s1.getCurrentClient().close();
    	
    	}//while_ture
    	
    }//main
   
}//iterServer