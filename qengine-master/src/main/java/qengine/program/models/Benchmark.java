package qengine.program.models;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Benchmark {
	Integer percentageOfEmptyQueries;
	Integer percentageOfDuplicates;
	ArrayList<Query> queries;
	long id;
	
	public Benchmark(Integer empty, Integer duplicates, ArrayList<Query> q) {
		percentageOfEmptyQueries = empty;
		percentageOfDuplicates = duplicates;
		id = System.currentTimeMillis();
		queries = q;
	}
	
	public ArrayList<Query> getQueries(){return this.queries;}
	
	public Integer size() {return this.queries.size();}
	public String getName(){
		return ("Benchmark_emp_"+percentageOfEmptyQueries+"_dup_"+percentageOfDuplicates+"_"+id);
	}
	
	public void writeBenchmark(String path) {
		StringBuilder builder = new StringBuilder();
		path = path+this.getName()+".queryset";
		for(Query q :queries) {			
			builder.append("\n\n"+q.toString());
		}
		FileWriter fw = null;

			try {
				fw = new FileWriter(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  	   
			try {
				fw.write(builder.toString());
				fw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

}
