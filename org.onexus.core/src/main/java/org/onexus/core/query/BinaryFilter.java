/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
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
