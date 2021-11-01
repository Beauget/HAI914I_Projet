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
		int tempSwitch = 0;
		
		

		switch(toSwitch) {
		case "SPO" : 
			System.out.println("SPO INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			index.add(toAdd);
			MainRDFHandler.seeIndex(MainRDFHandler.SPO);
			break;
		case "SOP" : 
			tempSwitch = toAdd.indexing[1];
			toAdd.indexing[1] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			System.out.println("SOP INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			index.add(toAdd);
			break;
		case "PSO" :
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[1];
			toAdd.indexing[1] = tempSwitch;
			
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			System.out.println("PSO INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			index.add(toAdd);
			break;
		case "POS" :
			tempSwitch = toAdd.indexing[1];
			toAdd.indexing[1] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[1];
			toAdd.indexing[1] = tempSwitch;
			System.out.println("POS INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			index.add(toAdd);
			break;
		case "OSP" :			
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[1];
			toAdd.indexing[1] = tempSwitch;
			
			tempSwitch = toAdd.indexing[1];
			toAdd.indexing[1] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			System.out.println("OSP INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			index.add(toAdd);
			break;
		case "OPS" :
			tempSwitch = toAdd.indexing[0];
			toAdd.indexing[0] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			
			tempSwitch = toAdd.indexing[1];
			toAdd.indexing[1] = toAdd.indexing[2];
			toAdd.indexing[2] = tempSwitch;
			index.add(toAdd);
			System.out.println("OPS INDEXING : " + toAdd.indexing[0] + " | " +  toAdd.indexing[1] + " | " +  toAdd.indexing[2]);
			break;
		}
	}
	
}
