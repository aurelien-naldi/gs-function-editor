package org.ginsim.gui.functioneditor;

import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.gui.functioneditor.FModel;
import org.colomoto.logicalfunction.RegulationSign;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.gui.functioneditor.FunctionPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by aurelien on 1/6/14.
 */
public class Launcher {

    static {
        ImageLoader.pushSearchPath("/org/ginsim/icons");
    }


    public static void main(String[] args) {


        JFrame frame = new JFrame("Test func editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        // create a simple model

        FModel model = new FModel();
        NodeInfo b2 = model.addNode("BM42");
        NodeInfo c5 = model.addNode("c5");
        NodeInfo x8 = model.addNode("x8");
        NodeInfo z3 = model.addNode("z3");
//        x8.setMax((byte)2);

        NodeInfo node = model.addNode("h7");


        model.addRegulation(b2, node, RegulationSign.POSITIVE);
        model.addRegulation(c5, node, RegulationSign.POSITIVE);
        model.addRegulation(x8, node, RegulationSign.NEGATIVE);
        model.addRegulation(z3, node, RegulationSign.DUAL);

        FunctionPanel fPanel = new FunctionPanel();

        JPanel cPanel = new JPanel(new GridBagLayout());


        // add selector buttons
        int n = 0;
        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;

        // add function edition panel
        cst.gridx = 0;
        cst.gridy = 1;
        cst.gridwidth = n+1;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        cPanel.add(fPanel, cst);

        fPanel.select(model.getRegulators(node));

        frame.setContentPane(cPanel);

        frame.setVisible(true);
    }
}
