package qengine.program.models;

import java.util.ArrayList;

public class Query {
	ArrayList<String> values = new ArrayList<String>();
	
	public Query(String s1, String s2, String s3) {
		values.add(s1);
		values.add(s2);
		values.add(s3);
	}
	
	public ArrayList<String> getValues() {return this.values;}
	
	public String toString() {
		return values.toString();
	}
}
