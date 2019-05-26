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
 * This element is designed to allow the aggregation of entries from different feeds while retaining information about an entry's source feed.
 * For this reason, Atom processors that are performing such aggregation should include at least the required feed-level metadata elements (id, title, and updated) in the source element.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="source" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Source extends Common
{
    /**
     * The authors of the feed.
     */
    private final List<Person> _authors = new ArrayList<Person>();

    /**
     * The categories associated with the feed.
     */
    private final List<Category> _categories = new ArrayList<Category>();

    /**
     * The list of persons or other entities who contributed to the feed.
     */
    private final List<Person> _contributors = new ArrayList<Person>();

    /**
     * The identification of the agent used to generate the feed.
     */
    private Generator _generator = null;

    /**
     * An URI that identifies an image that provides iconic visual identification for this feed.
     */
    private URIContainer _icon = null;

    /**
     * A permanent, universally unique identifier for the feed.
     */
    private URIContainer _id = null;

    /**
     * The list of references from this feed to a Web resource.
     */
    private final List<Link> _links = new ArrayList<Link>();

    /**
     * The URI reference that identifies an image that provides visual identification for this feed.
     */
    private URIContainer _logo = null;

    /**
     * Information about rights held in and over this feed.
     */
    private TextContainer _rights = null;

    /**
     * A human-readable description or subtitle for this feed.
     */
    private TextContainer _subtitle = null;

    /**
     * A human-readable title for this feed.
     */
    private TextContainer _title = null;

    /**
     * The most recent instant in time when this feed was modified in a way the publisher considers significant.
     */
    private Date _updated = null;

    /**
     * Returns the list of feed's authors.
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
     * Adds a feed's author.
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
     * Returns the list of categories associated with the feed.
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
     * Adds a category to this feed.
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
     * Returns the list of persons or other entities who contributed to the feed.
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
     * Adds a person or other entity who contributed to the feed.
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
     * Returns the identification of the agent used to generate the feed.
     * @return an agent identification. May be <code>null</code>.
     * @see #setGenerator
     * @castor.field
     *  get-method="getGenerator"
     *  set-method="setGenerator"
     * @castor.field-xml
     *  name="generator"
     *  node="element"
     */
    public Generator getGenerator()
    {
        return _generator;
    }

    /**
     * Initializes the identification of the agent used to generate the feed.
     * @param generator an agent identification. May be <code>null</code>.
     * @see #getGenerator
     */
    public void setGenerator(final Generator generator)
    {
        _generator = generator;
    }

    /**
     * Returns the URI that identifies an image that provides iconic visual identification for this feed.
     * The image should have an aspect ratio of one (horizontal) to one (vertical) and should be suitable for presentation at a small size.
     * @return an URI container. May be <code>null</code>.
     * @see #setIcon
     * @castor.field
     *  get-method="getIcon"
     *  set-method="setIcon"
     * @castor.field-xml
     *  name="icon"
     *  node="element"
     */
    public URIContainer getIcon()
    {
        return _icon;
    }

    /**
     * Initializes the URI that identifies an image that provides iconic visual identification for this feed.
     * @param icon an URI container. May be <code>null</code>.
     * @see #getIcon
     */
    public void setIcon(final URIContainer icon)
    {
        _icon = icon;
    }

    /**
     * Returns a permanent, universally unique identifier for the feed.
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
     * Initializes the permanent, universally unique identifier for the feed.
     * @param id an URI container. May be <code>null</code>.
     * @see #getId
     */
    public void setId(final URIContainer id)
    {
        _id = id;
    }

    /**
     * Returns the list of references from this feed to a Web resource.
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
     * Adds a reference from this feed to a Web resource.
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
     * Returns an URI reference that identifies an image that provides visual identification for this feed.
     * The image should have an aspect ratio of 2 (horizontal) to 1 (vertical).
     * @return an URI container. May be <code>null</code>.
     * @see #setLogo
     * @castor.field
     *  get-method="getLogo"
     *  set-method="setLogo"
     * @castor.field-xml
     *  name="logo"
     *  node="element"
     */
    public URIContainer getLogo()
    {
        return _logo;
    }

    /**
     * Initializes the URI reference that identifies an image that provides visual identification for this feed.
     * @param logo an URI container. May be <code>null</code>.
     * @see #getLogo
     */
    public void setLogo(final URIContainer logo)
    {
        _logo = logo;
    }

    /**
     * Returns information about rights held in and over this feed.
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
     * Initializes information about rights held in and over this feed.
     * @param rights a text container. May be <code>null</code>.
     * @see #getRights
     */
    public void setRights(final TextContainer rights)
    {
        _rights = rights;
    }

    /**
     * Returns a human-readable description or subtitle for this feed.
     * @return a text container. May be <code>null</code>.
     * @see #setSubtitle
     * @castor.field
     *  get-method="getSubtitle"
     *  set-method="setSubtitle"
     * @castor.field-xml
     *  name="subtitle"
     *  node="element"
     */
    public TextContainer getSubtitle()
    {
        return _subtitle;
    }

    /**
     * Initializes a human-readable description or subtitle for this feed.
     * @param subtitle a text container. May be <code>null</code>.
     * @see #getSubtitle
     */
    public void setSubtitle(final TextContainer subtitle)
    {
        _subtitle = subtitle;
    }

    /**
     * Returns a human-readable title for this feed.
     * @return a text container. May be <code>null</code>.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     * @castor.field-xml
     *  name="title"
     *  node="element"
     */
    public TextContainer getTitle()
    {
        return _title;
    }

    /**
     * Initializes the human-readable title for this feed.
     * @param title a text container. May be <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final TextContainer title)
    {
        _title = title;
    }

    /**
     * Returns the most recent instant in time when this feed was modified in a way the publisher considers significant.
     * Therefore, not all modifications necessarily result in a changed updated value.
     * @return a date. May be <code>null</code>.
     * @see #setUpdated
     * @castor.field
     *  get-method="getUpdated"
     *  set-method="setUpdated"
     * @castor.field-xml
     *  name="updated"
     *  node="element"
     */
    public Date getUpdated()
    {
        return _updated;
    }

    /**
     * Initializes the most recent instant in time when this feed was modified in a way the publisher considers significant.
     * @param updated a date. May be <code>null</code>.
     * @see #getUpdated
     */
    public void setUpdated(final Date updated)
    {
        _updated = updated;
    }
}
