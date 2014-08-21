package org.ginsim.gui.functioneditor;


import org.colomoto.logicalfunction.AssignmentFunction;
import org.colomoto.logicalfunction.FunctionBasedLogicalModel;
import org.colomoto.logicalfunction.RegulationInfo;
import org.colomoto.logicalfunction.RegulationSign;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.logicalfunction.FunctionNode;

import java.util.*;

public class FModel implements FunctionBasedLogicalModel {


    private final Map<NodeInfo, NodeMeta> nodes = new HashMap<NodeInfo, NodeMeta>();


    @Override
    public Collection<NodeInfo> getNodeInfos() {
        return nodes.keySet();
    }

    @Override
    public List<AssignmentFunction> getAssignements(NodeInfo node) {
        NodeMeta meta = nodes.get(node);
        if (meta == null) {
            return null;
        }

        return meta.fnode.getAssignements();
    }

    @Override
    public void invalidateFunction(NodeInfo node) {
        NodeMeta meta = nodes.get(node);
        if (meta == null) {
            return;
        }

        meta.fnode.invalidateFunction();
    }

    @Override
    public Collection<RegulationInfo> getRegulators(NodeInfo node) {
        NodeMeta meta = nodes.get(node);
        if (meta == null) {
            return null;
        }

        return meta.regulators;
    }

    @Override
    public FunctionNode createOperand(NodeInfo node) {
        return new NodeOperand(node);
    }

    @Override
    public void refresh(NodeInfo node) {

    }

    @Override
    public LogicalModel getLogicalModel() {
        return null;
    }

    public NodeInfo addNode(String name) {
        NodeInfo ni = new NodeInfo(name);
        nodes.put(ni, new NodeMeta(this, ni));
        return ni;
    }

    public void addRegulation(NodeInfo source, NodeInfo target, RegulationSign sign) {
        NodeMeta meta = nodes.get(target);
        if (meta == null) {
            return;
        }

        meta.regulators.add(new NodeRegulation(source, target, sign));
    }
}


class NodeMeta {

    public final FNode fnode;

    public final List<RegulationInfo> regulators;


    public NodeMeta(FunctionBasedLogicalModel model, NodeInfo info) {
        this.fnode = new FNode(model, info);
        this.regulators = new ArrayList<RegulationInfo>();
    }
}


class NodeRegulation implements RegulationInfo {

    private final NodeInfo source, target;

    private RegulationSign sign;

    private NodeOperand operand;

    public NodeRegulation(NodeInfo source, NodeInfo target, RegulationSign sign) {
        this.source = source;
        this.target = target;
        this.sign = sign;

        this.operand = null;
    }

    @Override
    public NodeInfo getRegulator() {
        return source;
    }

    @Override
    public NodeInfo getTarget() {
        return target;
    }

    @Override
    public int size() {
        return source.getMax();
    }

    @Override
    public int getThreshold(int k) {
        return k;
    }

    @Override
    public RegulationSign getSign(int k) {
        return sign;
    }

    @Override
    public FunctionNode getOperand(int k) {
        // TODO: this should be reset when the threshold is changed
        if (operand == null) {
            operand = new NodeOperand(source);
        }
        return operand;
    }

    public String toString() {
        return source+" ["+sign.getSymbol()+"]";
    }
}


class NodeOperand implements FunctionNode {

    private final NodeInfo info;

    public NodeOperand(NodeInfo info) {
        this.info = info;
    }

    @Override
    public String toString(boolean b) {
        return info.toString();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public int getMDD(MDDManager mddManager) {
        // FIXME: support multivalued stuff
        MDDVariable var = mddManager.getVariableForKey(info);
        return var.getNode(0,1);
    }
}
