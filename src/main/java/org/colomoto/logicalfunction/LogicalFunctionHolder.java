package org.colomoto.logicalfunction;

import org.colomoto.mddlib.logicalfunction.FunctionNode;

import java.util.List;

public interface LogicalFunctionHolder {

    LogicalFunctionGenerator getGenerator();

    void setGenerator(LogicalFunctionGenerator generator);

    List<AssignmentFunction> getAssignements();

    void invalidateFunction();

    FunctionNode getOperand();

}
