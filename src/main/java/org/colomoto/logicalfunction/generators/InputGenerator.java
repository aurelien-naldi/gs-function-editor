package org.colomoto.logicalfunction.generators;

import java.util.ArrayList;
import java.util.List;

import org.colomoto.logicalfunction.AssignmentFunction;
import org.colomoto.logicalfunction.FunctionBasedLogicalModel;
import org.colomoto.logicalfunction.LogicalFunctionGenerator;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.logicalfunction.FunctionNode;

/**
 * Function generator for input nodes: ignore regulators and returns the current value.
 * 
 * @author Aurelien Naldi
 *
 */
public class InputGenerator extends AbstractGenerator {
	public static final LogicalFunctionGenerator GENERATOR = new InputGenerator();

	private InputGenerator() {
		super("Input");
	}

	@Override
	public List<AssignmentFunction> getAssignements(FunctionBasedLogicalModel model, NodeInfo node) {
		List<AssignmentFunction> l = new ArrayList<AssignmentFunction>();

		FunctionNode function = null;
		int max = node.getMax();
		for (int i=1 ; i<=max ; i++) {
			function = model.createOperand(node);
			l.add(new AssignmentFunction(i, function));
		}
		return l;
	}
}
