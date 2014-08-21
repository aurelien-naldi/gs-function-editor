package org.colomoto.logicalfunction.terms;

import org.colomoto.logicalmodel.NodeInfo;


public class FinalTerm implements FunctionTerm {

    public boolean isNegated = false;

    public final NodeInfo operand;

    public FinalTerm(NodeInfo node) {
        this.operand = node;
    }

    @Override
    public boolean isOperand() {
        return true;
    }

    public boolean hasOperand(NodeInfo operand) {
        return (operand == this.operand);
    }

    public String toString() {
        String s = operand.toString();
        if (isNegated) {
            return "!" + s;
        }
        return s;
    }

    public void negate() {
        isNegated = !isNegated;
    }
}
