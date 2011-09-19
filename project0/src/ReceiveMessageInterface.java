import java.rmi.*;

public interface ReceiveMessageInterface extends Remote{
	
  void receiveMessage(String x) throws RemoteException;
  boolean addClient(String name, String ip, String port) throws RemoteException;
  boolean isFull()  throws RemoteException;
  String getClient (String name)  throws RemoteException;
  String getClientList()  throws RemoteException;
  public void removeByName(String name) throws RemoteException;
  public String removeMe(String name) throws RemoteException;
  public boolean heartbeat(String name) throws RemoteException;
  
}