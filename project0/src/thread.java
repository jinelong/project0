
public class thread extends Thread {
	int s;
	public thread(int i){
		s= i;
	}
	public void run(){
		try {
			count(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void count(int i) throws InterruptedException{

		while (i<100){
			System.out.println(i++);
			Thread.sleep(1000);
		}
	}
	
	public static void main(String[] a){
		
		
		Thread t = new thread(3);
		t.start();
		
		Thread d = new thread(10);
		d.start();
		
		System.out.println("done");
		
	}
}
