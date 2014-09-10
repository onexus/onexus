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
 * Logical AND operator between two OQL filters.
 */
public class And extends BinaryFilter {

    /**
     * Keep this constructor for JAXB compatibility.
     */
    @SuppressWarnings("UnusedDeclaration")
    public And() {
    }

    /**
     * Create a AND filter.
     *
     * @param left  The left filter.
     * @param right The right filter.
     */
    public And(final Filter left, final Filter right) {
        super(left, right);
    }

    @Override
    public final String getOperandSymbol() {
        return "AND";
    }

}
