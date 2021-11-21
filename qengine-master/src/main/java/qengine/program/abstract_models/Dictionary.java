package qengine.program.abstract_models;

import java.util.HashMap;

import org.eclipse.rdf4j.model.Statement;

import qengine.program.models.Pair;
import qengine.program.models.Triplet;

public abstract class Dictionary {
	
	public Dictionary(){};
	
	public Integer[] updateDictionary(Statement st) {
		return null;
	}
	
	public Integer getKey(String value) {return null;}
	public String getValue(Integer i) {return null;}
	
}


/*
	
	private void classicMain(Statement st) {
		boolean subject = false;
		boolean predicate = false;
		boolean object = false;
		Triplet toAdd = new Triplet(0,0,0);
		int i = 0;
		int subjectIndex = 0 , predicateIndex= 0, objectIndex = 0;
		for(Pair p : dictionnary) {
			
			if(p.isSameValue(st.getSubject().toString())) {
				subject = true;
				subjectIndex=i;
			}
			
			if(p.isSameValue(st.getPredicate().getLocalName())) {
				predicate = true;
				predicateIndex = i;
			}
			
			if(p.isSameValue(st.getObject().toString())) {
				object = true;
				objectIndex = i;
			}
			i++;
		}
		
			
			if(!subject) {
				dictionnary.add(new Pair(compteur,st.getSubject().toString()));
				toAdd.indexing[0] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[0] = subjectIndex;
			if(!predicate) {
				dictionnary.add(new Pair(compteur,st.getPredicate().getLocalName()));
				toAdd.indexing[1] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[1] = predicateIndex;
			if(!object) {
				dictionnary.add(new Pair(compteur,st.getObject().toString()));
				toAdd.indexing[2] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[2] = objectIndex;
			

			SPO.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
			SOP.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
			PSO.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
			POS.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
			OSP.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
			OPS.add(toAdd.indexing[0],toAdd.indexing[1],toAdd.indexing[2]);
	}
 */