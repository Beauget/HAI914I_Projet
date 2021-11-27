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
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import deprecated.IndexSimple;
import deprecated.Pair;
import deprecated.Triplet;
import qengine.program.abstract_models.Dictionary;
import qengine.program.abstract_models.Index;
import qengine.program.models.DictionaryHashMap;
import qengine.program.models.IndexOpti;

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
	
	//static final String outputDictionnary = "/home/dnspc/Desktop/M2/NoSQL/ProjetSSH/HAI914I_Projet/qengine-master/output/dictionnary.txt";
	//static final String outputIndex = "/home/dnspc/Desktop/M2/NoSQL/ProjetSSH/HAI914I_Projet/qengine-master/output/";
	//static final String outputDictionnary = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/dictionnary.txt";
	//static final String outputIndex = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/";
	static final String outputDictionnary = Main.outputPath + "/dictionnary.txt";
	static final String outputIndex = Main.outputPath;
	
	
	
	
	static Dictionary dictionary = new DictionaryHashMap();	
	static Index SPO = new IndexOpti("SPO");
	static Index SOP = new IndexOpti("SOP");
	static Index PSO = new IndexOpti("PSO");
	static Index POS = new IndexOpti("POS");
	static Index OSP = new IndexOpti("OSP");
	static Index OPS = new IndexOpti("OPS");

	static int nbTriplet = 0;
	
	
	
	@Override
	public void handleStatement(Statement st) {
		nbTriplet++;
		//avec un dictionnaire HashMap<Iteger,String> et une index Hashmap<Integer,arrayList<Triplet>>
		Integer[] toAdd = dictionary.updateDictionary(st);
		//System.out.println(toAdd[0] +" "+toAdd[1]+" "+toAdd[2]);
		add(toAdd[0], toAdd[1], toAdd[2],SPO);
		add(toAdd[0], toAdd[1], toAdd[2],SOP);
		add(toAdd[0], toAdd[1], toAdd[2],PSO);
		add(toAdd[0], toAdd[1], toAdd[2],POS);
		add(toAdd[0], toAdd[1], toAdd[2],OSP);
		add(toAdd[0], toAdd[1], toAdd[2],OPS);

	};
	
	 
	public static void writeDictionnary() throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputDictionnary);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 	   
		try {
			fw.write(dictionary.toString());
			fw.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void writeIndex() throws IOException {
		ArrayList<Index> indexes = indexesToArray();
		for(Index i : indexes) {
			String path = outputIndex + i.getOrder() + ".txt";
			FileWriter fw = null;
			try {
				fw = new FileWriter(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  	   
			try {
				fw.write(i.toString());
				fw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<Index> indexesToArray(){
		ArrayList<Index> indexes = new ArrayList<Index>();
		indexes.add(SPO);
		//System.out.println(SPO.toString());
		indexes.add(SOP);
		//System.out.println(SOP.toString());
		indexes.add(PSO);
		//System.out.println(PSO.toString());
		indexes.add(POS);
		//System.out.println(POS.toString());
		indexes.add(OSP);
		//System.out.println(OSP.toString());
		indexes.add(OPS);
		//System.out.println(OPS.toString());

		return indexes;
	}
	public void add(Integer s,Integer p,Integer o , Index idx) {
		String toSwitch = idx.getOrder();
		switch(toSwitch) {
		case "SPO" : 
			idx.add(s,p,o);
			break;
		case "SOP" : 
			idx.add(s,o,p);
			break;
		case "PSO" :
			idx.add(p,s,o);
			break;
		case "POS" :
			idx.add(p,o,s);
			break;
		case "OSP" :			
			idx.add(o,s,p);
			break;
		case "OPS" :
			idx.add(o,p,s);
			break;
		default:
			System.out.println("Default switch case");
			break;
		}
	}
	
	
	public static void writeToCSV(ArrayList<String> addToCSV , String optionName) {
		String toWrite = "nomDuFichierDonnee,nomDuFichierRequete,NombreDeTriplet,NombreDeRequete,TempsDeLectureDonnee,TempsDeLectureRequete,TempsDeCreationDico,NombreIndex,TempsCreationIndex,TempsTotalEval,TempsTotal\n";
		String path = outputIndex + optionName + ".csv";
		for(String s : addToCSV) {
			toWrite+= s+",";
		}
		toWrite = toWrite.substring(0,toWrite.length()-1);
		FileWriter fw = null;

			try {
				fw = new FileWriter(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  	   
			try {
				fw.write(toWrite.toString());
				fw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	