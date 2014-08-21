package org.colomoto.logicalfunction;

import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;

/**
 * Common interface for all function generators.
 * Function generators should be single-instance classes that can be applied to
 * multiple logical nodes.
 * 
 * Methods correspond to refresh paths: apply to a new node, add or remove regulators.
 * 
 * @author Aurelien Naldi
 */
public interface LogicalFunctionGenerator {
	
	/**
	 * @return an identifier for this generator. It should be unique.
	 */
	public String getID();
	
	/**
	 * Apply this function generator for a new node.
	 * @param model
	 * @param node
	 */
	public void applyToNode(FunctionBasedLogicalModel model, NodeInfo node);

	/**
	 * The regulators of a node have changed, update the function.
	 * @param node
	 * @param model
	 */
	public void regulatorsChanged(FunctionBasedLogicalModel model, NodeInfo node);

	/**
	 * Generate the assignments. This should only be called by the node itself when needed.
	 * 
	 * @param model
	 * @param node
	 * 
	 * @return the list of generated assignments functions.
	 */
	public List<AssignmentFunction> getAssignements(FunctionBasedLogicalModel model, NodeInfo node);
}
