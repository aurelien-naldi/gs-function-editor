package org.colomoto.logicalfunction;

/**
 * Helper class to store a text representation of logical functions during parsing
 * ready to be injected into a LogicalNode
 * 
 * It should probably not be used outside of this use case.
 * 
 * @author Aurelien Naldi
 */
public class AssignmentData {
	final public int value;
	final public String function;
	
	public AssignmentData(int value, String function) {
		super();
		this.value = value;
		this.function = function;
	}
}
