package POS_DTBuilder;

import java.io.Serializable;
import java.util.HashMap;


public class DTTagNode extends DTNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, DTNode> children;
	private String tag;
	private DTNode parent;
	
	public DTTagNode(String tag, DTNode parent) {
		children = new HashMap<String, DTNode>();
		this.tag = tag;
		this.parent = parent;
	}
	
	public void addChild(String transition, DTNode child) {
		children.put(transition, child);
	}
	
	public String getTag() {
		return tag;
	}
	
	public String toString() {
		return getTag();
	}
	
	public HashMap<String, DTNode> getChildren() {
		return children;
	}

}
