import java.util.*;
import java.io.*;
import java.net.*;
public class iterServer {
	
	/**
	 * @author Jin
	 * unit that stores online client info
	 *
	 */
	
	public int maxClient = 4;
	private ServerSocket checkServer;
	public Client clientList[] = new Client[maxClient]; 
	public int portC;
	private int counter = 0;
	public ServerSocket serverLink;
	
	public boolean isFull(){
		return (counter>maxClient || counter==maxClient)? true:false;
		
		
		
			
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
	
	class Client{
		
		public String name;
		public String ip;
		public String chatPort;
		
		public Client(){
			name = null;
			ip = null;
			serverLink = null;
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
    	
    	if(args[0].equals("") || args[0].equals(null)){
    		System.out.println("ussage: java iterServer [port]");
    	}
    	
    	int port;
    	port =Integer.parseInt(args[0]);
    	iterServer s1 = new iterServer(port);
    
    	while(!s1.isFull()){
    		Socket temp = null;
    		
    		if(s1.isFull()){
    			System.out.println("server is full");
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			continue;
    			}
    		else{
    	    	
		  	    ServerSocket tempServer= s1.createClient();
		  	    temp = tempServer.accept();
		  	  
    		}
   
		    try {
		        BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));

		        String str= null;
		        String name;
		        String ip = temp.getInetAddress().toString();
		        String p;
		        while ((str = rd.readLine()) != null) {
		        	Scanner t1 = new Scanner(str).useDelimiter("@");

			        name = t1.next();
			        ip = t1.next();
			        p = t1.next();//port
			        
			        
			        
			        if(s1.getCurrentClient().setName(name)){
			        	 s1.getCurrentClient().setIP(ip);
			        	 s1.getCurrentClient().setPort(p);
			        	 
			        	 System.out.println("name is " + name + " ip is " + ip);
		        	}else{
		        		System.out.println("name already exist");
		        		Socket s = new Socket(ip, Integer.parseInt(p));
		        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("name is already taken, please change a name and reconnect");
		        		wr.flush();
		        		wr.close();
		        		s1.removeCurrentClient();
		        	}
			        
			        
		  
		         }
		       
		        rd.close();
		    } catch (IOException e) {
		    	System.err.println("something wrong when receiving message from client");
		    }
    		 temp.close();
    		 s1.getCurrentClient().close();
    	}//while
    	
    }
   
}