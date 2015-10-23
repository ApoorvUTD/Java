import org.omg.CORBA.PUBLIC_MEMBER;


public class Message_Protocol {
	
	//we should follow the following message protocol to ensure thread communication between the master and the thread performing it
/**
	Test(component, level, state, id)
	Response(boolean action, id)                    //action ==0, Reject; action == 1, Accept
	Report(id,global_MWOE)
	Merge(level,global_MWOE)
	Connect(id,level, global_MWOE)           
	New_Component(component, level, id)
	Start(round)
	Finish(id,level) 
	
**/	
	//Test the eligibility or that particular node or process whether it is in our component or separate.
	public void test(component, Level, state, id ){//this is where we check our component id and act accordingly
		if (component == component(i))//if component id received and the component id to which we are planning to commit are same
            response(0, id(i));//then dude reject it we don't need that or else it will form loop
      else 
             response(1, id(i))	;//or else if different component then take it and accept the connection
	}
	
	//response is the function to react when the reject or acept command is passed to it
	public void response(boolean action, id){
		min_w = Constant.INFINITY;
	             int min_woe = 0;//minimum weight outgoing edge
	             if  (action == 0){//check what if the Reject is passed that is node happen to be in same component hmmm..
	            	 
	                    update the neighbor with id to state reject
	             }else  (w(id(i),id) < min_w){
	                        min_w = w(id(i), id)
	                        local_MWOE = (id(i), id, min_w)
	                    }
	             if (){process i has no neighbors with state as branch
	                        global_MWOE = local_MWOE
	                        send Report(id, global_MWOE)}
	             
	}
	
	public void report(id, global_MWOE){
		//Let Xj to denote the report message from node j
        
        if min ( U(Xj) )  <  local_MWOE(i)
             channel_MWOE(i) = the id of the minimum incoming message
             global_MWOE(i) = local_MWOE
        else 
             channel_MWOE(i) = null
             global_MWOE(i) = min(U(Xj))
        if component(i) == id(i)           //component leader
             send Merge(level(i), global_MWOE(i)) to channel_MWOE(i)
        else 
             send Report(level(i), global_MWOE(i))
	}
	
	public void merge(level,global_MWOE){
		if local_MWOE(i) = global_MWOE
                send send Merge(level(i), global_MWOE(i)) to channel_MWOE(i)
           else 
                compare the elements (m, n, w) in global_MWOE
                update the neighbor n state from basic to branch
                parent(i) = n
                if  global_MWOE belongs to received_MWOE 
                       level(i) += 1
                       send Finish(id(i), level(i)) to master thread
                       if (m > n)
                            component(i) = m
                            parent = null
                       else
                            component(i) = n
                       send New_Component(component(i), level(i), id(i)) to all the neighbors with state as branch except for n
                send Connect(id(i), level(i), global_MWOE) to neighbor n

	}
	
	public void connect(id,level,global_MWOE){
		add global_MWOE to list received_MWOE(i) 
        update the neighbor id state from basic to branch
        if level(i)+1 == level
              level(i) = level
              send Finish(id(i), level(i)) to master thread
              compare the elements (m, n, w) in global_MWOE
              if (m > n)
                    component(i) = m
                    parent = m
               else
                    component(i) = n
                    parent = null
              send New_Component(component(i), level(i), id(i)) to all the neighbors with state as branch except for id
         else if level(i) == level + 1
              send New_Component(component(i), level(i), id(i)) to the neighbor id

	}
	
	public void newComponent(component,level,id){
		if level(i) +1 ==level
                component(i) = component
                parent(i) = id
                level(i) = level
                send Finish(id(i), level(i)) to master thread
                send New_Component(component(i), level(i), id(i)) to all the neighbors with state as branch except for id
	}
	
	public void start(round){
		if (round == level(i) + 1) & (id(i) == component(i))
        send Test(component(i), level(i), basic, id(i)) to all the neighbor j with state basic
        send Test(component(i), level(i), branch, id(i)) to all the neighbor j with state branch
	}
	
	public void fininsh(id,level){
		 if level(i) +1 ==level
                 component(i) = component
                 parent(i) = id
                 level(i) = level
                 send Finish(id(i), level(i)) to master thread
                 send New_Component(component(i), level(i), id(i)) to all the neighbors with state as branch except for id

	}
}
