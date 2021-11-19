package qengine.program;

import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import qengine.program.Triplet;
import static java.nio.file.StandardOpenOption.*;



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
	
	static final String outputDictionnary = "/home/dnspc/Desktop/M2/NoSQL/Projet/HAI914I_Projet/qengine-master/output/dictionnary.txt";
	static final String outputIndex = "/home/dnspc/Desktop/M2/NoSQL/Projet/HAI914I_Projet/qengine-master/output/";
	//static final String outputDictionnary = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/dictionnary.txt";
	//static final String outputIndex = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/";
	static ArrayList<Pair> dictionnary = new ArrayList<Pair>();
	
	static HashMap<Integer,String> dictionnaryWithHashMap = new HashMap<Integer,String>();
	

	
	static Index SPO = new Index("SPO");
	static Index SOP = new Index("SOP");
	static Index PSO = new Index("PSO");
	static Index POS = new Index("POS");
	static Index OSP = new Index("OSP");
	static Index OPS = new Index("OPS");
	
	
	static IndexWithHashMap SPOHM = new IndexWithHashMap("SPO");
	static IndexWithHashMap SOPHM = new IndexWithHashMap("SOP");
	static IndexWithHashMap PSOHM = new IndexWithHashMap("PSO");
	static IndexWithHashMap POSHM = new IndexWithHashMap("POS");
	static IndexWithHashMap OSPHM = new IndexWithHashMap("OSP");
	static IndexWithHashMap OPSHM = new IndexWithHashMap("OPS");
	
	int compteur = 1;
	@Override
	public void handleStatement(Statement st) {
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
				dictionnaryWithHashMap.put(compteur, st.getSubject().toString());
				toAdd.indexing[0] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[0] = subjectIndex;
			if(!predicate) {
				dictionnary.add(new Pair(compteur,st.getPredicate().getLocalName()));
				dictionnaryWithHashMap.put(compteur, st.getPredicate().getLocalName());
				toAdd.indexing[1] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[1] = predicateIndex;
			if(!object) {
				dictionnary.add(new Pair(compteur,st.getObject().toString()));
				dictionnaryWithHashMap.put(compteur, st.getObject().toString());
				toAdd.indexing[2] = compteur;
				compteur++;
			}
			else
				toAdd.indexing[2] = objectIndex;
			

			SPO.addTriplet(toAdd);
			SOP.addTriplet(toAdd);
			PSO.addTriplet(toAdd);
			POS.addTriplet(toAdd);
			OSP.addTriplet(toAdd);
			OPS.addTriplet(toAdd);
			
			SPOHM.addTriplet(toAdd);
			SOPHM.addTriplet(toAdd);
			PSOHM.addTriplet(toAdd);
			POSHM.addTriplet(toAdd);
			OSPHM.addTriplet(toAdd);
			OPSHM.addTriplet(toAdd);
			
			



	};
	
	
	
	 public static void seeDictionnary(ArrayList<Pair> dictionnary) {
		for(Pair p : dictionnary) {
			System.out.print(p.toString());
		}
	}
	 
	 public static void seeDictionnaryHashMap(HashMap<Integer,String> map) {
		 Iterator it = map.entrySet().iterator();
		 while(it.hasNext()) {
			 System.out.println(it.next());
		 }
		}
	 
	 
	 public static String toStringDictionnary() {
		 StringBuilder builder = new StringBuilder();
		for(Pair p : dictionnary) {
			builder.append(p.toString()+"\n");
		}
		return builder.toString();
	}
	 
	public static void writeDictionnary(ArrayList<Pair> dictionnary) {
		FileWriter fw = null;
		//seeDictionnary(dictionnary);
		try {
			fw = new FileWriter(outputDictionnary);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		try {
			fw.write(toStringDictionnary());
			fw.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	   }
	
	
	public static void writeIndex(Index toWrite) throws IOException {
		String path = outputIndex + toWrite.getOrder() + ".txt";
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	       for (Triplet t : toWrite.getIndex()) {    	   
	    	      try {
					fw.write(t.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    }
 	      try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	  }

}

	