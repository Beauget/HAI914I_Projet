package qengine.program.models;

import java.util.ArrayList;

public class Query {
	String realQuery;
	ArrayList<Select> query = new ArrayList<Select>();
	
	public Query(String s) {
		this.realQuery = s;
	}
	
	public Query(Select s) {
		realQuery = s.toString();
		this.query.add(s);
	}
	
	
	public void setQuery(ArrayList<Select> query) {
		this.query = query;
	}


	public ArrayList<Select> getQuery() {return this.query;}
	
	
	public String getRealQuery() {
		return realQuery;
	}
	
	public boolean equals(Query q) {
		//Ici on supose que peux importe la taille du doublons, si les deux contient tout les objet de l'autre, c'est la même query
		boolean output = equalsOneSide(this,q)&&equalsOneSide(q,this);
		return output;
	}
	
	public boolean equalsOneSide(Query q1,Query q2) {
		//On verifie si l'égalité est vrai pour chaque select
		for (int i = 0; i < q1.getQuery().size(); i++) {
			boolean isTrueAtLeastOneTime = false;
			for (int j = 0; j < q2.getQuery().size(); j++) {
				if(q1.getQuery().get(i).equals(q2.getQuery().get(j))) {
					isTrueAtLeastOneTime=true;
				}
			}
			if(!isTrueAtLeastOneTime) {
				return false;
			}
		}
		return true;
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ?v0 WHERE {\n");
		for(Select s : query) {
			builder.append(s.toString());
		}
		builder.append("\n}");
		
		return builder.toString();
	}
}
