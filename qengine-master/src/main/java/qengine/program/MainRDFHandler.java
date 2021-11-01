package qengine.program;

import java.util.ArrayList;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import qengine.program.Triplet;



/**
 * Le RDFHandler intervient lors du parsing de données et permet d'appliquer un traitement pour chaque élément lu par le parseur.
 * 
 * <p>
 * Ce qui servira surtout dans le programme est la méthode {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple lu.
 * </p>
 * <p>
 * À adapter/réécrire selon vos traitements.
 * </p>
 */
public final class MainRDFHandler extends AbstractRDFHandler {
	static ArrayList<Pair> dictionnary = new ArrayList<Pair>();
	
	static Index SPO = new Index("SPO");
	static Index SOP = new Index("SOP");
	static Index PSO = new Index("PSO");
	static Index POS = new Index("POS");
	static Index OSP = new Index("OSP");
	static Index OPS = new Index("OPS");
	
	int compteur = 1;
	@Override
	public void handleStatement(Statement st) {
		//System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		boolean subject = false;
		boolean predicate = false;
		boolean object = false;
		Triplet toAdd = new Triplet(0,0,0);
		for(Pair p : dictionnary) {
			
			if(p.isSameValue(st.getSubject().toString())) {
				subject = true;
			}
			
			if(p.isSameValue(st.getPredicate().getLocalName())) {
				predicate = true;
			}
			
			if(p.isSameValue(st.getObject().toString())) {
				object = true;
			}
		}
		
			
			if(!subject) {
				dictionnary.add(new Pair(compteur,st.getSubject().toString()));
				toAdd.indexing[0] = compteur;
				compteur++;
			}
			if(!predicate) {
				dictionnary.add(new Pair(compteur,st.getPredicate().getLocalName()));
				toAdd.indexing[1] = compteur;
				compteur++;
			}
			if(!object) {
				dictionnary.add(new Pair(compteur,st.getObject().toString()));
				toAdd.indexing[2] = compteur;
				compteur++;
			}
			
			
			SPO.addTriplet(toAdd);
			SOP.addTriplet(toAdd);
			PSO.addTriplet(toAdd);
			POS.addTriplet(toAdd);
			OSP.addTriplet(toAdd);
			OPS.addTriplet(toAdd);
			


	};
	
	
	
	static public void seeDictionnary(ArrayList<Pair> dictionnary) {
		for(Pair p : dictionnary) {
			System.out.print(p.toString());
		}
	}
	
	static public  void seeIndex(Index toSee) {
		for(Triplet t : toSee.getIndex()) {
			System.out.println(t.indexing[0] + " | " +  t.indexing[1] + " | " +  t.indexing[2]);
		}
	}
	
	
}