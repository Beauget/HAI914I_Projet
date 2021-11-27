package qengine.program.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import qengine.program.abstract_models.Index;


// Index represent 1 type of index SPO,OPS.. 
public class IndexOpti extends Index{
	
	
	static double execIndex = 0;
	
	private   String order;
	private   HashMap<Integer,HashMap<Integer,List<Integer>>> index;
	
	public IndexOpti(String orderIndex) {
		super(orderIndex);
		order = orderIndex;
		index = new HashMap<Integer,HashMap<Integer,List<Integer>>>();
		execIndex = 0;
	}
	
	
	public    String getOrder() {
		return order;
	}
	
	
	public   HashMap<Integer,HashMap<Integer,List<Integer>>> getIndex() {
		return index;
	}
	
	public void setIndex(HashMap<Integer,HashMap<Integer,List<Integer>>> index) {
		this.index = index;
	}
	
	public   void add(Integer first,Integer second,Integer third) {
		double start = System.currentTimeMillis();
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
		
		double end = System.currentTimeMillis();
		
		execIndex += (end - start);
		
	}
	
	
	public static double getExecIndex() {
		return execIndex;
	}

	public   List<Integer> getAnswer(Integer first,Integer second) {
		if(index.get(first)!=null) {
			return index.get(first).get(second);
		}
		return null;
	}
	
	 public String toString() {
		 Iterator it = index.entrySet().iterator();
		 StringBuilder builder = new StringBuilder();

		 for (Entry<Integer, HashMap<Integer, List<Integer>>> entry1 : index.entrySet()) {
			  Integer key1 = entry1.getKey();
			  HashMap<Integer, List<Integer>> value1 = entry1.getValue();
			  for (Entry<Integer, List<Integer>> entry2 : value1.entrySet()) {
				  Integer key2 = entry2.getKey();
				  List<Integer> value2 = entry2.getValue();
				  builder.append(key1 + " , " + key2 + " : " + value2.toString() +"\n");
			  }
			}
		 return builder.toString();
		}

	
}
