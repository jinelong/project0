import java.net.*;
import java.util.Scanner;
import java.io.*;

// java iterClient [serverPort] [ownPort]

public class iterClient {

	int maxNumber = 3000;
	public String myName;
	public static int top = 0;
	clientInfo clientList[] = new clientInfo[maxNumber];
	public int serverPort;
	public int ownPort;
	public String serverAddr;
	public int counter = 0;
	public ServerSocket receive = null;
	public Thread rServer = null;
	public static boolean doNotQuit = true;
	public void chatToClient(int num, String msg) throws NumberFormatException, UnknownHostException, IOException{
		
		Socket s = new Socket(clientList[num].ip, Integer.parseInt(clientList[num].chatPort));
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write("chat@"+myName+ "@"+msg);
		wr.flush();
		wr.close();
		s.close();
		
		System.out.println("msg sent");
		
	}

	public void printClients(){
		System.out.println("Client list: top is  " + top);
		
		for(int i =0;i<top;i++){
			System.out.println("-------------\nClient: " + i );
			System.out.println("name: " + clientList[i].name);
			System.out.println("ip: " + clientList[i].ip);
			System.out.println("chatPort: " + clientList[i].chatPort);
			
		}
	}

	public iterClient(String addr, int sPort, String myName, int oPort)	throws IOException, InterruptedException {
		serverPort = sPort;
		ownPort = oPort;
		serverAddr = addr;
		rServer = new receiveServer(ownPort);
		rServer.start();
		
		// setup listening server for server info and client message
		// enter@name@port@
		String message = "enter@" + myName + "@" + ownPort + "@";
		
		send(serverAddr, serverPort, message);
		
		
		
	}//constructor
	class receiveServer extends Thread {
		int port;

		public receiveServer(int i) throws IOException {
			port = i;
			receive = new ServerSocket(port);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Socket temp = null;
			System.out.println("Listening server set up, waiting for server response");
			while (doNotQuit) {

				try {
					temp = receive.accept();
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
							if (instruction.equals("list")) {
								
								
								if(str.contains("only")){
									
									System.out.println("server sent list info");
									System.out.println(t1.next());
									continue;
									
								}
								top = 0;
								System.out.println(str.substring(12));
								String listInfo = str.substring(12);
								
								System.out.println("receive listInfo: " + listInfo);
								
								Scanner pond = new Scanner(listInfo).useDelimiter("#");
								String entry = pond.next();
								
								
								do{
									Scanner dollar = new Scanner(entry).useDelimiter("\\$");
									clientList[top] = new clientInfo();
									clientList[top].name = dollar.next();
									clientList[top].ip = dollar.next();
									clientList[top].chatPort = dollar.next();
							
									
									System.out.println(clientList[top].name + " added to the list" );
									top ++;
									try{
										entry = pond.next();
									}
									catch(Exception e){
										break;
									}
								}while(entry !=null);
							
								System.out.println("server sent list info: "+ top + " clients added");
								
								printClients();
							}//instruction == List
							else if (instruction.equals("heartbeat")) {
								// server request heartbeat
								String msg = "heartbeat" + "@" + myName + "@" + serverPort + "@";
								send(serverAddr, serverPort, msg);
								
							}
							else if (instruction.equals("warning")) {
								System.err.println(t1.next());
								//System.err.println("name already exist on the server");
								doNotQuit = false;
								rServer.stop();
								receive.close();
								
								System.exit(0);
								return;

							}
							else if(instruction.equals("info")){
								System.out.println(t1.next());
								
							}

						} else { 	
							
							name = t1.next();
							message = t1.next();// port
							
							System.out.println("from " + name + ": " + message);

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
	
	
	
	
	public void send(String serverAddr, int serverPort, String message) {

		Socket socket = null;

		try {

			socket = new Socket(serverAddr, serverPort);
//			socket.setSoTimeout(5000);

		} catch (SocketTimeoutException e) {
			System.err.println("socket timeout, please check server port and address");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// send connection confirmation
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			// get local ip address
			//InetAddress localHost = InetAddress.getLocalHost();
			//String ip = localHost.getHostName();

		//	System.out.println(localHost.getHostName());
		//	System.out.println(localHost.getHostAddress());

			wr.write(message);
			// wr.write("n5"+"@"+ip+"@"+"23433235@");
			wr.flush();
			wr.close();
		} catch (IOException e) {
		//	e.printStackTrace();	
			System.err.println("something wrong when sending message, try again....");
		}

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class clientInfo {

		public String name;
		public String ip;
		public String chatPort;
	}

	

	public static void main(String[] args) throws InterruptedException, IOException {

		Socket socket = null;
		iterClient user = null;
		int serverPort;
		int ownPort;
		String name;
		String serverAddr;

		Scanner s = new Scanner(System.in);

		// javac iterClient [serverIP/serverName] [serverPort] [myName]
		// [ownPort]
		// if(args[0].equals(null) || args[0].equals("") ||args[1].equals(null)
		// || args[1].equals("")){
		// System.err.println("useage: iterClient [serverName] [port]");
		// }
		// args[0] = serverAddr;
		// args[1] = serverPort
		// args[2] = ownPort

		serverAddr = args[0];
		serverPort = Integer.parseInt(args[1]);
		ownPort = Integer.parseInt(args[2]);
		
		System.out.print("please input your name: ");
		name = s.nextLine();
		
		try {

			user = new iterClient(serverAddr, serverPort, name, ownPort);
			user.myName = name;
		

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String str;
		str = s.nextLine();
		while (user.doNotQuit)
		 {
			System.out.println("enter your commend, enter 'quit' to disconnect. to chat, type \"chat@[clientNum]@[your message]\"");
			//s.next()  quit or chat@1
			
			if(str.equals("quit")){
				str = str + "@" + user.myName + "@" + serverPort + "@";
				System.out.println(str);
				user.send(serverAddr, serverPort, str);
				break;
			}
			else if(str.contains("chat")){
				Scanner c =new Scanner (str).useDelimiter("@");
				c.next();
				int num = Integer.parseInt(c.next());
				user.chatToClient(num, c.next());
				
			}
			else{
				System.out.println("unrecognized command");
			}
			str=s.nextLine();

		} // @quit@name
		
		user.rServer.stop();
		System.exit(1);
		

	}// main
}// iterClient
