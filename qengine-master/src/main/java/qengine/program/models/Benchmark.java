package qengine.program.models;

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

}
