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
 * Represents an individual entry, acting as a container for metadata and data associated with the entry.
 * This element can appear as a child of the feed element, or it can appear as the document (i.e., top-level) element of a stand-alone Atom Entry Document.
 * An Atom Entry Document represents exactly one Atom entry, outside of the context of an Atom feed.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="entry" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Entry extends Common
{
    /**
     * The authors of the entry.
     */
    private final List<Person> _authors = new ArrayList<Person>();

    /**
     * The categories associated with the entry.
     */
    private final List<Category> _categories = new ArrayList<Category>();

    /**
     * A list of contents.
     */
    private final List<Content> _contents = new ArrayList<Content>();

    /**
     * The list of persons or other entities who contributed to the entry.
     */
    private final List<Person> _contributors = new ArrayList<Person>();

    /**
     * A permanent, universally unique identifier for the entry.
     */
    private URIContainer _id = null;

    /**
     * The list of references from this entry to a Web resource.
     */
    private final List<Link> _links = new ArrayList<Link>();

    /**
     * An instant in time associated with an event early in the life cycle of the entry.
     */
    private Date _published = null;

    /**
     * Information about rights held in and over this entry.
     */
    private TextContainer _rights = null;

    /**
     * The source feed.
     */
    private Source _source = null;

    /**
     * A short summary, abstract, or excerpt of an entry.
     */
    private TextContainer _summary = null;

    /**
     * A human-readable title for this entry.
     */
    private TextContainer _title = null;

    /**
     * The most recent instant in time when this entry was modified in a way the publisher considers significant.
     */
    private Date _updated = null;

    /**
     * Returns the list of entry's authors.
     * The list is initially empty.
     * @return a list of authors. May be empty but not <code>null</code>.
     * @see #addAuthor
     * @castor.field
     *  get-method="getAuthors"
     *  set-method="addAuthor"
     *  type="christophedelory.atom.Person"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="author"
     *  node="element"
     */
    public List<Person> getAuthors()
    {
        return _authors;
    }

    /**
     * Adds an entry's author.
     * @param author an author. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>author</code> is <code>null</code>.
     * @see #getAuthors
     */
    public void addAuthor(final Person author)
    {
        if (author == null)
        {
            throw new NullPointerException("no author");
        }

        _authors.add(author);
    }

    /**
     * Returns the list of categories associated with the entry.
     * The list is initially empty.
     * @return a list of categories. May be empty but not <code>null</code>.
     * @see #addCategory
     * @castor.field
     *  get-method="getCategories"
     *  set-method="addCategory"
     *  type="christophedelory.atom.Category"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="category"
     *  node="element"
     */
    public List<Category> getCategories()
    {
        return _categories;
    }

    /**
     * Adds a category to this entry.
     * @param category a category. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>category</code> is <code>null</code>.
     * @see #getCategories
     */
    public void addCategory(final Category category)
    {
        if (category == null)
        {
            throw new NullPointerException("no category");
        }

        _categories.add(category);
    }

    /**
     * Returns a list of contents.
     * The list is initially empty.
     * @return a list of contents. May be empty but not <code>null</code>.
     * @see #addContent
     * @castor.field
     *  get-method="getContents"
     *  set-method="addContent"
     *  type="christophedelory.atom.Content"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="content"
     *  node="element"
     */
    public List<Content> getContents()
    {
        return _contents;
    }

    /**
     * Adds a content to this entry.
     * @param content a content. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>content</code> is <code>null</code>.
     * @see #getContents
     */
    public void addContent(final Content content)
    {
        if (content == null)
        {
            throw new NullPointerException("no content");
        }

        _contents.add(content);
    }

    /**
     * Returns the list of persons or other entities who contributed to the entry.
     * The list is initially empty.
     * @return a list of contributors. May be empty but not <code>null</code>.
     * @see #addContributor
     * @castor.field
     *  get-method="getContributors"
     *  set-method="addContributor"
     *  type="christophedelory.atom.Person"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="contributor"
     *  node="element"
     */
    public List<Person> getContributors()
    {
        return _contributors;
    }

    /**
     * Adds a person or other entity who contributed to the entry.
     * @param contributor a contributor. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>contributor</code> is <code>null</code>.
     * @see #getContributors
     */
    public void addContributor(final Person contributor)
    {
        if (contributor == null)
        {
            throw new NullPointerException("no contributor");
        }

        _contributors.add(contributor);
    }

    /**
     * Returns a permanent, universally unique identifier for the entry.
     * @return an URI container. May be <code>null</code>.
     * @see #setId
     * @castor.field
     *  get-method="getId"
     *  set-method="setId"
     * @castor.field-xml
     *  name="id"
     *  node="element"
     */
    public URIContainer getId()
    {
        return _id;
    }

    /**
     * Initializes the permanent, universally unique identifier for the entry.
     * @param id an URI container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>id</code> is <code>null</code>.
     * @see #getId
     */
    public void setId(final URIContainer id)
    {
        if (id == null)
        {
            throw new NullPointerException("no id");
        }

        _id = id;
    }

    /**
     * Returns the list of references from this entry to a Web resource.
     * The list is initially empty.
     * @return a list of links. May be empty but not <code>null</code>.
     * @see #addLink
     * @castor.field
     *  get-method="getLinks"
     *  set-method="addLink"
     *  type="christophedelory.atom.Link"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="link"
     *  node="element"
     */
    public List<Link> getLinks()
    {
        return _links;
    }

    /**
     * Adds a reference from this entry to a Web resource.
     * @param link a link. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @see #getLinks
     */
    public void addLink(final Link link)
    {
        if (link == null)
        {
            throw new NullPointerException("no link");
        }

        _links.add(link);
    }

    /**
     * Returns an instant in time associated with an event early in the life cycle of the entry.
     * Typically, will be associated with the initial creation or first availability of the resource.
     * @return a date. May be <code>null</code>.
     * @see #setPublished
     * @castor.field
     *  get-method="getPublished"
     *  set-method="setPublished"
     * @castor.field-xml
     *  name="published"
     *  node="element"
     */
    public Date getPublished()
    {
        return _published;
    }

    /**
     * Initializes the instant in time associated with an event early in the life cycle of the entry.
     * @param published a date. May be <code>null</code>.
     * @see #getPublished
     */
    public void setPublished(final Date published)
    {
        _published = published;
    }

    /**
     * Returns information about rights held in and over this entry.
     * This element should not be used to convey machine-readable licensing information.
     * @return a text container. May be <code>null</code>.
     * @see #setRights
     * @castor.field
     *  get-method="getRights"
     *  set-method="setRights"
     * @castor.field-xml
     *  name="rights"
     *  node="element"
     */
    public TextContainer getRights()
    {
        return _rights;
    }

    /**
     * Initializes information about rights held in and over this entry.
     * @param rights a text container. May be <code>null</code>.
     * @see #getRights
     */
    public void setRights(final TextContainer rights)
    {
        _rights = rights;
    }

    /**
     * Returns the source feed.
     * If an entry is copied from one feed into another feed, then the source feed's metadata (all child elements of the feed element other than the entry elements) may be preserved within the copied entry by adding a source child element, if it is not already present in the entry, and including some or all of the source feed's metadata elements as the source element's children.
     * Such metadata should be preserved if the source feed contains any of the child elements author, contributor, rights, or category and those child elements are not present in the source entry.
     * @return a source feed. May be <code>null</code>.
     * @see #setSource
     * @castor.field
     *  get-method="getSource"
     *  set-method="setSource"
     * @castor.field-xml
     *  name="source"
     *  node="element"
     */
    public Source getSource()
    {
        return _source;
    }

    /**
     * Initializes the source feed.
     * @param source a feed source. May be <code>null</code>.
     * @see #getSource
     */
    public void setSource(final Source source)
    {
        _source = source;
    }

    /**
     * Returns a short summary, abstract, or excerpt of an entry.
     * It is not advisable for this element to duplicate the title or content information because Atom processors might assume there is a useful summary when there is none.
     * @return a text container. May be <code>null</code>.
     * @see #setSummary
     * @castor.field
     *  get-method="getSummary"
     *  set-method="setSummary"
     * @castor.field-xml
     *  name="summary"
     *  node="element"
     */
    public TextContainer getSummary()
    {
        return _summary;
    }

    /**
     * Initializes a short summary, abstract, or excerpt of an entry.
     * @param summary a text container. May be <code>null</code>.
     * @see #getSummary
     */
    public void setSummary(final TextContainer summary)
    {
        _summary = summary;
    }

    /**
     * Returns a human-readable title for this entry.
     * @return a text container. May be <code>null</code> if not yet initialized.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     *  required="true"
     * @castor.field-xml
     *  name="title"
     *  node="element"
     */
    public TextContainer getTitle()
    {
        return _title;
    }

    /**
     * Initializes the human-readable title for this entry.
     * @param title a text container. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>title</code> is <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final TextContainer title)
    {
        if (title == null)
        {
            throw new NullPointerException("no title");
        }

        _title = title;
    }

    /**
     * Returns the most recent instant in time when this entry was modified in a way the publisher considers significant.
     * Therefore, not all modifications necessarily result in a changed updated value.
     * @return a date. May be <code>null</code> if not yet initialized.
     * @see #setUpdated
     * @castor.field
     *  get-method="getUpdated"
     *  set-method="setUpdated"
     *  required="true"
     * @castor.field-xml
     *  name="updated"
     *  node="element"
     */
    public Date getUpdated()
    {
        return _updated;
    }

    /**
     * Initializes the most recent instant in time when this entry was modified in a way the publisher considers significant.
     * @param updated a date. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>updated</code> is <code>null</code>.
     * @see #getUpdated
     */
    public void setUpdated(final Date updated)
    {
        if (updated == null)
        {
            throw new NullPointerException("no updated date");
        }

        _updated = updated;
    }
}
