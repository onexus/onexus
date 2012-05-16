package org.onexus.core.query;

public abstract class BinaryFilter extends Filter {

    private Filter left;
    private Filter right;

    public BinaryFilter() {
        super();
    }

    public BinaryFilter(Filter left, Filter right) {
        this.left = left;
        this.right = right;
    }

    public Filter getLeft() {
        return left;
    }

    public void setLeft(Filter left) {
        this.left = left;
    }

    public Filter getRight() {
        return right;
    }

    public void setRight(Filter right) {
        this.right = right;
    }

    public abstract String getOperandSymbol();

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        String prevTabs = (prettyPrint ? endingTabs(oql) : "");
        oql.append('(');
        oql.append(prettyPrint ? "\n\t" + prevTabs : " ");
        if (left != null) left.toString(oql, prettyPrint);
        oql.append(prettyPrint ? "\n" + prevTabs + getOperandSymbol() + "\n" + prevTabs + "\t" : " " + getOperandSymbol() +" ");
        if (right != null) right.toString(oql, prettyPrint);
        oql.append(prettyPrint ? "\n" + prevTabs : " ");
        oql.append(')');

        return oql;
    }
}
