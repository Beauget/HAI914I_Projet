package qengine.program.models;

import java.util.ArrayList;

public class Select {
	ArrayList<String> select = new ArrayList<String>();
	ArrayList<Integer> answer = new ArrayList<Integer>();
	
	public Select(String s1, String s2, String s3) {
		select.add(s1);
		select.add(s2);
		select.add(s3);
	}
	
	
	public ArrayList<String> getSelect() {
		return this.select;
	}

	public String getSubject() {
		return select.get(0);
	}

	public String getPredicate() {
		return select.get(1);
	}


	public String getObject() {
		return select.get(2);
	}
	

	public ArrayList<Integer> getAnswer() {
		return answer;
	}
	
	public void setAnswer(ArrayList<Integer> answer) {
		this.answer = answer;
	}
	
	public boolean equals(Select s) {
		boolean output = this.toString().equals(s.toString());
		return output;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String s : select) {
			if(s.equals("?")) {
				builder.append("?v0 ");
			}
			else {
				builder.append("<"+s+"> ");
			}
		}
		builder.append(". ");
		return builder.toString();
	}


}
