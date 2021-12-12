package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

import deprecated.Triplet;
import qengine.program.abstract_models.Dictionary;
import qengine.program.abstract_models.Index;
import qengine.program.models.DictionaryHashMap;
import qengine.program.models.IndexOpti;
import qengine.program.models.Query;
import qengine.program.models.Select;
import qengine.program.processor.Processor;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique,
 * à vous de programmer les entrées par passage d'arguments en ligne de commande comme demandé dans l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les triples et requêtes
 * depuis les fichiers ; ce sera à vous d'adapter/réécrire le code pour finalement utiliser les requêtes et interroger les données.
 * On ne s'attend pas forcémment à ce que vous gardiez la même structure de code, vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */

final class Main {
	static final String baseURI = null;

	/**
	 * Votre répertoire de travail où vont se trouver les fichiers à lire
	 */
	static String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
	//static String queryFile = workingDir + "STAR_ALL_workload.queryset";
	//static String queryFile = workingDir + "/1000/STAR_ALL_workload_1000.queryset";
	static String queryFile = workingDir + "/100/STAR_ALL_workload_100.queryset";
	//static String queryFile = workingDir + "/10000/STAR_ALL_workload_10000.queryset";
	//static String queryFile = workingDir + "test.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
	//static final String dataFile = workingDir + "sample_data.nt";
	//static String dataFile = workingDir + "500K.rdf";
	static String dataFile = workingDir + "100K.nt";
	// ========================================================================
	
	static String outputPath = "output/";
	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {
		
		// user menu
		
		
		int cmd = 999;
		StringBuilder builderBase = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		StringBuilder toPath = new StringBuilder();
		
		System.out.println("Veuillez entrer le path du fichier contenant les requêtes sparql(si vous souhaitez tester le code avec "+
				"STAR_ALL_workload.queryset et 100K.nt écrire \"defaut\")");

		Scanner sc = new Scanner(System.in);
			String firstEntry = sc.next();
		if(!(firstEntry.equals("defaut"))) {
			dataFile = firstEntry;
			System.out.println("Veuillez entrer le path du fichier contenant des données rdf :");

			sc = new Scanner(System.in);
			queryFile = sc.next();
			System.out.println("Veuillez entrer le path du document contenant le chemin vers le output :");

			sc = new Scanner(System.in);
			outputPath = sc.next();
		}
		HashMap<String,ArrayList<Query>> allQueries =  getAllTemplates("100");
		ArrayList<Processor> allProcessors = allTemplatesProcessor(allQueries);
		System.out.println(allProcessors.get(0).numberOfQueries());
		ArrayList<Query> benchmark = allProcessors.get(0).doParameters(30,10, 30);
		if(benchmark!=null)
			System.out.println(benchmark.size());
		/*
		 * 		for(Query q :queries) {
			builder.append("\n\n"+q.toString());
		}
		 */
		
		/* Utiliser pour append le contenus des template dans un fichier*/
		
		//appendToFileJava11("100");
		//appendToFileJava11("1000");
		//appendToFileJava11("10000");
		
		builderBase.append("--- Bienvenue dans notre moteur de requête RDF --- \n");
		System.out.println(builderBase.toString());
		builder.append("Options disponible (taper le chiffre correspondant à l'option) : \n" );
		builder.append("\n1 : Création du dictionnaire, index et réponses aux requêtes et du .csv contenant les temps de calculs et des informations sur les fichier");
		builder.append("\n2 : Création du dictionnaire et temps d'exécution (SANS ECRITURE /output)");
		builder.append("\n3 : Création du dictionnaire et temps d'exécution (AVEC ECRITURE /output)");
		builder.append("\n4 : Création des indexs et temps d'exécution (SANS ECRITURE /output)");
		builder.append("\n5 : Création des indexs et temps d'exécution (AVEC ECRITURE /output)");
		builder.append("\n6 : Chargement + exécution des requêtes et temps d'exécution (SANS ECRITURE /output)");
		builder.append("\n7 : Chargement + exécution des requêtes et temps d'exécution (AVEC ECRITURE /output)");
		builder.append("\n8 : Toute les données des options précédentes (SANS ECRITURE)");
		builder.append("\n9 : Toute les données des options précédentes (AVEC ECRITURE)");
		builder.append("\n0 : Quittez l'application");
		while(cmd != 0) {
			System.out.println(builder.toString());
			cmd = sc.nextInt();
			
			switch(cmd) {
			case 1 : 
				System.out.println("Traitement en cours ...");
				ArrayList<String> csv = new ArrayList<String>();
				double startCSV = System.currentTimeMillis();
				parseData();
				double startq = System.currentTimeMillis();
				ArrayList<Query> queriesCSV = parseQueries();
				double endq = System.currentTimeMillis();
				Processor processorCSV = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queriesCSV);
				processorCSV.doQueries();
				double endCSV = System.currentTimeMillis();
				double totalTimeCSV = (endCSV - startCSV);
				System.out.println("Génération du csv ...");
				csv.add(dataFile);
				csv.add(queryFile);
				csv.add(String.valueOf(MainRDFHandler.nbTriplet));
				csv.add(String.valueOf(queriesCSV.size()));
				csv.add("NON_DISPONIBLE");
				csv.add(String.valueOf(endq - startq));
				csv.add(String.valueOf(DictionaryHashMap.getTimeDictionnary()));
				csv.add("6");
				csv.add(String.valueOf(IndexOpti.getExecIndex()));
				csv.add(String.valueOf(totalTimeCSV));
				double endCSV2 = System.currentTimeMillis();
				totalTimeCSV = (endCSV2 - startCSV);
				csv.add(String.valueOf(totalTimeCSV));
				MainRDFHandler.writeToCSV(csv, "option_1_data_output");
				
				processorCSV.cleanQueriesWithCommentary(10);
				
				System.out.println("Fin de l'option \n");
				break;
				
			case 2 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Temps de création du dictionnaire (SANS ECRITURE) " + DictionaryHashMap.getTimeDictionnary() + " ms");
				System.out.println("Fin de l'option \n");
				break;
				
			case 3 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				double start = System.currentTimeMillis();
				MainRDFHandler.writeDictionnary();
				double end = System.currentTimeMillis();
				double writeTime = DictionaryHashMap.getTimeDictionnary() + (end - start);
				System.out.println("Temps de création du dictionnaire (AVEC ECRITURE dans /ouput " + writeTime + " ms");
				System.out.println("Fin de l'option \n");
				break;
				
			case 4 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Temps de création des 6 index (SANS ECRITURE et sans prise en compte du temps de création du dictionnaire) " + IndexOpti.getExecIndex() + " ms");
				break;
				
			case 5 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Écritures des données");
				double startIndex = System.currentTimeMillis();
				MainRDFHandler.writeIndex();
				double endIndex = System.currentTimeMillis();
				double writeTimeIndex = IndexOpti.getExecIndex() + (endIndex - startIndex );
				System.out.println("Temps de création des 6 index (AVEC ECRITURE dans /output et \n sans prise en compte du temps de création du dictionnaire) " + writeTimeIndex + " ms \n");
				System.out.println("Fin de l'option \n");
				break;
			case 6 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Requête(s) en cours");
				ArrayList<Query> queries = parseQueries();
				Processor processor = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries);
				processor.doQueries();
				System.out.print("Temps de création et d'exécution des requêtes (SANS ECRITURE et \n sans prise en compte de la création du dictionnaire et des index " + processor.getExecQuery() + " ms \n");
				System.out.println("Fin de l'option \n");
				break;
			case 7 : 
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Requête(s) en cours");
				ArrayList<Query> queries2 = parseQueries();
				Processor processor2 = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries2);
				processor2.writeAnswers(outputPath);
				System.out.println("Temps de création et d'exécution des requêtes (AVEC ECRITURE et \n sans prise en compte de la création du dictionnaire et des index " + processor2.getExecQueryWrite() + " ms \n");
				System.out.println("Fin de l'option \n");
				break;
			case 8 : 
				StringBuilder allToSee = new StringBuilder();
				double startAll = System.currentTimeMillis();
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Requête(s) en cours");
				ArrayList<Query> queriesAll = parseQueries();
				Processor processorAll = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queriesAll);
				processorAll.doQueries();
				double endAll = System.currentTimeMillis();
				allToSee.append("Temps de création du dictionnaire (SANS ECRITURE) " + DictionaryHashMap.getTimeDictionnary() + " ms \n");
				allToSee.append("Temps de création des index (SANS ECRITURE) " + IndexOpti.getExecIndex() + " ms \n");
				allToSee.append("Temps de création et d'exécution des requêtes (SANS ECRITURE) " + processorAll.getExecQuery() + " ms \n");
				double totalTime = (endAll - startAll);
				
				allToSee.append("Temps d'exécution total de l'application : " + totalTime + " ms");				
				System.out.println(allToSee.toString());
				System.out.println("Fin de l'option \n");
				break;
			case 9 : 
				double startWrite = System.currentTimeMillis();
				csv = new ArrayList<String>();
				System.out.println(" Création du dictionnaire et de l'index en cours..");
				parseData();
				System.out.println("Requête(s) en cours");
				startq = System.currentTimeMillis();
				ArrayList<Query> queriesWrite = parseQueries();
				endq = System.currentTimeMillis();
				Processor processorWrite = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queriesWrite);
				processorWrite.doQueries();				
				double endWrite = System.currentTimeMillis();
				double totalTimeWrite = (endWrite - startWrite);
				System.out.println("Écritures des données Dictionnaire, Index et Query..");
				
				MainRDFHandler.writeDictionnary();
				MainRDFHandler.writeIndex();
				processorWrite.writeAnswers(outputPath);

				System.out.println("Écritures du csv..");
				csv.add(dataFile);
				csv.add(queryFile);
				csv.add(String.valueOf(MainRDFHandler.nbTriplet));
				csv.add(String.valueOf(queriesWrite.size()));
				csv.add("NON_DISPONIBLE");
				csv.add(String.valueOf(endq - startq));
				csv.add(String.valueOf(DictionaryHashMap.getTimeDictionnary()));
				csv.add("6");
				csv.add(String.valueOf(IndexOpti.getExecIndex()));
				csv.add(String.valueOf(totalTimeWrite));
				double endWrite2 = System.currentTimeMillis();
				totalTimeCSV = (endWrite2 - startWrite);
				csv.add(String.valueOf(totalTimeCSV));
				MainRDFHandler.writeToCSV(csv, "option_8_data_output");
				System.out.println("Fin de l'option \n");
				break;
			case 0 : 
				System.out.println("Merci de votre visite, bonne journée !");
				break;
			default : 
				System.out.println("Mauvaise entrée clavier");
				break;
				}			
		}
//mvn clean compile assembly:single
	}
	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static Query processAQuery(ParsedQuery query, Query output) {
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
		
		for(StatementPattern p : patterns) {
			ArrayList<String> pattern = new ArrayList<String>();
			for(Var var :p.getVarList()) {
				if(var.getValue()==null)
					pattern.add("?");
				else
					pattern.add(var.getValue().toString());
			}
			output.getQuery().add(new Select(pattern.get(0),pattern.get(1),pattern.get(2)));
		}
		return output;
	}
	
	// ========================================================================

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
	ArrayList<String> s ;
	private static ArrayList<Query> parseQueries() throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
		 * entièrement dans une collection.
		 */
		ArrayList<Query> queries = new ArrayList<Query>();
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
			 * On considère alors que c'est la fin d'une requête
			 */
			{
				String line = lineIterator.next();
				queryString.append(line);

				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
					Query queryObject = new Query(queryString.toString().trim().replace("\t", ""));
					queries.add(processAQuery(query,queryObject)); // Traitement de la requête, à adapter/réécrire pour votre programme
					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
		}
		return queries;
	}

	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	private static void parseData() throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
			
			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(new MainRDFHandler());

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
			
		
			
			
		}
	}
	//Fonction pour append les template efficacement
	// Java 11, writeString, append mode
	  private static void appendToFileJava11(String number)
				throws IOException {
		  String[] fileList = {
				  "Q_1_eligibleregion_",
				  "Q_1_includes_",
				  "Q_1_likes_",
				  "Q_1_nationality_",
				  "Q_1_subscribes_",
				  "Q_2_includes_eligibleRegion_",
				  "Q_2_likes_nationality_",
				  "Q_2_subscribes_likes_",
				  "Q_2_tag_homepage_",
				  "Q_3_location_gender_type_",
				  "Q_3_location_nationality_gender_",
				  "Q_3_nationality_gender_type_",
				  "Q_4_location_nationality_gender_type_"
				  };
		  Path pathToAdd = Path.of(workingDir +number +"/STAR_ALL_workload_"+number+".queryset");
		  for(String name : fileList) {
			  Path fileName = Path.of(workingDir +number +"/"+name+number+".queryset");
			  
		      // default StandardCharsets.UTF_8
			  String content = Files.readString(fileName);
		      Files.writeString(pathToAdd, content,StandardCharsets.UTF_8,
		              StandardOpenOption.CREATE,
		              StandardOpenOption.APPEND);
		  }
		  

	  }
	  
	  private static HashMap<String,ArrayList<Query>> getAllTemplates(String number) throws FileNotFoundException, IOException{
		  HashMap<String,ArrayList<Query>> output = new HashMap<String,ArrayList<Query>>();
		  String[] fileList = {
				  "Q_1_eligibleregion_",
				  "Q_1_includes_",
				  "Q_1_likes_",
				  "Q_1_nationality_",
				  "Q_1_subscribes_",
				  "Q_2_includes_eligibleRegion_",
				  "Q_2_likes_nationality_",
				  "Q_2_subscribes_likes_",
				  "Q_2_tag_homepage_",
				  "Q_3_location_gender_type_",
				  "Q_3_location_nationality_gender_",
				  "Q_3_nationality_gender_type_",
				  "Q_4_location_nationality_gender_type_"
				  };
		  Path pathToAdd = Path.of(workingDir +number +"/STAR_ALL_workload_"+number+".queryset");
		  for(String name : fileList) {
			  queryFile = workingDir +number +"/"+name+number+".queryset";
			  ArrayList<Query> array = parseQueries();
			  output.put(name+number, array);
		  }
		  return output;
	  }
	  
	  /*
	   * @return Renvoie une array de processor qui sont clean ;
	  */
	  
	  private static ArrayList<Processor> allTemplatesProcessor(HashMap<String,ArrayList<Query>> hashmap) throws FileNotFoundException, IOException{
		  ArrayList<Processor> output = new ArrayList<Processor>();
		  parseData();
			 Iterator it = hashmap.entrySet().iterator();
			 
			 while (it.hasNext()) {
				 
				 HashMap.Entry entry = (Entry) it.next();
				 //String key =  (String) ((Entry) hashmap).getKey();
				 ArrayList<Query> value  = (ArrayList<Query>) entry.getValue();
				 
				 Processor p = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), value);
				 p.cleanQueries();
				 output.add(p);
			 }
		  return output;
	  }

}
