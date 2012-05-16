package org.onexus.core.query;

public class Not extends Filter {

    private Filter negatedFilter;

    public Not() {
        super();
    }

    public Not(Filter negatedFilter) {
        super();
        this.negatedFilter = negatedFilter;
    }

    public Filter getNegatedFilter() {
        return negatedFilter;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        boolean binaryFilter = prettyPrint && (negatedFilter instanceof BinaryFilter);
        String prevTabs = (binaryFilter ? endingTabs(oql) : "");

        oql.append("NOT");
        oql.append(binaryFilter ? "\n" + prevTabs : " ");

        if (negatedFilter !=null) negatedFilter.toString(oql, prettyPrint);

        return oql;
    }
}
