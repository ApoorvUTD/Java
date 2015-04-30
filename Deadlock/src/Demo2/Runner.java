package Demo2;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Runner {
	
	private Lock lock1 = new ReentrantLock();
	private Lock lock2 = new ReentrantLock();


	private Account acc1=new Account();
	private Account acc2=new Account();   
	
	private void acquireLock(Lock firstLock,Lock secondLock) throws InterruptedException{
		
		while(true){
		boolean getLock1=false;
		boolean getLock2=false;
		try{
			getLock1=firstLock.tryLock();
			getLock2=secondLock.tryLock();
		}
		finally{
			if (getLock1 && getLock2){
				return;
			}
			if(getLock1){
				firstLock.unlock();
			}
			if(getLock2){
				secondLock.unlock();
			}
			
		}
		Thread.sleep(1);
		}
	}
	
	public void firstThread() throws InterruptedException{
		
		Random random =new Random();
		for (int i=0;i<10000;i++){
			acquireLock(lock1,lock2 );
			try{
			Account.transfer(acc1,acc2,random.nextInt(100));}
			finally{
				lock1.unlock();
				lock2.unlock();
				
			}
		}
		
	}
	
public void secondThread() throws InterruptedException{
	Random random =new Random();
	for (int i=0;i<10000;i++){
		acquireLock(lock2,lock1 );
		try{
		Account.transfer(acc2,acc1,random.nextInt(100));}
		finally{
			lock1.unlock();
			lock2.unlock();
		}
	}	
	}

public void finished(){
	System.out.println("Account1 balance: "+acc1.getBalance());
	System.out.println("Account2 balance: "+acc2.getBalance());
	System.out.println("Total balance: "+(acc1.getBalance()+acc2.getBalance()));


}

}
