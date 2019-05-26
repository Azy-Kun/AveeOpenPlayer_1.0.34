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
package christophedelory.atom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An Atom Feed Document is a representation of an Atom feed, including metadata about the feed, and some or all of the entries associated with it.
 * This element is the document (i.e., top-level) element of an Atom Feed Document, acting as a container for metadata and data associated with the feed.
 * Its element children consist of metadata elements followed by zero or more entry child elements.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="feed" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Feed extends Source
{
    /**
     * The list of entries.
     */
    private final List<Entry> _entries = new ArrayList<Entry>();

    /**
     * @param id an URI container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>id</code> is <code>null</code>.
     */
    @Override
    public void setId(final URIContainer id)
    {
        if (id == null)
        {
            throw new NullPointerException("no id");
        }

        super.setId(id);
    }

    /**
     * @param title a text container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>title</code> is <code>null</code>.
     */
    @Override
    public void setTitle(final TextContainer title)
    {
        if (title == null)
        {
            throw new NullPointerException("no title");
        }

        super.setTitle(title);
    }

    /**
     * @param updated a date. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>updated</code> is <code>null</code>.
     */
    @Override
    public void setUpdated(final Date updated)
    {
        if (updated == null)
        {
            throw new NullPointerException("no updated date");
        }

        super.setUpdated(updated);
    }

    /**
     * Returns the list of feed's entries.
     * The list is initially empty.
     * @return a list of entries. May be empty but not <code>null</code>.
     * @see #addEntry
     * @castor.field
     *  get-method="getEntries"
     *  set-method="addEntry"
     *  type="christophedelory.atom.Entry"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="entry"
     *  node="element"
     */
    public List<Entry> getEntries()
    {
        return _entries;
    }

    /**
     * Adds a feed's entry.
     * @param entry an entry. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>entry</code> is <code>null</code>.
     * @see #getEntries
     */
    public void addEntry(final Entry entry)
    {
        if (entry == null)
        {
            throw new NullPointerException("no entry");
        }

        _entries.add(entry);
    }
}
