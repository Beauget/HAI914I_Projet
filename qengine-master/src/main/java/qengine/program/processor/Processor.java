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
import qengine.program.models.Select;


public class Processor {
	  Dictionary dictionary;
	  ArrayList<Index> indexes;
	  ArrayList<Query> queries;
	
	  double execQuery = 0;
	  double execQueryWrite = 0;
	
	public Processor(Dictionary d, ArrayList<Index> idx, ArrayList<Query> q){
		this.dictionary = d;
		this.indexes = idx;
		this.queries = q;
	}
	public String doQueries(){
		double start = System.currentTimeMillis();
		StringBuilder builder = new StringBuilder();
		//builder.append("Query , ")
		for(Query q : queries) {
			List<String> answer = doAQuery(q);
			if(answer!=null) {
				builder.append(q.getRealQuery()+"\n");
				for(String s :answer) {
				builder.append(s.toString()+"\n");
				}
				builder.append("\n");
			}
			else {
				//builder.append("Combinaison impossible"+"\n\n");
			}
				
		}
		double end = System.currentTimeMillis();
		execQuery += ((end - start) / 1000);
		return builder.toString();
	}
	
	public   double getExecQuery() {
		return execQuery;
	}
	
	public   double getExecQueryWrite() {
		return execQueryWrite;
	}
	/*
	 * @return la réponse à la requete en String
	 */
	
	private   List<String> doAQuery(Query q) {
		List<Integer> answerInIntegers = intersectionAnswers(q);
		if(answerInIntegers!=null)
			return getAnswersInString(answerInIntegers);
		return null;
	}
	/*
	 * @return l'intersection des différente réponse en integer
	 */
	private   List<Integer> intersectionAnswers(Query q){
		List<List<Integer>> allAnswers = doQueriesInIntegers(q);
		List<Integer> output =null;
		if( allAnswers !=null){
			output = new ArrayList<Integer>(allAnswers.get(0));
			if(output!=null) {
				for (int i = 1; i < allAnswers.size(); i++) {
					List<Integer> l1 = allAnswers.get(i);
					if(l1 !=null) {
						List<Integer> l2 = intersection(l1,output);
						output = l2;
					}
					else 
						return null;
				}
			}
			else 
				return null;
		}

		return output;
		
	}
	
	/*
	 * @return les réponse de chaque triplet en Integer
	 */
	private   List<List<Integer>> doQueriesInIntegers(Query q) {
		List<List<Integer>> queriesInInteger = queriesToInteger(q);
		List<List<Integer>> output = new ArrayList<List<Integer>>();
		Integer idx= 0;
		for(List<Integer> queryInInteger : queriesInInteger) {
			if(!queryInInteger.contains(-1)) {
				//on enleve les variables qu'on a pas
				List<Integer> temp = new ArrayList<Integer>(queryInInteger);
				temp.remove(0);
				for(Index index : indexes) {
					List<Integer> answer = index.getAnswer(temp.get(0), temp.get(1));
					if(answer!=null) {
						output.add(answer);
					}
				}
			}
			else
				return null;
		}
		//On a tr
		if(queriesInInteger.size()!=output.size())
			return null;
		return output;
	}
	
	private   List<List<Integer>> queriesToInteger(Query query){
		List<List<Integer>> output = new ArrayList<List<Integer>>();
		for(Select select : query.getQuery()) {
			List<Integer> toAdd = new ArrayList<Integer>();
			for(String s : select.getSelect()) {
				if(s.equals("?")) {
					toAdd.add(0);
					}
				else {
					Integer key = dictionary.getKey(s);
					if(key!=null) {
						toAdd.add(key);
					}
					else //l'objet n'existe pas dans le dictionnaire
						toAdd.add(-1);
				}
			}
			output.add(toAdd);
		}	
		return output;
	}
	
	private   List<String> getAnswersInString(List<Integer> answers ){
		List<String> output = new ArrayList<String>();
		for(Integer i :answers) {
			String answer = dictionary.getValue(i);
			if (answer!=null)
				output.add(answer);
		}
		return output;
	}
    public   <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
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
		execQueryWrite += (end - start);
		}
	
}
