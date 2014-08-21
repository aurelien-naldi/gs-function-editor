package org.ginsim.gui.functioneditor;

import org.colomoto.logicalfunction.AssignmentFunction;
import org.colomoto.logicalfunction.FunctionBasedLogicalModel;
import org.colomoto.logicalfunction.LogicalFunctionGenerator;
import org.colomoto.logicalfunction.LogicalFunctionHolder;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.NodeInfoHolder;
import org.colomoto.mddlib.logicalfunction.FunctionNode;

import java.util.List;


public class FNode implements NodeInfoHolder, LogicalFunctionHolder {

    private final NodeInfo info;
    private final FunctionBasedLogicalModel model;
    private LogicalFunctionGenerator generator;

    private List<AssignmentFunction> assignments;


    public FNode(FunctionBasedLogicalModel model, NodeInfo info) {
        this.model = model;
        this.info = info;
    }

    @Override
    public NodeInfo getNodeInfo() {
        return info;
    }


    @Override
    public LogicalFunctionGenerator getGenerator() {
        return generator;
    }

    @Override
    public void setGenerator(LogicalFunctionGenerator generator) {
        this.generator = generator;
        if (generator != null) {
            assignments = null;
        }
    }

    @Override
    public List<AssignmentFunction> getAssignements() {
        if (assignments == null && generator != null) {
            assignments = generator.getAssignements(model, info);
        }

        return assignments;
    }

    @Override
    public void invalidateFunction() {

    }

    @Override
    public FunctionNode getOperand() {
        return new NodeOperand(info);
    }
}
