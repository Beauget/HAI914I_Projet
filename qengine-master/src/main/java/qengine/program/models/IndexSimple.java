package qengine.program.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import qengine.program.abstract_models.Index;


// Index represent 1 type of index SPO,OPS.. 
public class IndexSimple extends Index {
	
	
	private String order;
	private ArrayList<Triplet> index;
	
	public IndexSimple(String orderIndex) {
		super (orderIndex);
		order = orderIndex;
		index = new ArrayList<Triplet>();
	}
	
	
	public String getOrder() {
		return order;
	}
	

	
	
	public ArrayList<Triplet> getIndex() {
		return this.index;
	}
	
	public void setIndex(ArrayList<Triplet> index) {
		this.index = index;
	}


	

	
	public void add(Integer first,Integer second,Integer third) {	
		Triplet toAdd = new Triplet(first, second, third);
		String toSwitch = this.getOrder();
		int tempSwitch = 0;
		Triplet addTo = new Triplet(toAdd.indexing[0], toAdd.indexing[1], toAdd.indexing[2]);
		switch(toSwitch) {
		case "SPO" : 
			//System.out.println("SPO INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.getIndex().add(addTo);
			break;
		case "SOP" : 
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			//System.out.println("SOP INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.add(addTo);
			break;
		case "PSO" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[1];
			addTo.indexing[1] = tempSwitch;
			

			//System.out.println("PSO INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.add(addTo);
			break;
		case "POS" :
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			//System.out.println("POS INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.add(addTo);
			break;
		case "OSP" :			

			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
		//	System.out.println("OSP INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			this.index.add(addTo);
			break;
		case "OPS" :
			tempSwitch = addTo.indexing[0];
			addTo.indexing[0] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			
			this.index.add(addTo);
			//System.out.println("OPS INDEXING : " + addTo.indexing[0] + " | " +  addTo.indexing[1] + " | " +  addTo.indexing[2]);
			break;
		default:
			System.out.println("Default switch case");
			break;
		}
	}
	
	 public String toString() {
		 StringBuilder builder = new StringBuilder();
			for(Triplet t : this.getIndex()) {
				builder.append(t.indexing[0] + " | " +  t.indexing[1] + " | " +  t.indexing[2] + "\n");
			}
			return builder.toString();
		}
	
}
