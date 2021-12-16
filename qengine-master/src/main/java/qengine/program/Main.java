package qengine.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import qengine.program.models.Benchmark;
import qengine.program.models.DictionaryHashMap;
import qengine.program.models.IndexOpti;
import qengine.program.models.Query;
import qengine.program.models.Select;
import qengine.program.processor.Processor;
//mvn clean compile assembly:single
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
	static String option = "";
	public static void main(String[] args) throws Exception {
		
		// user menu
		Scanner sc = new Scanner(System.in);
		System.out.println("--- Bienvenue dans notre moteur de requête RDF ---");
		while(!option.equals("q")) {
			sc = new Scanner(System.in);
			System.out.println("\n1 : Create a queryset \n2 : Launch a queryset \nq: quit");
			option = sc.next();
			if(option.equals("1")) {option="option1";}
			if(option.equals("2")) {option="option2";}
			option1();
			option2();
		}
		System.out.println("Merci de votre visite, bonne journée !");
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
	  

		
		public static HashMap<String,ArrayList<Query>> getAllTemplates() throws FileNotFoundException, IOException {
			File directory= new File(workingDir+"queryset/");
			HashMap<String,ArrayList<Query>> output = new HashMap<String,ArrayList<Query>>();
			for (File file : directory.listFiles())
			{
				if (!file.isDirectory()&&file.getName().endsWith(".queryset")) {
	  			  	queryFile = workingDir+"queryset/"+file.getName();
	  			  	ArrayList<Query> array = parseQueries();
	  			  	output.put(file.getName(), array);
	            } 

			}
			return output;
		}
		
		public static Benchmark createBenchmark(ArrayList<Processor> allProcessors,Integer numberOfTemplates){
			ArrayList<Processor> processorsUsed = new ArrayList<Processor>();
			Scanner sc = new Scanner(System.in);
			
			Integer x=0;
			while((x==0)||(x>numberOfTemplates)) {
				System.out.println("How many templates do you want to use ? :");
				sc = new Scanner(System.in);
				x= Integer.parseInt(sc.next());
			}
			System.out.println("Use the number beside the template name to refer to it.");
			for (int i = 0; i < x; i++) {
				int template = -1;
				System.out.println("Choose a number for the next template");
				while(template==-1||template>(numberOfTemplates-1)) {
					sc = new Scanner(System.in);
					template= Integer.parseInt(sc.next());
				}
				processorsUsed.add(allProcessors.get(template));
				System.out.println("Template number "+template+ " added");
			}	
			
			Integer percentageOfEmptyQueries = 100;
			Integer percentageOfDuplicates= 100;
			
			while(percentageOfEmptyQueries+percentageOfDuplicates>99) {
				x=-1;
				while(x<0||x>99) {
					System.out.println("Choose a percentage of empty query for the benchmark");
					sc = new Scanner(System.in);
					x= Integer.parseInt(sc.next());
				}
				percentageOfEmptyQueries=x;
				x=-1;
				while(x<0||x>99) {
					System.out.println("Choose a percentage of duplicates");
					sc = new Scanner(System.in);
					x= Integer.parseInt(sc.next());
				}
				percentageOfDuplicates=x;
			}
			Integer maxBenchmarkSize = ((maxNumberOfQueriesYouCanUse(processorsUsed)*100)/
					(100-(percentageOfDuplicates+percentageOfEmptyQueries)))
					*processorsUsed.size();
			x=0;
			while(x<processorsUsed.size()||x>maxBenchmarkSize) {
				System.out.println("What is the desired size of the benchmark ? (must not be over "+maxBenchmarkSize+")");
				sc = new Scanner(System.in);
				x= Integer.parseInt(sc.next());
			}
			Integer numberOfQueriesToGetFromEachTemplate = x/processorsUsed.size();
			
			ArrayList<Query> benchmark = new ArrayList<Query>();
			for(Processor p: processorsUsed) {
				ArrayList<Query> toAdd =p.doParameters(numberOfQueriesToGetFromEachTemplate,percentageOfEmptyQueries, percentageOfDuplicates);
				if(toAdd!=null) {
					benchmark.addAll(toAdd);
				}
				else {
					System.out.println("One template could not be used !");
				}
			}
			return new Benchmark(percentageOfEmptyQueries,percentageOfDuplicates,benchmark);	
		}
		
		public boolean isThereEnoughQueriesInEachTemplate(ArrayList<Processor> processors, Integer numberOfQueriesNeeded) {
			for(Processor p : processors) {
				if(p.numberOfQueries()<numberOfQueriesNeeded) {
					return false;
				}
			}
			return true;
		}
		
		public static Integer maxNumberOfQueriesYouCanUse(ArrayList<Processor> processors) {
			Integer max =-1;
			for(Processor p : processors) {
				if(p.numberOfQueries()<max||max==-1) {
					max = p.numberOfQueries();
				}
			}
			return max;
		}
		
		public static void option1() throws FileNotFoundException, IOException {
			Scanner sc = new Scanner(System.in);
			while(option.equals("option1")) {
				System.out.println("Please make sure to have all the templates you want to use in .../data/queryset/");
				System.out.println("Write the name of the rdf file you want to use (must be in .../data/rdf/) :");
		
				sc = new Scanner(System.in);
				dataFile = workingDir + "rdf/"+sc.next();
		
				System.out.println("Creating the dictionary and getting the templates....:");			
				HashMap<String,ArrayList<Query>> allQueries =  getAllTemplates();
				ArrayList<Processor> allProcessors = allTemplatesProcessor(allQueries);
				while(option.equals("option1")) {
					System.out.println("There is " + allQueries.size()+ " template(s) available :");
					
					Iterator it = allQueries.entrySet().iterator();	
					Integer x = 0;
					while (it.hasNext()) {			 
						HashMap.Entry en = (Entry) it.next();
						System.out.println(x+" :"+en.getKey());	
						x++;
					}
					Benchmark benchmark = createBenchmark(allProcessors,allQueries.size());
					if(benchmark.getQueries()!=null ) {
						benchmark.writeBenchmark(outputPath);
						System.out.println(benchmark.getName()+ " created! You can view it in .../output/");
						System.out.println("The number may be a little off because of the divisions => Real benchmark size : "+benchmark.size());
					}
					option = "nextChoice";
					while(option.equals("nextChoice")) {
						System.out.println("Do you want to quit ? y/n");
						sc = new Scanner(System.in);
						option = sc.next();
						if(option.equals("y")) {option="quit";}
						else if(option.equals("n")) {option="option1";}
						else {option="nextChoice";}
					}
				}			
			}
		}
		
		public static void option2() throws FileNotFoundException, IOException{
			while(option.equals("option2")) {
				int cmd = 999;
				StringBuilder builderBase = new StringBuilder();
				StringBuilder builder = new StringBuilder();
				StringBuilder toPath = new StringBuilder();
				Scanner sc = new Scanner(System.in);
				
				while(option.equals("option2")) {
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
							String fileName = "option_1_data_output_"+System.currentTimeMillis();
							MainRDFHandler.writeToCSV(csv, fileName);
							System.out.println(fileName+" has been created ! End of the option\n");
							break;
							
						case 2 : 
							System.out.println(" Création du dictionnaire et de l'index en cours..");
							parseData();
							System.out.println("Temps de création du dictionnaire (SANS ECRITURE) " + DictionaryHashMap.getTimeDictionnary() + " ms");
							System.out.println("Fin de l'option \n");
							break;
							
						case 3 : 
							System.out.println("Création du dictionnaire et de l'index en cours..");
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
							fileName = "option_8_data_output_"+System.currentTimeMillis();
							MainRDFHandler.writeToCSV(csv, fileName);
							System.out.println(fileName+" has been created ! End of the option\n");
							break;
						case 0 : 
							option="quit";
							break;
						default : 
							System.out.println("Mauvaise entrée clavier");
							break;
						}			
					}
				}
			}
		}

}
