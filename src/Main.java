import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main implements Runnable {
	//Instance variables
	int iGraph[][],iPID[];//edgeGraph and uid of nodes
	int processFinished;
	public Node[] nodeI;//oproc
	boolean replyAll, waitState;//boolean variable to check whether process replied or in wait condition
	int noOfNodes=0; //number of nodes for simplicity or number of processes in distributed scenario
	int noOfProcess=0;
			static String[] sProcessRoundStatus;
			;//for simplicity
	int uidOfNodes[] = null;
	double edgeGraph[][]=null;
	Thread check; 
	ThreadGroup threads;
	Main obj;
	String[] Rounds={"RoundOver","RoundNotOver","ProcessOver"};//put it as enum 
	String state;
	//static Node[] nodeArray;
	//Fragment[] fragments; 
	//MSTGraph mstGraph;
	
	//*********MAIN**********METHOD//
	public static void main(String[] args) throws IOException {
		Main ghs= new Main();		
		ghs.readNoadDetails();
		}
	
	//Constructor of Main class assigning default values
	public Main(){
		state=Rounds[0];//sStatus==sStatusValue initially rounds are set to be over that is starting point
		processFinished=0;//all the processes are done
		replyAll=false;//received reply from all set to false
		waitState=false;//none of my process is in wait state so chill
	}
	
	public void setMainController(Main obj){
		this.obj=obj; //assign the value to main object created 
	}
	
	//taking input value and extracting important stuff like number of processes neighbors etc.
	public void readNoadDetails() throws IOException
	{	
		System.out.println("****** Input Provided ****** " );
		Scanner sc = new Scanner(new File("a.txt"));//reading file from input
		noOfNodes = sc.nextInt();//total number of processes/nodes assigned to the variable
		noOfProcess=noOfNodes;
		uidOfNodes= new int[noOfNodes];//is array allocation for 8 different process in our input case
		sProcessRoundStatus = new String[noOfNodes];
		for(int i=0;i<noOfNodes;i++)
		{
			uidOfNodes[i]= sc.nextInt();
			System.out.println("UID of Processes ----> "+i+" is:" + uidOfNodes[i]);
		}
			System.out.println("****** Weights Provided ****** " );
			edgeGraph = new double[noOfNodes][noOfNodes];
		for (int i = 0; i < noOfNodes; i++) {
			for (int j = 0; j < noOfNodes; j++) {
				edgeGraph[i][j] = sc.nextDouble();
				System.out.println("Edge weights between Processes----> " + i+" and "+j+" is "+edgeGraph[i][j]);
			}
		}
		sc.close();
		nodeInitialization();//after input is set lets assign the values to all the nodes or in distributed sense processes :D
	}
	
	//initializing nodes and its connection to other nodes in short idea about neighbors :P
	public void nodeInitialization(){
		Node[] n;
		nodeI = new Node [noOfNodes];
		int process=0, NumberOfNbrs=0, iRow, prs=0;
		int neighbours[][];
		for(process=0;process<noOfNodes;process++){
			NumberOfNbrs=0;
			//counting the number of neighbors as negative edge implies no connection thus dropping anything below 0
			for(prs=0;prs<noOfNodes;prs++)
			{
				if(edgeGraph[process][prs]>=0){
					NumberOfNbrs++;
				}
			}
			System.out.println("Number of eligible neighbour of node: "+uidOfNodes[process]+" is " + NumberOfNbrs);
			neighbours= new int[NumberOfNbrs][2];//creates an array with row = no of neighbor and two columns
			iRow=0;
			
			//array assigned in previous step will be used to fill the details of neighbor id
			for(prs=0;prs<noOfNodes;prs++)
			{
				if(edgeGraph[process][prs]>0){
					neighbours[iRow][0]=prs;
					neighbours[iRow][1]=uidOfNodes[prs];
					iRow++;
	    	//System.out.println(neighbours[iRow][prs]);//array out of bound error need to fix it *****************
				}
			//System.out.println(neighbours[iRow][prs]);
			}
			
			//creating the (nodes or process) and passing it to the constructor defined in Node class
			nodeI[process]=new Node(uidOfNodes[process],process,neighbours);
		}
		
		
		//  send each nodes information  about how to communicate to their neighbors
		//  send an array of neighbor objects to the nodes
		//  first count, how many number of neighbors each node has. Then add each neighbor object to the array and send it to the Nodes.
		NumberOfNbrs=0;
		for(process=0;process<noOfNodes;process++){
			NumberOfNbrs=0;
			for(prs=0;prs<noOfNodes;prs++)
			{
				if(edgeGraph[process][prs]>0){
					NumberOfNbrs++;
				}
			}
			n=new Node[NumberOfNbrs];
			iRow=0;
			for(prs=0;prs<noOfNodes;prs++){
				if(edgeGraph[process][prs]>0){
					n[iRow]=nodeI[prs];
					iRow++;
				}	
			}
			nodeI[process].setNeighborObjects(n);//sending array of neighbor to the nodes
			//sending  the reference of the main thread to each process in order to control later
			nodeI[process].setMain(obj);
		}
	}
	
	//overriding run method of the interface runnable
	@SuppressWarnings("static-access")
	public void run(){
		try {
			check.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean controlSwitch = true;
		int pNo;
		while(controlSwitch){
				switch(state){
				case "RoundOver": //if round over then reset the entire process and continue
					resetProcessRoundStatus();
					for(pNo=0;pNo<noOfNodes;pNo++){
						nodeI[pNo].processThread("T"+uidOfNodes[pNo]);
					}
					state=Rounds[1];
					break;
				case "RoundNotOver"://if round not over then prepare for next round
					readyForNextRound();
					for(pNo=0;pNo<noOfNodes;pNo++){
						nodeI[pNo].reset("parent");
						nodeI[pNo].reset("convergeCast");
					}
					break;
				case "ProcessOver"://when the process is over
				
					for(pNo=0;pNo<=noOfNodes;pNo++){
						nodeI[pNo].printYourTree();
					}
					controlSwitch=false;
				}
			}
			System.out.println("\n---PROCESS TERMINATED---");
		}

		private synchronized void readyForNextRound(){
			//    	System.out.println("start function: readyForNextRound()");
			boolean bReadyForNextRound=true, bProcessDone=true;
			int iCount;

//			System.out.println("In readyForNextROund()");
			if(!replyAll){
				try {
//					System.out.println("In Master, entering wait state");
					waitState=true;
					wait();
					waitState=false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//      if any of the process has not completed their process, break from the loop
			//      and set bReadyForNextRound=False
			//      if any of the process is true, set the bProcessDone=false
			for(iCount=0;iCount<noOfNodes;iCount++){
				if(sProcessRoundStatus[iCount].compareToIgnoreCase("false")==0){
					bReadyForNextRound=false;
					bProcessDone=false;
					break;
				}
				else if(sProcessRoundStatus[iCount].compareToIgnoreCase("true")==0){
					bProcessDone=false;
				}
			}
			//            assign the status of the round completed in sStatus private variable
			if(bProcessDone){
				state=Rounds[2];
			}
			else if(bReadyForNextRound){
				state=Rounds[0];
				resetProcessRoundStatus();
			}
			else{
				state=Rounds[1];
			}
			//        System.out.println("End function: readyForNextRound()");
		}

		public synchronized void setProcessRoundStatus(int iIndex, String sStatus_temp){
//			System.out.println("Start of function: setProcessRoundStatus");
			sProcessRoundStatus[iIndex]=sStatus_temp;
			processFinished++;
			if(processFinished==noOfNodes){
				if(waitState)
				notify();
			}
//			System.out.println("End of function: setProcessRoundStatus");
		}

		//    reset the sProcessRoundStatus to False at the end of each round
		//    make the static variable to zero
		private void resetProcessRoundStatus() {
			// TODO Auto-generated method stub
			int iCount=0;
			for(iCount=0;iCount<noOfNodes;iCount++){
				sProcessRoundStatus[iCount]="false";
			}
			processFinished=0;
		}
		public void masterThread(String sThreadName){
			check=new Thread(this, sThreadName);
			check.start();
		}
	
		
	}

