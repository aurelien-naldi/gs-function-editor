package org.colomoto.logicalfunction;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDOperator;
import org.colomoto.mddlib.logicalfunction.FunctionNode;
import org.colomoto.mddlib.operators.OverwriteOperator;

public class AssignmentFunction {
	
	protected int value;
	protected FunctionNode function;

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public AssignmentFunction(int value, FunctionNode function) {
		this.value = value;
		this.function = function;
	}

	public int applyMDD(MDDManager factory, int current) {
		if (function == null) {
			return value;
		}
		int local = function.getMDD(factory);
		MDDOperator op = OverwriteOperator.getOverwriteAction(value);
		int ret = op.combine(factory, current, local);
		factory.free(local);
		return ret;
	}

	public FunctionNode getFunction() {
		return function;
	}
	
	public void setFunction(FunctionNode f, FunctionBasedLogicalModel model, NodeInfo node) {
		this.function = f;
		model.refresh(node);
	}

	@Override
	public String toString() {
		return value+": "+function;
	}

}
