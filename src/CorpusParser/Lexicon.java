package CorpusParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Lexicon {

	private HashMap<String, HashMap<String, Double>> lexicon;
	private HashMap<String, Double> occurence_map;

	public Lexicon() {
		occurence_map = new HashMap<String, Double>();
		lexicon = new HashMap<String, HashMap<String, Double>>();
	}

	public HashMap<String, Double> getTagsOf(String word) {
		return lexicon.get(word);
	}

	public double getOccurence(String word) {
		return occurence_map.get(word);
	}

	public boolean isTag(String word, String tag) {
		for (Entry<String, Double> entry : getTagsOf(word).entrySet()) {

		}
		if (lexicon.get(word).containsKey(tag))
			return true;
		else
			return false;
	}
	public void add(String word, String tag) {
		// is it already in the occurence map?
		if (occurence_map.containsKey(word)) {
			// increment the occurence
			double curr_value = occurence_map.get(word);
			occurence_map.put(word, curr_value + 1);
		}
		else 
			occurence_map.put(word, (double) 1);


		// is the word already here? if so updating has to be done
		if (lexicon.containsKey(word)) {
			// is this tag already in there? if so update its probability
			if (lexicon.get(word).containsKey(tag)) {
				double curr_prob = lexicon.get(word).get(tag);
				lexicon.get(word).put(tag, curr_prob + 1);
			}
			else
				lexicon.get(word).put(tag, (double) 1);
		}
		else {
			HashMap<String, Double> initial_map = new HashMap<String, Double>();
			initial_map.put(tag, (double) 1);
			lexicon.put(word, initial_map);
		}

	}

	public void cleanUp() {
		Collection<HashMap<String, Double>> values = lexicon.values();
		Iterator<HashMap<String, Double>> it = values.iterator();

		while (it.hasNext()) {

			HashMap<String, Double> map = it.next();
			Iterator<String> keys = map.keySet().iterator();

			while(keys.hasNext()) {
				if (map.get(keys.next()) < 4) {
					if (map.keySet().size() > 1)
						keys.remove();
				}
			}



		}
		// cleaning
		lexicon.remove("to");
		HashMap<String, Double> tomap = new HashMap<String, Double>();
		tomap.put("TO", (double) 50);
		lexicon.put("to", tomap);
		
		lexicon.remove("will");
		HashMap<String, Double> tomap2 = new HashMap<String, Double>();
		tomap2.put("MOD", (double) 1000);
		lexicon.put("will", tomap2);
		
		lexicon.remove("can");
		HashMap<String, Double> tomap3 = new HashMap<String, Double>();
		tomap3.put("MOD", (double) 1000);
		lexicon.put("can", tomap3);

	}
} 
