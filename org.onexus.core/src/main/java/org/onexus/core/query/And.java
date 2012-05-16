package org.onexus.core.query;

public class And extends BinaryFilter {

    public And() {
        super();
    }

    public And(Filter left, Filter right) {
        super(left, right);
    }

    @Override
    public String getOperandSymbol() {
        return "AND";
    }

}
