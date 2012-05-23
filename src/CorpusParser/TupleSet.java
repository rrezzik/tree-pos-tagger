package CorpusParser;



import java.util.ArrayList;
import java.util.Set;


public class TupleSet {
	private ArrayList<ArrayList<ArrayList<String>>> tupleset;
	private Set<String> tagset;
	private Lexicon lexicon;
	
	public TupleSet(ArrayList<ArrayList<ArrayList<String>>> tuples, Set<String> tag_set, Lexicon lexicon) {
		this.tupleset = tuples;
		this.tagset = tag_set;
		this.lexicon = lexicon;
	}

	public ArrayList<ArrayList<ArrayList<String>>> getDataSet() {
		return tupleset;
	}
	
	public Set<String> getTagSet() {
		return tagset;
	}

	public Lexicon getLexicon() {
		return lexicon;
	}
	

}
