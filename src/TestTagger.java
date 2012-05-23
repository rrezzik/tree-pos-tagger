
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import CorpusParser.Parser;
import CorpusParser.TupleSet;
import POSGui.POSGui;
import POSTagger.POSTagger;
import POS_DTBuilder.DTBuilder;
import POS_DTBuilder.DTNode;



public class TestTagger {

	public static void main(String [] args) {

		try {
			TupleSet tuples = Parser.parse("brown_para.corpus", "tagset.txt");
			//DTBuilder builder = new DTBuilder(tuples);
			DTNode tri_root = null;
			DTNode bi_root = null;
			FileInputStream fis_tri = null;
			ObjectInputStream in_tri = null;
			FileInputStream fis_bi = null;
			ObjectInputStream in_bi = null;
			try
			{
				fis_tri = new FileInputStream("trigram_graph.graph");
				in_tri = new ObjectInputStream(fis_tri);
				fis_bi = new FileInputStream("bigram_graph.graph");
				in_bi = new ObjectInputStream(fis_bi);

				tri_root = (DTNode)in_tri.readObject();
				bi_root = (DTNode)in_bi.readObject();

				in_tri.close();
				in_bi.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			catch(ClassNotFoundException ex)
			{
				ex.printStackTrace();
			}

			// +++++++++++++++++++++++++++++++++++++++++++++++++
			// DO NOT UNCOMMENT!! You don't want to be 
			// building the trees. This has already been done
			// +++++++++++++++++++++++++++++++++++++++++++++++++
			//
			//			DTNode root = builder.buildTree();
			//			FileOutputStream fos = null;
			//			ObjectOutputStream out = null;
			//			try
			//			{
			//				fos = new FileOutputStream("trigram_graph.graph");
			//				out = new ObjectOutputStream(fos);
			//				out.writeObject(root);
			//				out.close();
			//			}
			//			catch(IOException ex)
			//			{
			//				ex.printStackTrace();
			//			}




			// This prints the tree to a .png using GraphViz. I have commented it out as it is configured for my Linux computer
			// and I don't know the user's environment.
			
			//DTBuilder.printTree(tri_root, "tri");
			//DTBuilder.printTree(bi_root, "bi");
			
			
			// Create a tagger that uses the trigram tree and the bigram tree and used our lexicon derived from
			// the corpus
			POSTagger tagger = new POSTagger(tri_root, bi_root, tuples.getLexicon());


			// it's GUI time
			// tag some sentences!
			POSGui gui = new POSGui(tagger);
			gui.setVisible(true);

		} catch (IOException e) {
		}
	}
}
