package org.ginsim.gui.functioneditor;

import org.colomoto.logicalfunction.RegulationInfo;
import org.colomoto.logicalfunction.terms.FunctionTerm;
import org.colomoto.logicalfunction.terms.GroupOfTerms;
import org.colomoto.logicalfunction.terms.Operators;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.commongui.utils.ImageLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * @author Aurelien Naldi
 */
public class FunctionPanel extends JPanel implements HyperlinkListener, KeyListener, ChangeListener {

    public static final Icon IC_YES = ImageLoader.getImageIcon("fed_yes.png");
    public static final Icon IC_NOT = ImageLoader.getImageIcon("fed_not.png");
    public static final Icon IC_NNOT = ImageLoader.getImageIcon("fed_nonot.png");
    public static final Icon IC_ANY = ImageLoader.getImageIcon("fed_any.png");

    private static final Insets i_label = new Insets(4, 15, 4, 15);
    private static final Insets i_toggle = new Insets(4, 0, 4, 0);

    private final java.util.List<RegulatorToggle> toggles = new ArrayList<RegulatorToggle>();

    private final JLabel label = new JLabel();
    private String typeCache = null;

    private final JEditorPane functionField = new JEditorPane();

    private final GroupOfTerms root = new GroupOfTerms();
    private GroupOfTerms curGroup = root;
    private FunctionTerm curTerm = curGroup;

    private final JPanel availablePanel = new JPanel(new GridBagLayout());

    private RegulationInfo[] regulations;

    private JToggleButton bNot = new JToggleButton(IC_NNOT);
    private JToggleButton bAnd = new JToggleButton("&");
    private JToggleButton bOr = new JToggleButton("|");

    private boolean locked = false;


    public FunctionPanel() {
        super(new GridBagLayout());
        setFocusable(true);
        addKeyListener(this);

        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 1;
        cst.gridy = 1;
        cst.gridwidth = 5;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        cst.anchor = GridBagConstraints.NORTHWEST;

        functionField.addHyperlinkListener(this);
        functionField.setEditable(false);
        functionField.setContentType("text/html");
        functionField.addKeyListener(this);
        functionField.setFocusable(false);
        add(functionField, cst);

        cst.gridwidth = 1;
        cst.weighty = 0;
        cst.gridy = 2;
        cst.fill = GridBagConstraints.HORIZONTAL;
        label.setFocusable(false);
        add(label, cst);

        JButton b = new JButton("<-");
        b.setToolTipText("Move left");
        b.setFocusable(false);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moveSelection(false);
            }
        });
        cst.weightx = 0;
        cst.gridx++;
        add(b, cst);

        b = new JButton("->");
        b.setToolTipText("Move right");
        b.setFocusable(false);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moveSelection(true);
            }
        });
        cst.gridx++;
        add(b, cst);

        b = new JButton("U");
        b.setToolTipText("Select parent");
        b.setFocusable(false);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectUp();
            }
        });
        cst.gridx++;
        add(b, cst);

        b = new JButton("x");
        b.setToolTipText("Delete selection");
        b.setFocusable(false);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteSelection();
            }
        });
        cst.gridx++;
        add(b, cst);

        // kill focus on buttons
        bNot.setFocusable(false);
        bAnd.setFocusable(false);
        bOr.setFocusable(false);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(bAnd);
        bgroup.add(bOr);
        bAnd.setSelected(true);

        bNot.addChangeListener(this);
        bAnd.addChangeListener(this);
        bOr.addChangeListener(this);

        cst = new GridBagConstraints();
        cst.gridx = 1;
        cst.gridy = 1;
        cst.insets = i_label;
        JPanel fullPanel = new JPanel(new GridBagLayout());
        fullPanel.setBackground(Color.LIGHT_GRAY);
        fullPanel.add(bNot, cst);


        cst.insets = i_toggle;
        cst.gridx++;
        fullPanel.add(bAnd, cst);
        cst.gridx++;
        fullPanel.add(bOr, cst);

        cst.insets = i_label;
        cst.gridx++;
        JButton bSubGroup = new JButton("+()");
        bSubGroup.setFocusable(false);
        fullPanel.add(bSubGroup, cst);
        bSubGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addSubGroup();
            }
        });

        cst.gridx = 1;
        cst.gridy = 2;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        cst.gridwidth = 4;
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(availablePanel);
        fullPanel.add(sp, cst);

        cst = new GridBagConstraints();
        cst.anchor = GridBagConstraints.NORTHEAST;
        cst.gridx = 6;
        cst.gridy = 1;
        cst.gridheight = 3;
        cst.weighty = 1;
        cst.weightx = 0;
        cst.fill = GridBagConstraints.VERTICAL;
        add(fullPanel, cst);

        select(null);
    }

    public void moveSelection(boolean b) {
        curGroup.moveTerm(curTerm, b);
        functionUpdated();
    }

    public void select(Collection<RegulationInfo> regs) {
        if (regs == null) {
            regulations = null;
            reload();
            return;
        }
        int size = regs.size();
        regulations = new RegulationInfo[size];
        int idx=0;
        for (RegulationInfo reg: regs) {
            regulations[idx] = reg;
            idx++;
        }

        if (regs == null) {
            setSelectedGroup(null);
        } else {
            setSelectedGroup(root);
        }
        reload();
    }

    public void functionUpdated() {
        functionField.setText(root.toHTML(curGroup, curTerm));
    }

    public void setSelectedGroup(GroupOfTerms sub) {
        this.curGroup = sub;
        this.curTerm = sub;
    }

    public void selectUp() {
        GroupOfTerms parent = root.findParent(curGroup);
        if (parent != null) {
            curTerm = curGroup;
            curGroup = parent;
            refresh();
        }
    }

    public void deleteSelection() {
        if (curGroup == curTerm) {
            return;
        }
        curTerm = curGroup.removeTerm(curTerm);
        refresh();
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        String descr = hyperlinkEvent.getDescription();

        FunctionTerm term = root.findTerm(descr.substring(2));

        if (term != curTerm) {
            GroupOfTerms parent = root.findParent(term);
            if (parent == null) {
                return;
            }
            curTerm = term;
            curGroup = parent;
        } else if (term instanceof GroupOfTerms) {
            curGroup = (GroupOfTerms)term;
            curTerm = term;
        } else {
            term.negate();
        }
        refresh();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        char c = keyEvent.getKeyChar();

        if (typeCache != null) {
            switch( c ) {
                case ' ':
                case '\t':
                    validateTypeCache();
                    break;
                default:
                    if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                        updateTypeCache(typeCache+c);
                    }
            }

            return;
        }


        switch( c ) {
            case '!':
                curTerm.negate();
                refresh();
                break;
            case '(':
                addSubGroup();
                refresh();
                break;
            case '&':
                setOperator(Operators.AND);
                refresh();
                break;
            case '|':
                setOperator(Operators.OR);
                refresh();
                break;
            default:
                if (Character.isLetter(c)) {
                    updateTypeCache(""+c);
                }
        }
    }

    private void validateTypeCache() {
        if (typeCache == null || regulations == null) {
            typeCache = null;
            return;
        }

        typeCache = typeCache.toLowerCase();

        NodeInfo match = null;
        for (RegulationInfo reg: regulations) {
            NodeInfo ni = reg.getRegulator();
            String rid = ni.getNodeID().toLowerCase();

            if (rid.startsWith(typeCache)) {
                if (typeCache.length() == rid.length()) {
                    match = ni;
                    break;
                }

                if (match == null) {
                    match = ni;
                }
            }
        }

        if (match != null) {
            addOperand(match);
        }
        updateTypeCache(null);
        refresh();
    }

    private void updateTypeCache(String s) {
        this.typeCache = s;
        if (s == null || s.trim().length() == 0) {
            typeCache = null;
            label.setText("---");
            label.setForeground(Color.GRAY);
            return;
        }

        typeCache = s;
        label.setText(typeCache);
        label.setForeground(Color.BLACK);
        // TODO: show hints!
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        boolean hasControl = keyEvent.isControlDown() || keyEvent.isMetaDown();

        if (typeCache != null) {
            switch( keyCode ) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    int l = typeCache.length()-1;
                    updateTypeCache(typeCache.substring(0, l));
                    break;
                case KeyEvent.VK_ESCAPE:
                    updateTypeCache(null);
                    break;
                case KeyEvent.VK_ENTER:
                    validateTypeCache();
                    break;
            }

            return;
        }

        switch( keyCode ) {
            case KeyEvent.VK_UP:
                selectUp();
                break;
            case KeyEvent.VK_DOWN:
                if (curTerm instanceof GroupOfTerms) {
                    curGroup = (GroupOfTerms)curTerm;
                    refresh();
                }
                break;
            case KeyEvent.VK_ENTER:
                if (hasControl) {
                    System.out.println("TODO: validate function");
                } else if (curTerm instanceof GroupOfTerms) {
                    curGroup = (GroupOfTerms)curTerm;
                    refresh();
                } else {
                    curTerm.negate();
                    refresh();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (hasControl) {
                    moveSelection(false);
                    break;
                }
                curTerm = curGroup.findPrevTerm(curTerm);
                functionUpdated();
                break;
            case KeyEvent.VK_RIGHT :
                if (hasControl) {
                    moveSelection(true);
                    break;
                }
                curTerm = curGroup.findNextTerm(curTerm);
                functionUpdated();
                break;
            case KeyEvent.VK_DELETE:
                deleteSelection();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    public void addSubGroup() {
        curGroup = curGroup.addSubGroup();
        curTerm = curGroup;
        refresh();
    }

    public void addOperand(NodeInfo node) {
        curTerm = curGroup.addOperand(node);
    }

    public void removeOperand(NodeInfo node) {
        curGroup.removeOperand(node);
        // TODO: update selection
    }

    public void setOperator(Operators op) {
        curGroup.operator = op;
    }

    public NodeInfo getNode(int n) {
        return regulations[n].getRegulator();
    }

    public void refresh() {
        if (locked) {
            return;
        }

        locked = true;

        if (curGroup == null) {
            bNot.setIcon(IC_NNOT);
            bNot.setSelected(false);

            for (RegulatorToggle toggle: toggles) {
                toggle.setSelected(false);
            }
            locked = false;
            return;
        }

        if (curGroup.isNegated) {
            bNot.setIcon(IC_NOT);
            bNot.setSelected(true);
        } else {
            bNot.setIcon(IC_NNOT);
            bNot.setSelected(false);
        }

        if (curGroup.operator == Operators.AND) {
            bAnd.setSelected(true);
        } else if (curGroup.operator == Operators.OR) {
            bOr.setSelected(true);
        } else {
            System.out.println("Which function is this??");
        }

        // mirror the current group
        int idx = 0;
        for (RegulatorToggle toggle: toggles) {
            toggle.refresh(curGroup);
        }

        locked = false;
        functionUpdated();
    }

    public void reload() {
        availablePanel.removeAll();
        toggles.clear();

        GridBagConstraints cst = new GridBagConstraints();
        cst.anchor = GridBagConstraints.NORTHWEST;
        cst.gridx = 1;
        cst.gridy = 1;
        cst.fill = GridBagConstraints.HORIZONTAL;
        int size = 0;
        if (regulations != null) {
            size = regulations.length;
        }
        for (int r=0 ; r<size ; r++) {

            RegulationInfo reg = regulations[r];
            cst.insets = i_toggle;
            RegulatorToggle toggle = new RegulatorToggle(reg, 0);
            toggle.addChangeListener(this);
            toggles.add(toggle);
            availablePanel.add(toggle, cst);

            cst.gridy++;
        }

        availablePanel.doLayout();
        availablePanel.repaint();
        refresh();
        updateTypeCache(null);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (curGroup == null) {
            System.out.println("nothing to refresh");
            return;
        }

        if (locked) {
            return;
        }

        Object source = changeEvent.getSource();
        // if it comes from a regulator toggle
        if (source instanceof RegulatorToggle) {

            RegulatorToggle toggle = (RegulatorToggle)source;
            NodeInfo node = toggle.reg.getRegulator();

            if (toggle.isSelected()) {
                addOperand(node);
            } else {
                removeOperand(node);
            }
            refresh();
            return;
        }

        curGroup.isNegated = bNot.isSelected();
        if (bAnd.isSelected()) {
            setOperator(Operators.AND);
        } else {
            setOperator(Operators.OR);
        }

        refresh();
    }

}


class RegulatorToggle extends JCheckBox {

    public final RegulationInfo reg;
    public final int idx;

    public RegulatorToggle(RegulationInfo reg, int idx) {
        super(reg.getRegulator().toString());
        setFocusable(false);
        this.reg = reg;
        this.idx = idx;
    }

    public void refresh(GroupOfTerms group) {
        setSelected(group.hasOperand(reg.getRegulator()));
    }
}
