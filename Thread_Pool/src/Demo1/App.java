package Demo1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Processor implements Runnable {
	private int id;
public Processor (int id){ //to set id to different processes
	this.id=id;
}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting a thread: "+id);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Completed the thread: "+id);
	}
	
	
}
public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorService executor=Executors.newFixedThreadPool(2);
		
		for (int i=0;i<5;i++){
		executor.submit(new Processor(i));	
		}
		executor.shutdown();
		System.out.println("All task has been assigned to the workers");
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
