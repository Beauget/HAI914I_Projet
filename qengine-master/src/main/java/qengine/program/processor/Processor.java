package qengine.program.processor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public int numberOfNoAnswer(){
		int output = 0;
		for(Query q : queries) {
			List<String> answer = doAQuery(q);
			if(answer==null) {
				output++;
			}				
		}
		return output;
	}
	
	public int numberOfQueries() {
		return this.queries.size();
	}
	
	public int numberOfDuplicates(){
		int output = 0;
		for(int i=0 ; i< queries.size()-1; i++) {
			for(int j=i+1 ; j< queries.size();j++) {
				if(queries.get(i).equals(queries.get(j))) {
					output++;
				}
			}
		}
		return output;
	}
	/*
	 * @return une hashMap contenant les queries et leur nombre d'apparition
	 */
	
	public HashMap<String,Integer> queriesDuplicatesHashMap(){
		int output = 0;
		HashMap<String,Integer> mapQueries = new HashMap<String,Integer>();
		for(Query q : queries) {
			if(mapQueries.get(q.toString())==null) {
				mapQueries.put(q.toString(), 1);
			}
			else {
				Integer v = mapQueries.get(q.toString());
				mapQueries.put(q.toString(), v+1);
				output++;
			}
		}
		return mapQueries;
	}
	
	public List<Query> queriesDuplicatesArrayList(){
		ArrayList output = new ArrayList();
		for(int i=0 ; i< queries.size()-1; i++) {
			Integer numberOfDuplicate = 0;
			for(int j=i+1 ; j< queries.size();j++) {
				if(queries.get(i).equals(queries.get(j))) {	
					numberOfDuplicate++;
					//c'est le 3+ doublon que je trouve, je l'ajoute a la liste de query à supprimer
					if(numberOfDuplicate>1) {
						output.add(queries.get(j));	
					}
				}
			}
		}
		return output;
	}
	/*
	 * @return  ArrayList<Query> queries contenant maintenant au plus deux doublons par queries
	 */
	public void cleanDuplicates2() {
		List<Query> queriesToRemove = queriesDuplicatesArrayList();
		System.out.println(queriesToRemove.toString());
		for(Query q : queriesToRemove) {
			queries.remove(q);
		}
		System.out.println(queries.toString());
	}
	
	/*
	 * @return  ArrayList<Query> queries contenant maintenant au plus deux doublons par queries
	 */
	public void cleanDuplicates() {
		HashMap<String,Integer> uncleanQueries = queriesDuplicatesHashMap();
		for (Map.Entry<String, Integer> entry : uncleanQueries.entrySet()) {
		    Integer value = entry.getValue();
		    if(value>2) {
			    String key = entry.getKey();
			    //nombre de fois qu'il doit etre enleve
			    Integer numberOfTimesToRemove = value-2;
			    //on utilise un iterrator pour iterer et remove
				for (Iterator<Query> qs = queries.iterator(); qs.hasNext();) {
					Query query = qs.next();
				    if (query.toString().equals(key)&&numberOfTimesToRemove>0) {
				    	qs.remove();
				    	numberOfTimesToRemove--;
				    }
				}
			}
		}
		System.out.println(queries.toString());
	}
	public String doQueries(){
		double start = System.currentTimeMillis();
		StringBuilder builder = new StringBuilder();
		builder.append("Query , Answer(s) \n");
		for(Query q : queries) {
			List<String> answer = doAQuery(q);
			if(answer!=null) {
				if(answer.size()!=0) {
					builder.append(q.getRealQuery()+" ,");
					for(String s :answer) {
					builder.append(s.toString()+" ");
					}
					builder.append("\n");
				}
			}				
		}
		double end = System.currentTimeMillis();
		execQuery += ((end - start) / 1000);
		return builder.toString();
	}
	
	public double getExecQuery() {
		return execQuery;
	}
	
	public  double getExecQueryWrite() {
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
		String pathToFile = path + "export_query_results" + ".csv";
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
