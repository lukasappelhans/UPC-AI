

import java.util.List;
import java.util.ArrayList;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class AISuccessorFunction implements SuccessorFunction {

	@Override
	public List getSuccessors(Object s) {
		ArrayList retVal = new ArrayList();
		AIState state = (AIState)s;
		//AIHeuristicFunction heuristic = new AIHeuristicFunction();
		//System.out.println("State heuristic: " + heuristic.getHeuristicValue(state));
		
		//SWAP
		
		for (int i = 0; i != state.drives.size(); ++i) {
			//System.out.println("SWAP");
			AIState.Drive d = state.drives.get(i);
			for (int j = 1; j < d.actions.size() - 1; ++j) {
		        for (int k = j + 1; k < d.actions.size() - 1; ++k) {
	            	AIState newState = new AIState(state);
	            	newState.swap(i, j, k);
	            	if (newState.actionRestrictionsValid(i)) {
	            		String S = AIState.SWAP + " " + newState.toString();
	            		retVal.add(new Successor(S, newState));
	            	}
		        }
			}
		}
		//EXCHANGE
		
		for (int i = 0; i != state.drives.size(); ++i) {
			//System.out.println("EXCHANGE");
			AIState.Drive d1 = state.drives.get(i);
			for (int j = i + 1; j != state.drives.size(); ++j) {
				AIState.Drive d2 = state.drives.get(j);
				for (int k = 0; k != d1.actions.size(); ++k) {
					int id1 = d1.actions.get(k);
					if (id1 < 0)
						continue;
				
					for (int l = 0; l != d2.actions.size(); ++l) {
						int id2 = d2.actions.get(l);
						if (id2 < 0)
							continue;
						//driver swap check
						if(((k==0)&&(!state.Users.get(id2-1).isConductor()))||((l==0)&&(!state.Users.get(id1-1).isConductor())))
							continue;
						
		            	AIState newState = new AIState(state);
		            	newState.exchange(i, j, id1, id2);
		                String S = AIState.EXCHANGE + " " + newState.toString();
		                retVal.add(new Successor(S, newState));
					}
				}
			}
		}
		//MOVE
		
		for (int i = 0; i != state.drives.size(); ++i) {
			//System.out.println("MOVE");
			AIState.Drive d1 = state.drives.get(i);
			for (int j = 0; j != state.drives.size(); ++j) {

				if (i == j)
					continue;
				
				AIState.Drive d2 = state.drives.get(j);
				
				for (int k = 0; k != d1.actions.size(); ++k) {
					
					int action = d1.actions.get(k);
					
					if (action<0)
						continue;
					//Don't move out the driver unless the driver is the only one left
					if ((k==0)&&(d1.actions.size()!=2))
						continue;
					
					//Determine the positions to be moved to
					for (int ind1 = 1; ind1 < (d2.actions.size() - 1); ++ind1) {
						
						for (int ind2 = ind1 + 1; ind2 < (d2.actions.size() - 1); ++ind2) {
							
							AIState newState = new AIState(state);
		            		boolean removes = newState.move(i, j, action, ind1, ind2);
		            		boolean valid = removes ? newState.actionRestrictionsValid() : 
		            			(newState.actionRestrictionsValid(i) && newState.actionRestrictionsValid(j));
		            		if (valid) {
		            			String S = AIState.MOVE + " i: " + i + ", j: " + j + ", action: " + action + ", ind1: " + ind1 + ", ind2: " + ind2 + ": " + newState.toString();
		                		retVal.add(new Successor(S, newState));
		            		}
						}
					}
	                
	                //if the user is a conductor he can also be moved to a new drive
	                if (state.Users.get(action - 1).isConductor()) {
	                	AIState newState2 = new AIState(state);
		            	newState2.move(i, action); 	
		                String S = AIState.MOVE + " i: " + i + " action: " + action + "\n " + newState2.toString();
		                retVal.add(new Successor(S, newState2));
	                }
	                	
				}
			}
		}
		
		//System.out.println("Found " + retVal.size());
		/*for (Object o : retVal) {
			Successor suc = (Successor)o;
			double heuristicValue = heuristic.getHeuristicValue(suc.getState());
			System.out.println("Successor-heuristic: " + heuristicValue);
			if (heuristicValue < 1) {
				System.out.println(suc.getAction());
			}
		} */
		return retVal;
	}

}
