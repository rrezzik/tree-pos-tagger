package POSTagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import CorpusParser.Lexicon;
import POS_DTBuilder.DTLeafNode;
import POS_DTBuilder.DTNode;

public class POSTagger {

	private Lexicon lexicon;
	private DTNode tri_tree;
	private DTNode bi_tree;

	public POSTagger(DTNode tri_tree, DTNode bi_tree, Lexicon lexicon) {
		this.tri_tree = tri_tree;
		this.bi_tree = bi_tree;
		this.lexicon = lexicon;
	}


	public ArrayList<String> tagSentence(String sentence) {
		ArrayList<String> tag_list = new ArrayList<String>();
		System.out.println("Received sentence: " + sentence);

		// remove period if it's there
		if (sentence.endsWith("."))
			sentence = sentence.substring(0, sentence.length() - 1);

		String[] words = sentence.split("\\s+");
		String tag_1 = "";
		String tag_2 = "";
		String best_tag = "";

		for (int index = 0; index < words.length; index++) {
			System.out.println("Determining POS for: " + words[index]);

			HashMap<String, Double> probability_map = null;
			HashMap<String, Double> tag_map = null;

			tag_map = lexicon.getTagsOf(words[index]);

			// is this the first word in the sentence, do a guess
			if (index < 1) {
				// lookup the probability map
				tag_map = lexicon.getTagsOf(words[index]);
				if (tag_map == null) 
					tag_map = lexicon.getTagsOf(words[index].toLowerCase());
				if (tag_map == null)
					System.out.println("Not in here");
				if (tag_map.keySet().size() == 1) {
					//String best_tag = null;
					for (Entry<String, Double> entry : tag_map.entrySet()) {
						best_tag = entry.getKey();
						tag_1 = best_tag;
					}	

					System.out.println("Only key: " + best_tag);

				}
				else {
					// return the tag with highest probability
					double max = Double.MIN_VALUE;
					//String best_tag = null;
					for (Entry<String, Double> entry : tag_map.entrySet()) {
						if (entry.getValue() > max) {
							max = entry.getValue();
							best_tag = entry.getKey();
						}
					}

					tag_1 = best_tag;

				}
			}

			// second word in the sentence? use bigram tree
			else if (index == 1) {
				tag_map = lexicon.getTagsOf(words[index]);
				if (tag_map == null)
					tag_map = lexicon.getTagsOf(words[index].toLowerCase());

				if (tag_map == null) {
					System.out.println("No tags found for this word");
					best_tag = "";
				}

				else if (tag_map.keySet().size() == 1) {
					for (Entry<String, Double> entry : tag_map.entrySet()) {
						best_tag = entry.getKey();
						tag_2 = best_tag;
					}	

					System.out.println("Only key: " + best_tag);

				}
				else {
					DTNode current_node = bi_tree;
					
					while (!(current_node instanceof DTLeafNode)) {
						System.out.println("Testing node: " + current_node.getTag());
						if (tag_1.equals(current_node.getTag())) {
							current_node = current_node.getChildren().get("yes");
						}
						else {
							current_node = current_node.getChildren().get("no");
						}
					}


					// ok we reached a leaf: we will say the result is the one with the highest probability
					DTLeafNode leafNode = (DTLeafNode) current_node;
					probability_map = leafNode.getProbTable();
					double max = Double.MIN_VALUE;
					
					// check for the possibilities of this word in the lexicon
					// then out of those which is most probable given the sequence 
					// of words
					double prob;
					for (Entry<String, Double> tag : tag_map.entrySet()) {
						if (probability_map.containsKey(tag.getKey())) {
							prob = probability_map.get(tag.getKey());
							if (prob > max) {
								max = prob;
								best_tag = tag.getKey();
							}
						}
					}

					tag_2 = best_tag;
					System.out.println("It's tagged as: " + best_tag);
				}
				

			}


			else {
				tag_map = lexicon.getTagsOf(words[index]);
				if (tag_map == null)
					tag_map = lexicon.getTagsOf(words[index].toLowerCase());

				if (tag_map == null) {
					System.out.println("No tags found for this word");
					best_tag = "";
				}
				
				else if (tag_map.keySet().size() == 1) {
					for (Entry<String, Double> entry : tag_map.entrySet()) {
						best_tag = entry.getKey();
					}
					
					tag_2 = tag_1;
					tag_1 = best_tag;


				}

				// third word and on, use trigram
				else {
					DTNode current_node = tri_tree;
					int tag_to_check = 1; 
					
					while (!(current_node instanceof DTLeafNode)) {
						if (tag_to_check == 1) {
							if (tag_1.equals(current_node.getTag())) {
								current_node = current_node.getChildren().get("yes");
								tag_to_check++;
							}
							else {
								current_node = current_node.getChildren().get("no");
							}
						}
						else {
							if (tag_2.equals(current_node.getTag())) {
								current_node = current_node.getChildren().get("yes");
							}
							else {
								current_node = current_node.getChildren().get("no");
							}
						}
					}
					// ok we reached a leaf: we will say the result is the one with the highest probability
					DTLeafNode leafNode = (DTLeafNode) current_node;
					probability_map = leafNode.getProbTable();
					double max = Double.MIN_VALUE;
					
					// check for the possibilities of this word in the lexicon
					// then out of those which is most probable given the sequence 
					// of words
					double prob;
					for (Entry<String, Double> tag : tag_map.entrySet()) {
						if (probability_map.containsKey(tag.getKey())) {
							prob = probability_map.get(tag.getKey());
							if (prob > max) {
								max = prob;
								best_tag = tag.getKey();
							}
						}
					}

					tag_1 = tag_2;
					tag_2 = best_tag;
					System.out.println("It's tagged as: " + best_tag);
				}


			}

			// add the tag to the arraylist
			tag_list.add(best_tag);
		}

		// return our list of tags for each word
		return tag_list;
	}
}
