import java.util.Timer;
import java.util.TimerTask;


public class Reminder {
    Timer timer;

    public Reminder(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds*1000);
        
	}
    public void print(){
    	
        System.out.format("Time's up!%n");

    	
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.format("Time's up!%n");
            //timer.schedule(new RemindTask(), 2*1000);
        }
    }

    public static void main(String args[]) {
        new Reminder(2);
        System.out.format("Task scheduled.%n");
    }
}