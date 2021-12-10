package qengine.program;

import java.io.File;
import qengine.program.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import qengine.program.processor.*;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;

public class JenaSolver {
	static final String baseURI = null;

	/**
	 * Votre rÃ©pertoire de travail oÃ¹ vont se trouver les fichiers Ã  lire
	 */
	static String workingDir = "data/";
	/**
	 * Fichier contenant les requÃªtes sparql
	 */
	//static final String queryFile = workingDir + "sample_query.queryset";
	static String queryFile = workingDir + "STAR_ALL_workload.queryset";
	
	static long executionTime;
	
	
	public static void main(String[] args) throws IOException {

		Model model = ModelFactory.createDefaultModel();
		String pathToOntology = "data/100K.nt";
		InputStream in = FileManager.get().open(pathToOntology);
		model.read(in, null,"NT");
		
		parseQueries(model);
		System.out.println("Temps total d'exécution requête Jena : " + executionTime);

	}
		  
	  
	static void DoQuery(Model m, String q) {

			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.create(query, m);
			//Décommentez pour voir les réponses des requêtes
		/*	try {

				ResultSet rs = qexec.execSelect();

				ResultSetFormatter.out(System.out, rs, query);

			} finally {

				qexec.close();
			}*/
		
		}
	
	
	private static void parseQueries(Model m) throws FileNotFoundException, IOException {
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			while (lineIterator.hasNext())
			{
				String line = lineIterator.next();				
				queryString.append(line);
				
				if (line.trim().endsWith("}")) {
					long start = System.currentTimeMillis();
					DoQuery(m,queryString.toString());
					long end = System.currentTimeMillis();
					executionTime = executionTime + (end - start);
					queryString.setLength(0);				
				}
				
			}
		}
	}

}
