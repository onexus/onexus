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
package org.onexus.ui.website.utils.reflection;

import com.google.common.base.Objects;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.wicket.util.lang.PropertyResolver;

import java.util.*;


public class ListComposer<T> extends AbstractList<T> {
    
    private Object parent;
    private String[] expression;
    
    private Iterator<T> chain;

    public ListComposer(Object parent, String expression) {
        super();
        this.parent = parent;
        this.expression = expression.split("\\.");
    }

    @Override
    public T get(int index) {
        
        Iterator<T> it = iterator();
        
        T value = it.next();
        for (int i=0; i < index; i++) {
            value = it.next();
        }
        
        return value;
    }

    @Override
    public Iterator<T> iterator() {
        return newIteratorChain();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListIteratorWrapper( iterator() ); 
    }

    @Override
    public int size() {

        Iterator<T> it = newIteratorChain();

        int size=0;
        while(it.hasNext()) {
            size++;
        }
        
        return size;
    }
    
    private Iterator<T> newIteratorChain() {
        IteratorChain chain = new IteratorChain();
        addInnerIterators(chain, expression, 0, parent);
        return chain;
    }
    
    private static void addInnerIterators(IteratorChain chain, String[] expression, int depth, Object currentParent) {

        String method = expression[depth];
        List list = (List) PropertyResolver.getValue(method, currentParent);

        int nextDepth = depth + 1;
        if (nextDepth < expression.length )  {
            for(Object child : list) {
                addInnerIterators(chain, expression, nextDepth, child);
            }
        } else {
            chain.addIterator(list.iterator());
        }
        
    }
    
}
