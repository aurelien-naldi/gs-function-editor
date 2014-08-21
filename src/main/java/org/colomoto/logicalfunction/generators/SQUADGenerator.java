package org.colomoto.logicalfunction.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.colomoto.logicalfunction.AssignmentFunction;
import org.colomoto.logicalfunction.FunctionBasedLogicalModel;
import org.colomoto.logicalfunction.LogicalFunctionGenerator;
import org.colomoto.logicalfunction.RegulationInfo;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.logicalfunction.FunctionNode;
import org.colomoto.mddlib.logicalfunction.operators.AndOperatorFactory;
import org.colomoto.mddlib.logicalfunction.operators.NotOperatorFactory;
import org.colomoto.mddlib.logicalfunction.operators.OrOperatorFactory;

public class SQUADGenerator extends AbstractGenerator {

	public static final LogicalFunctionGenerator GENERATOR = new SQUADGenerator();

	private SQUADGenerator() {
		super("SQUAD");
	}

	@Override
	public List<AssignmentFunction> getAssignements(FunctionBasedLogicalModel model, NodeInfo node) {
		List<AssignmentFunction> l = new ArrayList<AssignmentFunction>();
		FunctionNode function = null;

		Collection<RegulationInfo> regulations = model.getRegulators(node);
		if (regulations.size() < 1) {
			// no regulators: rely on the default value
			return l;
		}

		FunctionNode frepr =  null;
		for (RegulationInfo reg: regulations) {
			FunctionNode f = reg.getOperand(0);
			switch (reg.getSign(0)) {
			case POSITIVE:
				function = function == null ?  f : OrOperatorFactory.FACTORY.getNode(function, f);
				break;
			case NEGATIVE:
				frepr = frepr == null ?  f : OrOperatorFactory.FACTORY.getNode(frepr, f);
				break;
			}
		}

		// join activators and inhibitors
		if (frepr != null) {
			frepr = NotOperatorFactory.FACTORY.getNode(frepr);
			if (function != null) {
				function = AndOperatorFactory.FACTORY.getNode(function, frepr);
			} else {
				function = frepr;
			}
		}

		l.add(new AssignmentFunction(1, function));

		return l;
	}
}
