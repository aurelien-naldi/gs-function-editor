package org.colomoto.logicalfunction;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.logicalfunction.FunctionNode;

public interface RegulationInfo {

	NodeInfo getRegulator();

	NodeInfo getTarget();

    int size();

    int getThreshold(int k);

	RegulationSign getSign(int k);

    FunctionNode getOperand(int k);
}
