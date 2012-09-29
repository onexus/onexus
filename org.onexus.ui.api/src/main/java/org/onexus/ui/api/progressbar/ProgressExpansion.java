package org.onexus.ui.api.progressbar;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.onexus.data.api.Progress;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ProgressExpansion implements Set<Progress>, Serializable
    {
        private static final long serialVersionUID = 1L;

        private static MetaDataKey<ProgressExpansion> KEY = new MetaDataKey<ProgressExpansion>()
        {
            private static final long serialVersionUID = 1L;
        };

        private Set<String> ids = new HashSet<String>();

        private boolean inverse = false;

        public void expandAll()
        {
            ids.clear();

            inverse = true;
        }

        public void collapseAll()
        {
            ids.clear();

            inverse = false;
        }

        @Override
        public boolean add(Progress foo)
        {
            if (inverse)
            {
                return ids.remove(foo.getId());
            }
            else
            {
                return ids.add(foo.getId());
            }
        }

        @Override
        public boolean remove(Object o)
        {
            Progress foo = (Progress)o;

            if (inverse)
            {
                return ids.add(foo.getId());
            }
            else
            {
                return ids.remove(foo.getId());
            }
        }

        @Override
        public boolean contains(Object o)
        {
            Progress foo = (Progress)o;

            if (inverse)
            {
                return !ids.contains(foo.getId());
            }
            else
            {
                return ids.contains(foo.getId());
            }
        }

        @Override
        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public <A> A[] toArray(A[] a)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Progress> iterator()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Progress> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Get the expansion for the session.
         *
         * @return expansion
         */
        public static ProgressExpansion get()
        {
            ProgressExpansion expansion = Session.get().getMetaData(KEY);
            if (expansion == null)
            {
                expansion = new ProgressExpansion();

                Session.get().setMetaData(KEY, expansion);
            }
            return expansion;
        }

}
