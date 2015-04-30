package demo1;

import java.util.Scanner;

class Processor extends Thread {
	//use of volatile keyword helps to get make thread understand that it gonna change,so don't expect much.
	private volatile boolean running =true;
	public void run() {
		while(running) {
			System.out.println("Hello");
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void shutdown(){
		running=false;
	}
}
public class App {
	public static void main(String [] args){
		
		Processor proc1=new Processor();
		proc1.start();
		
		System.out.println("Press enter key to stop....");
		Scanner scanner= new Scanner (System.in);
		scanner.nextLine();
		
		proc1.shutdown();
		
		
	}

}
