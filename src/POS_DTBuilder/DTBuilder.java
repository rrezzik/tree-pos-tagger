package POS_DTBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import CorpusParser.TupleSet;
import GraphViz.GraphViz;

public class DTBuilder {
	private TupleSet tuples;

	public DTBuilder(TupleSet tuples) {
		this.tuples = tuples;
	}

	public DTNode buildTree() {
		return informationQ(tuples.getDataSet(), 1, tuples.getTagSet(), null);
	}

	public static DTNode informationQ(ArrayList<ArrayList<ArrayList<String>>> C, 
			int index, Set<String> tagset, DTNode parent) {

		// is this a leaf node? That is, is index < 0
		if (index < 0) {
			System.out.println("Reached a leaf");
			// calculate probability of tag three being all the other tags // a table essentially
			HashMap<String, Double> prob_table = new HashMap<String, Double>();

			for (String tag : tagset) {
				double count = 0;
				for (ArrayList<ArrayList<String>> tuple : C) {
					if (tuple.get(2).get(1).equals(tag)) {
						count++;
					}
				}
				double prob = count / C.size();
				prob_table.put(tag, prob);
			}

			return new DTLeafNode(parent, prob_table);
		}

		String best_tag = null;
		double best_info = Double.MAX_VALUE;
		ArrayList<ArrayList<ArrayList<String>>> C_pos = null;
		ArrayList<ArrayList<ArrayList<String>>> C_neg = null;

		ArrayList<ArrayList<ArrayList<String>>> C_pos_split = null;
		ArrayList<ArrayList<ArrayList<String>>> C_neg_split = null;		

		for (String test_tag : tagset) {
			double pos_part = 0;
			double neg_part = 0;

			double prob_succ = 0;
			double prob_fail = 0;

			// get the probability that test q succeeds or fails
			double succ = 0;
			double fail = 0;

			C_pos = new ArrayList<ArrayList<ArrayList<String>>>();
			C_neg = new ArrayList<ArrayList<ArrayList<String>>>();

			for (ArrayList<ArrayList<String>> tuple : C) {
				if (tuple.get(index).get(1).equals(test_tag)) {
					succ++;
					C_pos.add(tuple);
				}
				else {
					fail++;
					C_neg.add(tuple);
				}
			}

			prob_succ = succ/C.size();
			prob_fail = fail/C.size();

			// calculate the positive results part
			for (String tag : tagset) {
				double count = 0;
				for (ArrayList<ArrayList<String>> tuple : C_pos) {
					if (tuple.get(2).get(1).equals(tag)) {
						count++;
					}
				}
				if (count == 0) {
					pos_part += 0;
				}
				else
					pos_part += (count/C_pos.size()) * (Math.log(count/C_pos.size()) / Math.log(2));
			}

			pos_part *= prob_succ;

			// calculate the negative results part
			for (String tag : tagset) {
				double count = 0;
				for (ArrayList<ArrayList<String>> tuple : C_neg) {
					if (tuple.get(2).get(1).equals(tag)) {
						count++;
					}
				}
				if (count == 0) {
					neg_part += 0;
				}
				else
					neg_part += (count/C_neg.size()) * (Math.log(count/C_neg.size()) / Math.log(2));
			}

			neg_part *= prob_fail;

			double result = 0;
			result -= pos_part;
			result -= neg_part;

			if (result < best_info) {
				best_info = result;
				best_tag = test_tag;
				C_neg_split = C_neg;
				C_pos_split = C_pos;
			}

		}


		// so if the best tag we could find this time results in at least one subset of trigrams whose size 
		// is below some threshold (e.g. < 2) 
		// the probabilities for the third tag are estimated using all trigrams which have passed to this recursion step

		if (C_pos_split.size() <= 2 || C_neg_split.size() <= 2) {
			System.out.println("Reached a leaf");
			// calculate probability of tag three being all the other tags // a table essentially
			HashMap<String, Double> prob_table = new HashMap<String, Double>();

			for (String tag : tagset) {
				double count = 0;
				for (ArrayList<ArrayList<String>> tuple : C) {
					if (tuple.get(2).get(1).equals(tag)) {
						count++;
					}
				}
				double prob = count / C.size();
				prob_table.put(tag, prob);
			}

			return new DTLeafNode(parent, prob_table);
		}
		
		// we have the best tag, now do the two children
		DTTagNode node = new DTTagNode(best_tag, parent);
		node.addChild("yes", informationQ(C_pos_split, index-1, tagset, parent));
		node.addChild("no", informationQ(C_neg_split, index, tagset, parent));

		return node;
	}

	public static void printTree(DTNode root, String filename) {
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		HashMap<String, String> edges = new HashMap<String, String>();
		HashMap<String, String> transitions = new HashMap<String, String>();
		ArrayList<String> node_ids = new ArrayList<String>();

		printT(root, null, null, null, gv, edges, transitions, node_ids);

		Iterator it = edges.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			gv.addln(pairs.getValue() + " -> " + pairs.getKey() + " [ label = \"" + transitions.get(pairs.getKey()) + "\" ];");

		}

		gv.addln(gv.end_graph());
		//System.out.println(gv.getDotSource());

		String type = "png";
		File out = new File("/home/rafik/" + filename + "." + type);   // out.gif in this example
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
		try {
			Process p = Runtime.getRuntime().exec("eog " + "/home/rafik/" + filename + "." + type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printT(DTNode node, DTNode parent, String parent_id, String transition, GraphViz gv, HashMap<String, String> edges,
			HashMap<String, String> transitions, ArrayList<String> node_ids) {

		if (node instanceof DTLeafNode) {
			String id = getNodeID(node_ids);
			gv.addln(id + "[label=\"" + node.getTag() + "\"]");
			edges.put(id, parent_id);
			transitions.put(id, transition);
		}
		else {
			if (parent == null) {
				String id = getNodeID(node_ids);

				gv.addln(id + "[label=\"" + node.getTag() + "\"]");
				HashMap<String, DTNode> children = node.getChildren();
				Iterator it = children.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					printT((DTNode)pairs.getValue(), node, id, (String)pairs.getKey(), gv, edges, transitions, node_ids);
				}
			}
			else {
				String id = getNodeID(node_ids);

				gv.addln(id + "[label=\"" + node.getTag() + "\"]");
				edges.put(id, parent_id);
				transitions.put(id, transition);

				HashMap<String, DTNode> children = node.getChildren();
				Iterator it = children.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					printT((DTNode)pairs.getValue(), node, id, (String)pairs.getKey(), gv, edges, transitions, node_ids);
				}
			}


		}
	}

	private static String getNodeID(ArrayList<String> nodes) {
		String s = "n" + nodes.size();
		nodes.add(s);
		return s;
	}

}
