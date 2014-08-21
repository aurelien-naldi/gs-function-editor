package org.colomoto.logicalfunction.terms;

import org.colomoto.logicalmodel.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class GroupOfTerms implements FunctionTerm {

    public boolean isNegated = false;

    public Operators operator = Operators.AND;

    public List<FunctionTerm> terms = new ArrayList<FunctionTerm>();

    @Override
    public boolean isOperand() {
        return true;
    }

    public void negate() {
        isNegated = !isNegated;
    }

    @Override
    public boolean hasOperand(NodeInfo operand) {
        return findOperand(operand) >= 0;
    }

    private int findOperand(NodeInfo operand) {
        int idx = 0;
        for (FunctionTerm term: terms) {
            if (term instanceof FinalTerm && term.hasOperand(operand)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    public FunctionTerm findTerm(String path) {
        if (path == null || path.length() == 0) {
            return this;
        }

        String[] tpath = path.split(",");
        int[] ipath = new int[tpath.length];

        for (int i=0 ; i<tpath.length ; i++) {
            ipath[i] = Integer.parseInt(tpath[i]);
        }

        return findTerm(ipath, 0);
    }

    public GroupOfTerms findParent(FunctionTerm term) {
        if (term == this) {
            return this;
        }

        for (FunctionTerm t: terms) {
            if (t == term) {
                return this;
            }
            if (t instanceof GroupOfTerms) {
                GroupOfTerms parent = ((GroupOfTerms)t).findParent(term);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

    private FunctionTerm findTerm(int[] path, int idx) {
        FunctionTerm term = terms.get(path[idx]);

        if (idx == path.length-1) {
            return term;
        }

        if (term instanceof GroupOfTerms) {
            GroupOfTerms sub = (GroupOfTerms)term;
            return sub.findTerm(path, idx+1);
        }

        return null;
    }

    public void removeOperand(NodeInfo operand) {
        int idx = findOperand(operand);
        while (idx >= 0) {
            terms.remove(idx);
            idx = findOperand(operand);
        }
    }

    public void addTerm(FunctionTerm term) {
        terms.add(term);
    }

    public FinalTerm addOperand(NodeInfo operand) {
        int idx = findOperand(operand);
        if (idx >= 0) {
            return (FinalTerm)terms.get(idx);
        }

        FinalTerm term = new FinalTerm(operand);
        addTerm(term);
        return term;
    }

    public void moveTerm(FunctionTerm term, boolean increase) {
        int idx = terms.indexOf(term);
        if (idx < 0) {
            return;
        }

        int target = increase ? idx+1 : idx-1;
        if (target < 0 || target >= terms.size()) {
            return;
        }

        terms.set(idx, terms.get(target));
        terms.set(target, term);
    }

    public FunctionTerm removeTerm(FunctionTerm term) {
        int idx = terms.indexOf(term);
        if (idx >= 0) {
            terms.remove(term);
            if (idx == terms.size()) {
                idx--;
            }

            if (idx >= 0) {
                return terms.get(idx);
            }
        }
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = " & ";
        if (operator == Operators.OR) {
            sep = " | ";
        }

        if (isNegated) {
            sb.append("!");
        }

        sb.append("(");

        boolean first = true;
        for (FunctionTerm term:terms) {
            if (first) {
                first = false;
            } else {
                sb.append(sep);
            }
            sb.append(term.toString());
        }

        sb.append(")");

        return sb.toString();
    }

    public String toHTML(GroupOfTerms curGroup, FunctionTerm curTerm) {
        StringBuffer sb = new StringBuffer("<html>");
        fillHTML(sb, curGroup, curTerm, "");
        sb.append("</html>");
        return sb.toString();
    }

    public void fillHTML(StringBuffer sb, GroupOfTerms curGroup, FunctionTerm curTerm, String path) {
        boolean isCurrent = (curGroup == this);
        int curTermIdx = -1;
        if (isCurrent) {
            if (curTerm != this) {
                curTermIdx = 0;
                for (FunctionTerm term: terms) {
                    if (curTerm == term) {
                        break;
                    }
                    curTermIdx++;
                }
                sb.append("<span style='background:#ddddff;'>");
            } else {
                sb.append("<span style='background:#bbffbb;'>");
            }
        } else if (terms.size() < 1) {

            // allow the selection of empty groups!
            String text = isNegated ? "!()" : "()";
            sb.append("<a href='N:"+path+"'>"+text+"</a>");
            return;
        }

        String basePath = path;
        if (path.length() > 0) {
            basePath += ",";
        }

        String sep = " & ";
        if (operator == Operators.OR) {
            sep = " | ";
        }

        if (isNegated) {
            sb.append("!");
        }

        sb.append("(");

        boolean first = true;
        int idx = 0;
        for (FunctionTerm term:terms) {
            if (first) {
                first = false;
            } else {
                sb.append(sep);
            }

            if (idx == curTermIdx) {
                sb.append("<span style='background:#bbffbb;'>");
            }
            String curPath = basePath+idx;
            if ((term instanceof GroupOfTerms)) {
                GroupOfTerms sub = (GroupOfTerms)term;
                sub.fillHTML(sb, curGroup, curTerm, curPath);
            } else {
                sb.append("<a href='N:"+curPath+"'>");
                sb.append(term.toString());
                sb.append("</a>");
            }

            if (idx == curTermIdx) {
                sb.append("</span>");
            }
            idx++;
        }

        sb.append(")");
        if (isCurrent) {
            sb.append("</span>");
        }
    }

    public GroupOfTerms addSubGroup() {
        GroupOfTerms sub = new GroupOfTerms();
        terms.add( sub );
        return sub;
    }

    public void negateTerm(int idx) {
        terms.get(idx).negate();
    }

    public GroupOfTerms findParent(GroupOfTerms cur) {
        for (FunctionTerm term: terms) {
            if (term == cur) {
                return this;
            }

            if (term instanceof GroupOfTerms) {
                GroupOfTerms p = ((GroupOfTerms)term).findParent(cur);
                if (p != null) {
                    return p;
                }
            }
        }
        return null;
    }

    public FunctionTerm findNextTerm(FunctionTerm cur) {
        int idx = 0;
        if (cur != this) {
            for (FunctionTerm term: terms) {
                idx++;
                if (term == cur) {
                    break;
                }
            }
        }

        if (idx < 0 || idx >= terms.size()) {
            return this;
        }

        return terms.get(idx);
    }
    public FunctionTerm findPrevTerm(FunctionTerm cur) {
        int idx = -1;
        if (cur != this) {
            idx = -1;
            for (FunctionTerm term: terms) {
                idx++;
                if (term == cur) {
                    break;
                }
            }
            idx--;
        } else {
            idx = terms.size()-1;
        }

        if (idx < 0 || idx >= terms.size()) {
            return this;
        }

        return terms.get(idx);
    }
}
