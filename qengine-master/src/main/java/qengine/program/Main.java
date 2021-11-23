package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	static final String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
	//static final String queryFile = workingDir + "sample_query.queryset";
	static final String queryFile = workingDir + "STAR_ALL_workload.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
	//static final String dataFile = workingDir + "sample_data.nt";
	static final String dataFile = workingDir + "100K.nt";
	// ========================================================================
	
	//static final String outputPath = "/home/dnspc/Desktop/M2/NoSQL/Projet/HAI914I_Projet/qengine-master/output/";
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
		
		toPath.append("Veuillez entrer le path output pour les r�sultats de l'application (output/ par d�faut �crire \"defaut\")");
		System.out.println(toPath.toString());
		Scanner sc = new Scanner(System.in);
		String toChange = sc.next();
		if(!(toChange.equals("defaut"))) {
			outputPath = toChange;
		}
		System.out.println(outputPath);
		builderBase.append("--- Bienvenue dans notre moteur de requête RDF --- \n");
		System.out.println(builderBase.toString());
		builder.append("Options disponible (taper le chiffre correspondant à l'option) : \n" );
		builder.append("\n1 : Création du .csv contenant tout les résultats de l'application");
		builder.append("\n2 : Création du dictionnaire et temps d'éxecution (SANS ECRITURE /output)");
		builder.append("\n3 : Création du dictionnaire et temps d'éxecution (AVEC ECRITURE /output)");
		builder.append("\n4 : Création des indexs et temps d'exécution (SANS ECRITURE /output)");
		builder.append("\n5 : Création des indexs et temps d'exécution (AVEC ECRITURE /output)");
		builder.append("\n6 : Chargement + exécution des requêtes et temps d'exécution (SANS ECRITURE /output)");
		builder.append("\n7 : Chargement + exécution des requêtes et temps d'exécution (AVEC ECRITURE /output)");
		builder.append("\n8 : Toute les données des options précédentes (SANS ECRITURE)");
		builder.append("\n0 : Quittez l'application");
		while(cmd != 0) {
			System.out.println(builder.toString());
			cmd = sc.nextInt();
			
			switch(cmd) {
			case 1 : 
				System.out.println("generation du csv ...");
				ArrayList<String> csv = new ArrayList<String>();
				double startCSV = System.currentTimeMillis();
				parseData();
				ArrayList<Query> queriesCSV = parseQueries();
				Processor processorCSV = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queriesCSV);
				processorCSV.doQueries();
				double endCSV = System.currentTimeMillis();
				double totalTimeCSV = (endCSV - startCSV);
				csv.add(dataFile);
				csv.add(queryFile);
				csv.add(String.valueOf(MainRDFHandler.nbTriplet));
				csv.add(String.valueOf(queriesCSV.size()));
				csv.add("0");
				csv.add("0");
				csv.add(String.valueOf(DictionaryHashMap.getTimeDictionnary()));
				csv.add("6");
				csv.add(String.valueOf(IndexOpti.getExecIndex()));
				csv.add(String.valueOf(totalTimeCSV));
				csv.add("0");
				MainRDFHandler.writeToCSV(csv);
				break;
			case 2 : 
				System.out.println("dictionnaire en cours...");
				parseData();
				System.out.println("Temps de création du dictionnaire (SANS ECRITURE) " + DictionaryHashMap.getTimeDictionnary() + " ms");
				break;
			case 3 : 
				System.out.println("dictionnaire ecriture en cours..");
				parseData();
				double start = System.currentTimeMillis();
				MainRDFHandler.writeDictionnary();
				double end = System.currentTimeMillis();
				double writeTime = DictionaryHashMap.getTimeDictionnary() + (end - start);
				System.out.println("Temps de création du dictionnaire (AVEC ECRITURE dans /ouput " + writeTime + " ms");
				break;
			case 4 : 
				System.out.println("dictionnaire, index ecriture en cours..");
				parseData();
				System.out.println("Temps de création des 6 index (SANS ECRITURE et sans prise en compte du temps de création du dictionnaire) " + IndexOpti.getExecIndex() + " ms");
				break;
			case 5 : 
				System.out.println("dictionnaire,index ecriture en cours..");
				parseData();
				System.out.println("écriture en cours");
				double startIndex = System.currentTimeMillis();
				MainRDFHandler.writeIndex();
				double endIndex = System.currentTimeMillis();
				double writeTimeIndex = IndexOpti.getExecIndex() + (endIndex - startIndex );
				System.out.println("Temps de création des 6 index (AVEC ECRITURE dans /output et \n sans prise en compte du temps de création du dictionnaire) " + writeTimeIndex + " ms \n");
				break;
			case 6 : 
				System.out.println("dictionnaire,index ecriture en cours..");
				parseData();
				System.out.println("requête en cours");
				ArrayList<Query> queries = parseQueries();
				Processor processor = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries);
				processor.doQueries();
				System.out.print("Temps de création et d'exécution des requêtes (SANS ECRITURE et \n sans prise en compte de la création du dictionnaire et des index " + processor.getExecQuery() + " ms \n");
				break;
			case 7 : 
				System.out.println("dictionnaire,index ecriture en cours..");
				parseData();
				System.out.println("requête + écriture en cours");
				System.out.println("requête en cours");
				ArrayList<Query> queries2 = parseQueries();
				Processor processor2 = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries2);
				processor2.writeAnswers(outputPath);
				System.out.println("Temps de création et d'exécution des requêtes (AVEC ECRITURE et \n sans prise en compte de la création du dictionnaire et des index " + processor2.getExecQueryWrite() + " ms \n");
				break;
			case 8 : 
				StringBuilder allToSee = new StringBuilder();
				double startAll = System.currentTimeMillis();
				parseData();
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
				
				break;
			case 0 : 
				System.out.println("Merci de votre visite, bonne journée !");
			default : 
				System.out.println("Mauvaise entrée clavier");
				}			
		}
		
		/*
		
		parseData();
		System.out.println("Execution de parseData()...");
		System.out.println("D�but �criture dans le dossier /output des r�sultats...");

		//MainRDFHandler.writeIndex();
		
		
		System.out.println("Dictionnaire et Index �crit dans le dossier /output");
		
		System.out.println("Execution de parseQueries()...");
		ArrayList<Query> queries = parseQueries();
		System.out.println("Création de Processor...");
		Processor processor = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries);
		
		System.out.println("Traitement des query et écriture...");
		//String outputPath = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/";
		processor.writeAnswers(outputPath);
		
		System.out.println("Fini !!! ");

		*/


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
		/*
		System.out.println("first pattern : " + patterns.get(0));
		System.out.println("object of the first pattern : " + patterns.get(0).getObjectVar().getValue());

		System.out.println("variables to project : ");
		//Utilisation d'une classe anonyme
		query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {

			public void meet(Projection projection) {
				System.out.println(projection.getProjectionElemList().getElements());
			}
		});*/
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
}
