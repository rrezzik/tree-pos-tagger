package CorpusParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Parser {

	static public TupleSet parse(String corp_file, String tagset) throws IOException {

		File corpus_file = new File(corp_file);
		File tag_file = new File(tagset);

		ArrayList<ArrayList<ArrayList<String>>> data_set = new ArrayList<ArrayList<ArrayList<String>>>();
		Lexicon lexicon = new Lexicon();
		//HashMap<String, HashMap<String, Double>> lexicon = new HashMap<String, HashMap<String, Double>>();
		Set<String> tag_set = new HashSet<String>();

		// grab the contents of the Brown corpus
		String file_contents = getContents(corpus_file);
		
		// grad the tags
		String tags_line = getContents(tag_file);
		
		String [] tags = tags_line.split((","));
		for (String t : tags) {
			tag_set.add(t);
		}

		String patternStr = "(^.*\\S+.*$)+";
		Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(file_contents);

		ArrayList<String> training_sentences = new ArrayList<String>();
		while (matcher.find()) {
			training_sentences.add(matcher.group());
		}

		// we have all the sentences from that file. Split the words and their tag
		for (String sentence : training_sentences) {
			//System.out.println(sentence);
			String [] pairs = sentence.split("\\s+");
			ArrayList<ArrayList<String>> parsed_sentence = new ArrayList<ArrayList<String>>();
			for (String pair : pairs) {
				if (!pair.isEmpty()) {
					ArrayList<String> parsed_pair = new ArrayList<String>();
					if (pair.split("/").length == 2) {
						String word = pair.split("/")[0];
						String tag = pair.split("/")[1];
						//tag_set.add(tag);
						
						lexicon.add(word, tag);

						// add the word and then the tag
						parsed_pair.add(pair.split("/")[0]);
						parsed_pair.add(pair.split("/")[1]);
						parsed_sentence.add(parsed_pair);
					}
				}
			}
			data_set.add(parsed_sentence);
		}


		// our dataset is a bunch of tagged sentences
		// we need to create trigram tuples from those

		ArrayList<ArrayList<ArrayList<String>>> tuples = new ArrayList<ArrayList<ArrayList<String>>>(); 
		ArrayList<ArrayList<String>> tuple = null;
		for (ArrayList<ArrayList<String>> sentence : data_set) {
			
			// for bigrams
//			for (int i = 0; i < sentence.size() - 1; i++) {
//				tuple = new ArrayList<ArrayList<String>>();
//				// make bigrams
//				tuple.addAll(sentence.subList(i, i+2));
//				tuples.add(tuple);
//				//System.out.println(tuples);
//			}
			
			// for trigrams
			for (int i = 0; i < sentence.size() - 2; i++) {
				tuple = new ArrayList<ArrayList<String>>();
				// make bigrams
				tuple.addAll(sentence.subList(i, i+3));
				tuples.add(tuple);
				//System.out.println(tuples);
			}

		}

		
		// all done :) return the tuples, now the hard work begins, training!
		
		// clean up tags with very low occurence
		lexicon.cleanUp();
		return new TupleSet(tuples, tag_set, lexicon);
	}

	static private String getContents(File aFile) {
		StringBuilder contents = new StringBuilder();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				/*
				 * readLine is a bit quirky :
				 * it returns the content of a line MINUS the newline.
				 * it returns null only for the END of the stream.
				 * it returns an empty String if two newlines appear in a row.
				 */
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return contents.toString();
	}

}