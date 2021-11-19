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
import java.util.List;
import java.util.Map.Entry;

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
	
	//static final String outputDictionnary = "/home/dnspc/Desktop/M2/NoSQL/ProjetMoodle/qengine-master/output/dictionnary.txt";
	//static final String outputIndex = "/home/dnspc/Desktop/M2/NoSQL/ProjetMoodle/qengine-master/output/";
	static final String outputDictionnary = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/dictionnary.txt";
	static final String outputIndex = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output";
	static ArrayList<Pair> dictionnary = new ArrayList<Pair>();
	static HashMap<Integer,String> dictionnaryHashMap = new HashMap<Integer,String>();

	

	
	static Index SPO = new Index("SPO");
	static Index SOP = new Index("SOP");
	static Index PSO = new Index("PSO");
	static Index POS = new Index("POS");
	static Index OSP = new Index("OSP");
	static Index OPS = new Index("OPS");
	
	static IndexOpti SPOHM = new IndexOpti("SPO");
	static IndexOpti SOPHM = new IndexOpti("SOP");
	static IndexOpti PSOHM = new IndexOpti("PSO");
	static IndexOpti POSHM = new IndexOpti("POS");
	static IndexOpti OSPHM = new IndexOpti("OSP");
	static IndexOpti OPSHM = new IndexOpti("OPS");
	int compteur = 1;
	int compteurHm = 1;
	@Override
	public void handleStatement(Statement st) {
		//avec un dictionnaire Pair et une index arrayList<Triplet>
		//classicMain(st);
		//avec un dictionnaire HashMap<Iteger,String> et une index Hashmap<Integer,arrayList<Triplet>>
		hashMapMain(st);
	};
	
	
	
	 public static void seeDictionnary(ArrayList<Pair> dictionnary) {
		for(Pair p : dictionnary) {
			System.out.print(p.toString());
		}
	}
	 
	 public static String seeHashMapDictionnary() {
		 
		 StringBuilder builder = new StringBuilder();
		 Iterator it = dictionnaryHashMap.entrySet().iterator();
		 
		 while (it.hasNext()) {
			 HashMap.Entry dic = (HashMap.Entry) it.next();
			 builder.append(((Entry) dic).getKey()+" : " + ((Entry) dic).getValue()+ "\n");
		 }
		 return builder.toString();
	}
	 
	public static void writeDictionnary(ArrayList<Pair> dictionnary) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputDictionnary);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	       for (Pair s : dictionnary) {    	   
	    	      try {
					fw.write(s.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       }
	       fw.close();
	   }
	public static String toStringDictionnary() {
		StringBuilder builder = new StringBuilder();
		for (Pair s : dictionnary) {    
			builder.append(s.toString());
		}
		return builder.toString();
	}
	
	public static void writeDictionnary2() {
		FileWriter fw = null;
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
			

			SPO.addTriplet(toAdd);
			SOP.addTriplet(toAdd);
			PSO.addTriplet(toAdd);
			POS.addTriplet(toAdd);
			OSP.addTriplet(toAdd);
			OPS.addTriplet(toAdd);
	}
	
	private void hashMapMain(Statement st){
			boolean subject = false;
			boolean predicate = false;
			boolean object = false;
			Integer [] toAdd = {0,0,0};			
			int i = 0;
			int subjectIndex = 0 , predicateIndex= 0, objectIndex = 0;

			 Iterator it = dictionnaryHashMap.entrySet().iterator();
			 
			 while (it.hasNext()) {
				 
				 HashMap.Entry dic = (Entry) it.next();
				 String value = (String) ((Entry) dic).getValue();
				 Integer key = (Integer) ((Entry) dic).getKey();
				 
				 if(value.equals(st.getSubject().toString())) {
						subject = true;
						subjectIndex= key;
					}
					
					if(value.equals(st.getPredicate().getLocalName())) {
						predicate = true;
						predicateIndex = key;
					}
					
					if(value.equals(st.getObject().toString())) {
						object = true;
						objectIndex = key;
					}
			 }	
			
				
				if(!subject) {
					compteurHm = dictionnaryHashMap.size()+1;
					dictionnaryHashMap.put(compteurHm, st.getSubject().toString());
					toAdd[0] = compteurHm;
				}
				else
					toAdd[0] = subjectIndex;
				if(!predicate) {
					compteurHm = dictionnaryHashMap.size()+1;
					dictionnaryHashMap.put(compteurHm,st.getPredicate().getLocalName());
					toAdd[1] = compteurHm;

				}
				else
					toAdd[1] = predicateIndex;
				if(!object) {
					compteurHm = dictionnaryHashMap.size()+1;
					dictionnaryHashMap.put(compteurHm,st.getObject().toString());
					toAdd[2]= compteurHm;
				}
				else
					toAdd[2]= objectIndex;

				SPOHM.add(toAdd);
				SOPHM.add(toAdd);
				PSOHM.add(toAdd);
				POSHM.add(toAdd);
				OSPHM.add(toAdd);
				OPSHM.add(toAdd);

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
	       	fw.close();
	    	  }
	}
	