package org.colomoto.logicalfunction.terms;

import org.colomoto.logicalmodel.NodeInfo;

public interface FunctionTerm {

    boolean isOperand();

    boolean hasOperand(NodeInfo operand);

    void negate();
}
