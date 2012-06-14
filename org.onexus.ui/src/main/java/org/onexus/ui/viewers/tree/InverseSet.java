package org.onexus.ui.viewers.tree;

import org.apache.wicket.model.IDetachable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * An inverse set.
 *
 * @author Sven Meier
 */
public class InverseSet<T> implements Set<T>, IDetachable
{

    private static final long serialVersionUID = 1L;

    private Set<T> set;

    /**
     * Create a full set.
     *
     * @param set
     *            the contained set
     */
    public InverseSet(Set<T> set)
    {
        this.set = set;
    }

    public void detach()
    {
        if (set instanceof IDetachable)
        {
            ((IDetachable)set).detach();
        }
    }

    public boolean isEmpty()
    {
        return !set.isEmpty();
    }

    public boolean contains(Object o)
    {
        return !set.contains(o);
    }

    public boolean add(T t)
    {
        return set.remove(t);
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object o)
    {
        return set.add((T)o);
    }

    public boolean addAll(Collection<? extends T> ts)
    {
        boolean changed = false;

        for (T t : ts)
        {
            changed |= set.remove(t);
        }

        return changed;
    }

    public boolean containsAll(Collection<?> cs)
    {
        for (Object c : cs)
        {
            if (set.contains(c))
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> cs)
    {
        boolean changed = false;

        for (Object c : cs)
        {
            changed |= set.add((T)c);
        }

        return changed;
    }

    public int size()
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    public <S> S[] toArray(S[] a)
    {
        throw new UnsupportedOperationException();
    }
}
