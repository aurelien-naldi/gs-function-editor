package org.colomoto.logicalfunction.generators;

import org.colomoto.logicalfunction.FunctionBasedLogicalModel;
import org.colomoto.logicalfunction.LogicalFunctionGenerator;
import org.colomoto.logicalmodel.NodeInfo;

/**
 * Common parts for simple generators: define a name, invalidate on change
 * 
 * @author Aurelien Naldi
 */
public abstract class AbstractGenerator implements LogicalFunctionGenerator {
	
	final String m_name;

	public AbstractGenerator(String name) {
		this.m_name = name;
	}

	@Override
	public String getID() {
		return m_name;
	}

	@Override
	public String toString() {
		return m_name;
	}

	@Override
	public void applyToNode(FunctionBasedLogicalModel model, NodeInfo node) {
		model.invalidateFunction(node);
	}
	
	@Override
	public void regulatorsChanged(FunctionBasedLogicalModel model, NodeInfo node) {
		model.invalidateFunction(node);
	}
}
