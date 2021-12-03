package qengine.program.models;

import java.util.ArrayList;

public class Query {
	String realQuery;
	ArrayList<Select> query = new ArrayList<Select>();
	
	public Query(String rQ) {
		this.realQuery = rQ;
	}
	
	
	public void setQuery(ArrayList<Select> query) {
		this.query = query;
	}


	public ArrayList<Select> getQuery() {return this.query;}
	
	
	public String getRealQuery() {
		return realQuery;
	}
	public boolean isSameAs(Query q) {
		return this.getQuery().containsAll(q.getQuery());
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(Select s : query) {
			builder.append(s.toString()+"\n");
		}
		
		return builder.toString();
	}
}
