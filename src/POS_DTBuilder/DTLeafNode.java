package POS_DTBuilder;

import java.io.Serializable;
import java.util.HashMap;

public class DTLeafNode extends DTNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DTNode parent;
	private HashMap<String, Double> prob_table;
	
	public DTLeafNode(DTNode parent, HashMap<String, Double> prob_table) {
		this.prob_table = prob_table;
		this.setParent(parent);
	}

	public DTNode getParent() {
		return parent;
	}

	public void setParent(DTNode parent) {
		this.parent = parent;
	}

	@Override
	public HashMap<String, DTNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HashMap<String, Double> getProbTable() {
		return prob_table;
	}

	@Override
	public String getTag() {

		return "LEAF";
	}
	
}
