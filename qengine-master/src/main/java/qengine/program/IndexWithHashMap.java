package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class IndexWithHashMap {
	
	private String order;
	private HashMap<Integer,Triplet> index;
	
	public IndexWithHashMap(String orderIndex) {
		order = orderIndex;
		index = new HashMap<Integer,Triplet>();
	}
	
	
	public String getOrder() {
		return order;
	}
	

	
	
	public HashMap<Integer,Triplet> getIndex() {
		return this.index;
	}
	
	public void setIndex(HashMap<Integer,Triplet> index) {
		this.index = index;
	}


	

	
	public void addTriplet(Triplet toAdd) {	
		String toSwitch = this.getOrder();
		int tempSwitch = 0;
		Triplet addTo = new Triplet(toAdd.indexing[0], toAdd.indexing[1], toAdd.indexing[2]);
		switch(toSwitch) {
		case "SPO" : 
			//System.out.println("SPO INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.put(addTo.indexing[0], addTo);
			break;
		case "SOP" : 
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			//System.out.println("SOP INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.put(addTo.indexing[0],addTo);
			break;
		case "PSO" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[1];
			addTo.indexing[1] = tempSwitch;
			

			//System.out.println("PSO INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.put(addTo.indexing[0],addTo);
			break;
		case "POS" :
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			//System.out.println("POS INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.put(addTo.indexing[0],addTo);
			break;
		case "OSP" :			

			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
		//	System.out.println("OSP INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.put(addTo.indexing[0],addTo);
			break;
		case "OPS" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			this.index.put(addTo.indexing[0],addTo);
			//System.out.println("OPS INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			break;
		default:
			System.out.println("Default switch case");
			break;
		}
	}
	
	 public String toString() {
		 System.out.println("Je suis bien l'index : " + this.getOrder());
		 String toSee = "";
		 Iterator it = this.getIndex().entrySet().iterator();
		 while(it.hasNext()) {
			 System.out.println("pouet");
			 System.out.println(it.next().toString());
			// toSee += it.next().[0] + " | " +  t.indexing[1] + " | " +  t.indexing[2] + "\n";
		 }
			return toSee;
		}

	

}
