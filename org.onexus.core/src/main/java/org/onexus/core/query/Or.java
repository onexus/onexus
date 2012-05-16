package org.onexus.core.query;

public class Or extends BinaryFilter {

    public Or() {
        super();
    }

    public Or(Filter left, Filter right) {
        super(left, right);
    }

    @Override
    public String getOperandSymbol() {
        return "OR";
    }

}
