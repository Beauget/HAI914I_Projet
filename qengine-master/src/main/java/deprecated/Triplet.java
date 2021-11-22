package deprecated;

// Triplet = 1,2,3 order give by index
public class Triplet {
	
	public int[] indexing;
	
	public Triplet(int s, int p, int o) {
		indexing = new int[] {s,p,o};
	}
	
	public int[] getIn() {
		return indexing;
	}
	
	
	public String toString() {
		String s =  indexing[0] + " " + indexing[1] + " " + indexing[2] + " " + "\n";
		return s;
	}

}
