/*
 * Copyright (c) 2008, Christophe Delory
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY CHRISTOPHE DELORY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CHRISTOPHE DELORY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package christophedelory.playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * The base definition of time containers.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public abstract class AbstractTimeContainer extends AbstractPlaylistComponent
{
    /**
     * The list of components of this time container.
     */
    private final List<AbstractPlaylistComponent> _components = new ArrayList<AbstractPlaylistComponent>();

    /**
     * Returns an ordered array of playlist components present in this container.
     * @return an array of playlist components. May be empty but not <code>null</code>.
     * @see #addComponent
     * @see #removeComponent
     * @see #getComponentsNumber
     */
    public AbstractPlaylistComponent[] getComponents()
    {
        final AbstractPlaylistComponent[] ret = new AbstractPlaylistComponent[_components.size()];
        _components.toArray(ret); // Shall not throw NullPointerException, ArrayStoreException.

        return ret;
    }

    /**
     * Appends the specified playlist component to the end of this container.
     * @param component the playlist component to be appended to this container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @see #getComponents
     * @see #removeComponent
     * @see #addComponent(int,AbstractPlaylistComponent)
     */
    public void addComponent(final AbstractPlaylistComponent component)
    {
        component.setParent(this); // Throws NullPointerException if component is null.
        _components.add(component);
    }

    /**
     * Inserts the specified playlist component at the specified position in this container.
     * Shifts the component currently at that position (if any) and any subsequent components to the right (adds one to their indices).
     * @param index the index at which the specified component is to be inserted.
     * @param component the playlist component to be inserted in this container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt; getComponentsNumber()).
     * @see #getComponents
     * @see #addComponent
     * @see #removeComponent
     */
    public void addComponent(final int index, final AbstractPlaylistComponent component)
    {
        component.setParent(this); // Throws NullPointerException if component is null.
        _components.add(index, component); // May throw IndexOutOfBoundsException.
    }

    /**
     * Removes the first occurrence of the specified playlist component in this container.
     * If the container does not contain the element, it is unchanged.
     * @param component the playlist component to be removed from this container, if any. Shall not be <code>null</code>.
     * @return <code>true</code> if the container contained the specified element, <code>false</code> otherwise.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @see #addComponent
     * @see #getComponents
     * @see #removeComponent(int)
     */
    public boolean removeComponent(final AbstractPlaylistComponent component)
    {
        component.setParent(null); // Throws NullPointerException if component is null.

        return _components.remove(component);
    }

    /**
     * Removes the playlist component at the specified position in this container.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * @param index the index of the component to be removed.
     * @return the component that was removed from the container.
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= getComponentsNumber()).
     * @see #addComponent
     * @see #getComponents
     * @see #removeComponent(AbstractPlaylistComponent)
     */
    public AbstractPlaylistComponent removeComponent(final int index)
    {
        final AbstractPlaylistComponent component = _components.remove(index); // May throw IndexOutOfBoundsException.
        component.setParent(null);

        return component;
    }

    /**
     * Returns the number of playlist components in this container.
     * @return the number of playlist components in this container.
     * @see #getComponents
     */
    public int getComponentsNumber()
    {
        return _components.size();
    }

    @Override
    public void acceptDown(final PlaylistVisitor visitor) throws Exception
    {
        // Copy the list in an intermediate array, in order to allow the visitor to handle safely the list itself.
        final AbstractPlaylistComponent[] components = getComponents();

        for (AbstractPlaylistComponent component : components)
        {
            component.acceptDown(visitor); // May throw Exception.
        }
    }
}
