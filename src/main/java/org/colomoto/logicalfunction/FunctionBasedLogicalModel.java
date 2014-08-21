package org.colomoto.logicalfunction;

import java.util.Collection;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.logicalfunction.FunctionNode;

/**
 * A Logical model based on logical functions.
 * This abstracts the use of function and function generators for Edit-friendly logical models.
 * 
 * @author Aurelien Naldi
 */
public interface FunctionBasedLogicalModel {

	Collection<NodeInfo> getNodeInfos();
	
	List<AssignmentFunction> getAssignements(NodeInfo node);
	
	/**
	 * Mark the function associated to the given node as invalid.
	 * Its generator will be called next time the function is needed.
	 * 
	 * @param node
	 */
	void invalidateFunction(NodeInfo node);

	/**
	 * Get the list of regulations applying to the given node.
	 * 
	 * @param node
	 * @return a list of regulation objects
	 */
	Collection<RegulationInfo> getRegulators(NodeInfo node);


    FunctionNode createOperand(NodeInfo node);

	/**
	 * FIXME: what should this do?
	 * 
	 * @param node
	 */
	void refresh(NodeInfo node);

	
	LogicalModel getLogicalModel();
}
