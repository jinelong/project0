import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class receive {

	public static void main(String []a ) throws IOException
	{
		
		ServerSocket s = new ServerSocket(22222);
		Socket temp = s.accept();
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));
		String str = null;
		
		while((str=rd.readLine())!=null){
			System.out.println(str+"\n");
			
		}
		rd.close();
		temp.close();
		s.close();
		
	}
}
