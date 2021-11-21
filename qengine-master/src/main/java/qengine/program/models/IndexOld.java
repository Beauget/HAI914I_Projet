package qengine.program.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import qengine.program.abstract_models.Index;


// Index represent 1 type of index SPO,OPS.. 
public class IndexOld extends Index{
	
	
	private String order;
	private HashMap<Integer,HashMap<Integer,List<Integer>>> index;
	
	public IndexOld(String orderIndex) {
		super(orderIndex);
		order = orderIndex;
		index = new HashMap<Integer,HashMap<Integer,List<Integer>>>();
	}
	
	
	public String getOrder() {
		return order;
	}
	
	
	public HashMap<Integer,HashMap<Integer,List<Integer>>> getIndex() {
		return this.index;
	}
	
	public void setIndex(HashMap<Integer,HashMap<Integer,List<Integer>>> index) {
		this.index = index;
	}
	
	public void addTripletWithKey(Integer first,Integer second,Integer third) {
		//verifie si la premier hashmap existe
		HashMap<Integer,List<Integer>> secondHashMap = index.get(first);
		//si la premiere hash n'existe pas je l'ajoute
		if(secondHashMap==null) {
			index.put(first,new HashMap<Integer,List<Integer>>());
		}
		//verifie si la deuxieme hashMap existe
		List<Integer> currentArray = index.get(first).get(second);
		if (currentArray==null){
			secondHashMap = index.get(first);
			currentArray = new ArrayList<Integer>();
			currentArray.add(third);
			secondHashMap.put(second,currentArray);
			index.put(first, secondHashMap);
		}
		else {
			secondHashMap = index.get(first);
			currentArray.add(third);
			secondHashMap.put(second,currentArray);
			index.put(first, secondHashMap);
		}
		
	}


	

	
	public void add(Integer [] toAdd) {	
		String toSwitch = this.getOrder();
		int tempSwitch = 0;
		Triplet addTo = new Triplet(toAdd[0], toAdd[1], toAdd[2]);
		switch(toSwitch) {
		case "SPO" : 
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
			break;
		case "SOP" : 
			tempSwitch = addTo.indexing[1];
			addTo.indexing[1] = addTo.indexing[2];
			addTo.indexing[2] = tempSwitch;
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
			break;
		case "PSO" :
			tempSwitch = toAdd[0];
			toAdd[0] = toAdd[1];
			toAdd[1] = tempSwitch;
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
			break;
		case "POS" :
			tempSwitch = toAdd[1];
			toAdd[1] = toAdd[2];
			toAdd[2] = tempSwitch;
			
			tempSwitch = toAdd[0];
			toAdd[0] = toAdd[2];
			toAdd[2] = tempSwitch;
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
			break;
		case "OSP" :			

			tempSwitch = toAdd[0];
			toAdd[0] = toAdd[2];
			toAdd[2] = tempSwitch;
			
			tempSwitch = toAdd[1];
			toAdd[1] = toAdd[2];
			toAdd[2] = tempSwitch;
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
			break;
		case "OPS" :
			tempSwitch = toAdd[0];
			toAdd[0] = toAdd[2];
			toAdd[2] = tempSwitch;
			
			addTripletWithKey(toAdd[0],toAdd[1],toAdd[2]);
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
		 /*while (it.hasNext()) {
			 HashMap.Entry first = (HashMap.Entry) it.next();
			 Iterator it2 = first.entrySet().iterator();
			 while (it2.hasNext()) {
				 HashMap.Entry second = (HashMap.Entry)it2.next();
				 builder.append(second.getKey() + " = " + second.getValue().toString()+"\n");
			 }
		 }*/
		 for (Entry<Integer, HashMap<Integer, List<Integer>>> entry1 : index.entrySet()) {
			  Integer key1 = entry1.getKey();
			  HashMap<Integer, List<Integer>> value1 = entry1.getValue();
			  for (Entry<Integer, List<Integer>> entry2 : value1.entrySet()) {
				  Integer key2 = entry2.getKey();
				  List<Integer> value2 = entry2.getValue();
				  builder.append(key1 + " , " + key2 + " : " + value2.toString() +"\n");
			  }
			  // do whatever with value1 and value2 
			}
		 return builder.toString();
		}

	
}
