import java.util.Scanner;


public class parse {
	
	public static void main(String []a ){
		
		String input = " jin$128.10.25.203$22232#";
		String p = null;
		Scanner pond = new Scanner(input).useDelimiter("#");
		
		p = pond.next();
		
		do{
			
			System.out.println("p = " + p);
			Scanner dollar = new Scanner(p).useDelimiter("\\$");
			String name = dollar.next();
			String ip = dollar.next();
			String port = dollar.next();
			
			System.out.println(name + " " + ip + " " + port +"\n");
			try{
				p = pond.next();
			}
			catch(Exception e){
				break;
			}
		}while(p !=null);
		
		
		
	}

}


