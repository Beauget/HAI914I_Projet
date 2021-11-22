package qengine.program.processor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import arq.query;
import qengine.program.abstract_models.Dictionary;
import qengine.program.abstract_models.Index;
import qengine.program.models.IndexOpti;
import qengine.program.models.Query;


public class Processor {
	static Dictionary dictionary;
	static ArrayList<Index> indexes;
	static ArrayList<Query> queries;
	
	static double execQuery = 0;
	static double execQueryWrite = 0;
	
	public Processor(Dictionary d, ArrayList<Index> idx, ArrayList<Query> q){
		this.dictionary = d;
		this.indexes = idx;
		this.queries = q;
	}
	public String doQueries(){
		double start = System.currentTimeMillis();
		StringBuilder builder = new StringBuilder();
		for(Query q : queries) {
			builder.append("QUERY : "+q.toString()+"\n");
			List<String>  answer = doAQuery(q);
			if(answer!=null) {
				builder.append("Answer(s) :"+"\n"+answer.toString()+"\n\n");
			}
			else {
				builder.append("Combinaison impossible"+"\n\n");
			}
				
		}
		double end = System.currentTimeMillis();
		execQuery += ((end - start) / 1000);
		return builder.toString();
	}
	
	public static double getExecQuery() {
		return execQuery;
	}
	
	public static double getExecQueryWrite() {
		return execQueryWrite;
	}
	
	private static List<String> doAQuery(Query query) {
		List<Integer> answerInIntegers = doQueryInIntegers(query);
		if(answerInIntegers!=null)
			return getAnswersInString(answerInIntegers);
		return null;
	}
	
	private static List<Integer> doQueryInIntegers(Query q) {
		ArrayList<Integer> queryInInteger = queryToInteger(q);
		List<Integer> output= new ArrayList<Integer>();
		if(!queryInInteger.contains(-1)) {
			//on enleve les variables qu'on a pas
			queryInInteger.remove(0);
			for(Index index : indexes) {
				List<Integer> answer = index.getAnswer(queryInInteger.get(0), queryInInteger.get(1));
				if(answer!=null) {
					return answer;
				}
			}
			//System.out.println("Processor : doQueryInIntegers : toute les variables existe mais combinaison impossible"+"\n");
		}
		else {
			//System.out.println(q.toString());
			//System.out.println("Processor : doQueryInIntegers : Cette requete comporte des objets qui n'existe pas dans le dictionnaire"+"\n");
			}
		return null;
	}
	
	private static ArrayList<Integer> queryToInteger(Query query){
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(String s : query.getValues()) {
			if(s.equals("?")) {
				output.add(0);
				}
			else {
				Integer key = dictionary.getKey(s);
				if(key!=null) {
					output.add(key);
				}
				else //l'objet n'existe pas dans le dictionnaire
					output.add(-1);
			}
		}		
		return output;
	}
	
	private static List<String> getAnswersInString(List<Integer> answers ){
		List<String> output = new ArrayList<String>();
		for(Integer i :answers) {
			String answer = dictionary.getValue(i);
			if (answer!=null)
				output.add(answer);
		}
		return output;
	}
	
	public void writeAnswers(String path) throws IOException {
		double start = System.currentTimeMillis();
		String pathToFile = path + "Answers" + ".txt";
		FileWriter fw = null;
		try {
			fw = new FileWriter(pathToFile);
		} catch (IOException e1) {
				e1.printStackTrace();
			}  	   
			try {
				fw.write(doQueries());
				fw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		double end = System.currentTimeMillis();
		execQueryWrite += ((end - start) / 1000);
		}
	
}
