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

import qengine.program.abstract_models.Dictionary;
import qengine.program.abstract_models.Index;
import qengine.program.models.Query;
import qengine.program.models.Triplet;
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

	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {
		parseData();
		System.out.println("Execution de parseData()...");
		System.out.println("D�but �criture dans le dossier /output des r�sultats...");

		MainRDFHandler.writeIndex();
		MainRDFHandler.writeDictionnary();
		
		System.out.println("Dictionnaire et Index �crit dans le dossier /output");
		
		System.out.println("Execution de parseQueries()...");
		ArrayList<Query> queries = parseQueries();
		System.out.println("Création de Processor...");
		Processor processor = new Processor(MainRDFHandler.dictionary,MainRDFHandler.indexesToArray(), queries);
		
		System.out.println("Traitement des query et écriture...");
		String outputPath = "/home/hayaat/Desktop/Master/M2/Git/HAI914I_Projet/qengine-master/output/";
		processor.writeAnswers(outputPath);
		
		System.out.println("Fini !!! ");

		


	}
	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static Query processAQuery(ParsedQuery query) {
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
	
		
		ArrayList<String> output = new ArrayList<String>();
		for(Var var : patterns.get(0).getVarList()) {
			if(var.getValue()==null)
				output.add("?");
			else
				output.add(var.getValue().toString());
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
		
		return new Query(output.get(0),output.get(1),output.get(2));
	}

	// ========================================================================

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
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

					queries.add(processAQuery(query)); // Traitement de la requête, à adapter/réécrire pour votre programme
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
