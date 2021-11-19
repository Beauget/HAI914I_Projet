package qengine.program;


public class Pair{
	int key;
	String value;
	
	public Pair(int k, String v) {
		this.key=k;
		this.value=v;
	}
	
	public int getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public boolean isSameValue(String value){
		return (this.value.equals(value));
	}

	 public String toString() {
		 return("Key : " + this.getKey()+ "\n" +  "Value : "+this.getValue() + "\n");
	 }
	 
}
