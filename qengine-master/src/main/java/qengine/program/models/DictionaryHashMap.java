package qengine.program.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.rdf4j.model.Statement;

import qengine.program.abstract_models.Dictionary;

public class DictionaryHashMap extends Dictionary {
	
	private HashMap<Integer,String> dictionary= new HashMap<Integer,String>();
	static double execDictionnary = 0;
	
	public DictionaryHashMap(){
		super();
		execDictionnary = 0;
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<Integer,String>  getDictionary(){
		return dictionary;
	}
	
	public Integer[] updateDictionary(Statement st) {
		double start = System.currentTimeMillis();
		boolean subject = false;
		boolean predicate = false;
		boolean object = false;
		Integer [] toAdd = {0,0,0};			
		int subjectIndex = 0 , predicateIndex= 0, objectIndex = 0;

		 Iterator it = dictionary.entrySet().iterator();
		 
		 while (it.hasNext()) {
			 
			 HashMap.Entry dic = (Entry) it.next();
			 String value = (String) ((Entry) dic).getValue();
			 Integer key = (Integer) ((Entry) dic).getKey();
			 
			 if(value.equals(st.getSubject().toString())) {
					subject = true;
					subjectIndex= key;
				}
				
				if(value.equals(st.getPredicate().toString())) {
					predicate = true;
					predicateIndex = key;
				}
				
				if(value.equals(st.getObject().toString())) {
					object = true;
					objectIndex = key;
				}
		 }	
		
			
			if(!subject) {
				Integer compteurHm = dictionary.size()+1;
				dictionary.put(compteurHm, st.getSubject().toString());
				toAdd[0] = compteurHm;
			}
			else
				toAdd[0] = subjectIndex;
			if(!predicate) {
				Integer compteurHm = dictionary.size()+1;
				dictionary.put(compteurHm,st.getPredicate().toString());
				toAdd[1] = compteurHm;

			}
			else
				toAdd[1] = predicateIndex;
			if(!object) {
				Integer compteurHm = dictionary.size()+1;
				dictionary.put(compteurHm,st.getObject().toString());
				toAdd[2]= compteurHm;
			}
			else
				toAdd[2]= objectIndex;
			double end = System.currentTimeMillis();
			execDictionnary += (end - start);
			return toAdd;
	}
	public Integer getKey(String s) {
	 for ( Entry<Integer, String> entry : dictionary.entrySet()) {
		  String value = entry.getValue();
		  if(s.equals(value)) {
			  return entry.getKey();
		  }

		}
	 return null;
	}
	
	public static double getTimeDictionnary() {
		return execDictionnary;
	}
	
	public String getValue(Integer i) {
		return dictionary.get(i);
		}
	 public String toString() {
		 StringBuilder builder = new StringBuilder();
		 Iterator it = dictionary.entrySet().iterator();
		 
		 while (it.hasNext()) {
			 HashMap.Entry dic = (HashMap.Entry) it.next();
			 builder.append(((Entry) dic).getKey()+" : " + ((Entry) dic).getValue()+ "\n");
		 }
		 return builder.toString();
	}
}
