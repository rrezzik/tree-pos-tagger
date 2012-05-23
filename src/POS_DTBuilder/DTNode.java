package POS_DTBuilder;

import java.io.Serializable;
import java.util.HashMap;


public abstract class DTNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract String getTag();
	public abstract HashMap<String, DTNode> getChildren(); 
}
