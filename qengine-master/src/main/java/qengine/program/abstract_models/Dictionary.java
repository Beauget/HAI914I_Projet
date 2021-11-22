package qengine.program.abstract_models;

import java.util.HashMap;

import org.eclipse.rdf4j.model.Statement;

import deprecated.Pair;
import deprecated.Triplet;

public abstract class Dictionary {
	
	public Dictionary(){};
	
	public Integer[] updateDictionary(Statement st) {
		return null;
	}
	
	public Integer getKey(String value) {return null;}
	public String getValue(Integer i) {return null;}
	
}


