/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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

public class Or extends Filter {

    private Filter left;
    private Filter right;

    public Or() {
	this(null, null, null);
    }

    public Or(String collectionId, Filter left, Filter right) {
	super(collectionId);
	this.left = left;
	this.right = right;
    }

    public Filter getLeft() {
	return left;
    }

    public Filter getRight() {
	return right;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("(");
	builder.append(left);
	builder.append(") OR (");
	builder.append(right);
	builder.append(")");
	return builder.toString();
    }

}
