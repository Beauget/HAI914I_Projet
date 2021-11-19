package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


// Index represent 1 type of index SPO,OPS.. 
public class IndexOpti {
	
	
	private String order;
	private HashMap<Integer,ArrayList<Triplet>> index;
	
	public IndexOpti(String orderIndex) {
		order = orderIndex;
		index = new HashMap<Integer,ArrayList<Triplet>>();
	}
	
	
	public String getOrder() {
		return order;
	}
	
	
	public HashMap<Integer,ArrayList<Triplet>> getIndex() {
		return this.index;
	}
	
	public void setIndex(HashMap<Integer,ArrayList<Triplet>> index) {
		this.index = index;
	}
	
	public void addTripletWithKey(Integer i,Triplet triplet) {
		//verifie si une value existe déja pour la key 
		ArrayList<Triplet> currentArray = index.get(i);
		if (currentArray==null){
			currentArray = new ArrayList<Triplet>();
			currentArray.add(triplet);
			index.put(i, currentArray);
		}
		else {
			currentArray.add(triplet);
			index.put(i, currentArray);
		}
	}


	

	
	public void addTriplet(Triplet toAdd) {	
		String toSwitch = this.getOrder();
		int tempSwitch = 0;
		Triplet addTo = new Triplet(toAdd.indexing[0], toAdd.indexing[1], toAdd.indexing[2]);
		switch(toSwitch) {
		case "SPO" : 
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		case "SOP" : 
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		case "PSO" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[1];
			addTo.indexing[1] = tempSwitch;
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		case "POS" :
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		case "OSP" :			

			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		case "OPS" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			addTripletWithKey(toAdd.indexing[0],toAdd);
			break;
		default:
			System.out.println("Default switch case");
			break;
		}
	}
	
	 public String toString() {
		 System.out.println("Je suis bien l'index : " + this.getOrder());
		 Iterator it = index.entrySet().iterator();
		 StringBuilder builder = new StringBuilder();
		 while (it.hasNext()) {
			 HashMap.Entry pair = (HashMap.Entry)it.next();
			 builder.append(pair.getKey() + " = " + pair.getValue().toString()+"\n");
		 }		 
		 return builder.toString();
		}

	
}
