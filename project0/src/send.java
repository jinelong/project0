import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class send {

	public static void main(String[] a) throws UnknownHostException, IOException
	{
		
		
		Socket s = new Socket("lore.cs.purdue.edu", 22222);
		
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				s.getOutputStream()));
		
		wr.write("server@warning@damn");
		wr.flush();
		wr.write("server@warning@damn");
		wr.flush();
		wr.write("server@warning@damn");
		wr.write("server@warning@damn");
		wr.flush();
		wr.write("server@warning@damn");
		wr.flush();
		wr.close();

		
		
	}
}
