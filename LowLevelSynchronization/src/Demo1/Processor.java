package Demo1;

import java.util.LinkedList;
import java.util.Random;

public class Processor {
	LinkedList<Integer> list =new LinkedList<Integer>();
	private final int LIMIT = 10;
	private Object lock=new Object();
	
	public void produce() throws InterruptedException{
		int value=0;
		while (true){
			synchronized(lock){
				
				while(list.size()==LIMIT){ //to check and verify that wait was not waken up unknowingly
					lock.wait();
				}
			list.add(value++);
			lock.notify();
		}
		}
		
	}
	public void consume() throws InterruptedException{
		Random random =new Random();
		
		while (true){
			synchronized(lock){
				
				while(list.size()==0){
					lock.wait();
				}
			System.out.print("List size is: "+list.size());
			int value=list.removeFirst();
			System.out.println(";Value is: "+value);
			lock.notify();
			}
			Thread.sleep(random.nextInt(1000));
			
		}
	}
}
