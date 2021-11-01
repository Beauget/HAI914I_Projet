package qengine.program;

import java.util.ArrayList;


// Index represent 1 type of index SPO,OPS.. 
public class Index {
	
	
	private String order;
	private ArrayList<Triplet> index;
	
	public Index(String orderIndex) {
		order = orderIndex;
		index = new ArrayList<Triplet>();
	}
	
	
	public String getOrder() {
		return order;
	}
	
	public ArrayList<Triplet> getIndex() {
		return index;
	}
	
	
	public void addTriplet(Triplet toAdd) {	
		String toSwitch = this.getOrder();
		switch(toSwitch) {
		case "SPO" : 
			this.index.add(toAdd);
			break;
		case "SOP" : 
			this.index.add(toAdd);
			break;
		case "PSO" : 
			this.index.add(toAdd);
			break;
		case "POS" : 
			this.index.add(toAdd);
			break;
		case "OSP" : 
			this.index.add(toAdd);
			break;
		case "OPS" : 
			this.index.add(toAdd);
			break;
		}
	}
	
	
	public  void seeIndex() {
		for(Triplet t : index) {
			System.out.println(t.indexing[0] + " | " +  t.indexing[1] + " | " +  t.indexing[2]);
		}
	}
}
