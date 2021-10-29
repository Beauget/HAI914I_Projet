package qengine.program;

import java.util.ArrayList;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;



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
	ArrayList<Pair> dictionnary = new ArrayList<Pair>();
	int compteur = 1;
	@SuppressWarnings("deprecation")
	@Override
	public void handleStatement(Statement st) {
		System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		boolean subject = false;
		boolean predicate = false;
		boolean object = false;
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
			compteur++;
		}
		if(!predicate) {
			dictionnary.add(new Pair(compteur,st.getPredicate().getLocalName()));
			compteur++;
		}
		if(!object) {
			dictionnary.add(new Pair(compteur,st.getObject().toString()));
			compteur++;
		}
		System.out.println(dictionnary.toString());
	};
	
	
}