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
package org.onexus.collection.api.query;

/**
 * BinaryFilter serves as the highest level abstract base class for all OQL
 * where filters that operate between two predicates. Example: AND and OR filters.
 */
public abstract class BinaryFilter extends Filter {

    private Filter left;
    private Filter right;

    /**
     * Keep this constructor for JAXB compatibility.
     */
    public BinaryFilter() {
        super();
    }

    /**
     * @param left  A <code>Filter</code> that represents the left predicate
     * @param right A <code>Filter</code> that represents the right predicate
     */
    public BinaryFilter(Filter left, Filter right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return The left predicate
     */
    public Filter getLeft() {
        return left;
    }

    /**
     * @param left The left predicate
     */
    public void setLeft(Filter left) {
        this.left = left;
    }

    /**
     * @return The right predicate
     */
    public Filter getRight() {
        return right;
    }

    /**
     * @param right The right predicate
     */
    public void setRight(Filter right) {
        this.right = right;
    }

    /**
     * @return the OQL string representation of this filter operand.
     */
    public abstract String getOperandSymbol();

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        String prevTabs = prettyPrint ? endingTabs(oql) : "";
        oql.append('(');
        oql.append(prettyPrint ? "\n\t" + prevTabs : " ");
        if (left != null) {
            left.toString(oql, prettyPrint);
        }
        oql.append(prettyPrint ? "\n" + prevTabs + getOperandSymbol() + "\n" + prevTabs + "\t" : " " + getOperandSymbol() + " ");
        if (right != null) {
            right.toString(oql, prettyPrint);
        }
        oql.append(prettyPrint ? "\n" + prevTabs : " ");
        oql.append(')');

        return oql;
    }
}
