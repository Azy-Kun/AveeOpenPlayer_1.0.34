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
package christophedelory.rss;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import christophedelory.rss.media.BaseMedia;
import christophedelory.rss.media.Content;
import christophedelory.rss.media.Group;

/**
 * An item may represent a "story" -- much like a story in a newspaper or magazine;
 * if so its description is a synopsis of the story, and the link points to the full story.
 * An item may also be complete in itself, if so, the description contains the text (entity-encoded HTML is allowed), and the link and title may be omitted.
 * All elements of an item are optional, however at least one of title or description must be present.
 * <br>
 * <u>RSS Media note</u>:
 * <br>
 * While both media contents and media groups have no limitations on the number of times they can appear,
 * the general nature of RSS should be preserved:
 * an item represents a "story".
 * Simply stated, this is similar to the blog style of syndication.
 * However, if one is using this module to strictly publish media, there should be one item element for each media object/group.
 * This is to allow for proper attribution for the origination of the media content through the link element.
 * It also allows the full benefit of the other RSS elements to be realized.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="item"
 */
public class Item extends BaseMedia // I don't want regular RSS item to directly extend the group element (Group), as they do not share the same namespace.
{
    /**
     * The title of the item.
     */
    private String _title = null;

    /**
     * The URL of the item.
     */
    private URI _link = null;

    /**
     * The item synopsis.
     */
    private String _description = null;

    /**
     * Email address of the author of the item.
     */
    private String _author = null;

    /**
     * Includes the item in one or more categories.
     */
    private final List<Category> _categories = new ArrayList<Category>();

    /**
     * URL of a page for comments relating to the item.
     */
    private String _comments = null;

    /**
     * Describes a media object that is attached to the item.
     */
    private Enclosure _enclosure = null;

    /**
     * A string that uniquely identifies the item.
     */
    private GUID _guid = null;

    /**
     * Indicates when the item was published.
     */
    private Date _pubDate = null;

    /**
     * The RSS channel that the item came from.
     */
    private Source _source = null;

    /**
     * A list of media contents.
     */
    private final List<Content> _mediaContents = new ArrayList<Content>();

    /**
     * A list of media groups.
     */
    private final List<Group> _mediaGroups = new ArrayList<Group>();

    /**
     * The parent channel element.
     */
    private transient Channel _channel = null;

    /**
     * Returns the title of the item.
     * In 0.91, title is a required sub-element of item.
     * In 0.92, all sub-elements of item are optional.
     * Why? When a RSS file reflects the content of a weblog or "blog" site,
     * the structure required by previous versions of RSS was often impossible to synthesize.
     * For example, there is no actual limit on the number of links a weblog item can have.
     * Example: "Venice Film Festival Tries to Quit Sinking".
     * No default value.
     * @return the item's title. May be <code>null</code>.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     * @castor.field-xml
     *  name="title"
     *  node="element"
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Initializes the title of the item.
     * @param title the item's title. May be <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = title;
    }

    /**
     * Initializes the URL of the item.
     * @param link an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getLinkString
     * @see #setLink
     */
    public void setLinkString(final String link) throws URISyntaxException
    {
        _link = new URI(link); // Throws NullPointerException if link is null. May throw URISyntaxException.
    }

    /**
     * Returns the URL of the item.
     * @return an URL as a string. May be <code>null</code>.
     * @see #setLinkString
     * @see #getLink
     * @castor.field
     *  get-method="getLinkString"
     *  set-method="setLinkString"
     * @castor.field-xml
     *  name="link"
     *  node="element"
     */
    public String getLinkString()
    {
        String ret = null;

        if (_link != null)
        {
            ret = _link.toString();
        }

        return ret;
    }

    /**
     * Initializes the URL of the item.
     * @param link an URL. May be <code>null</code>.
     * @see #getLink
     * @see #setLinkString
     */
    public void setLink(final URI link)
    {
        _link = link;
    }

    /**
     * Returns the URL of the item.
     * In 0.91, link is a required sub-element of item.
     * In 0.92, all sub-elements of item are optional.
     * Why? When a RSS file reflects the content of a weblog or "blog" site,
     * the structure required by previous versions of RSS was often impossible to synthesize.
     * For example, there is no actual limit on the number of links a weblog item can have.
     * Example: "http://nytimes.com/2004/12/07FEST.html".
     * No default value.
     * @return an URL. May be <code>null</code>.
     * @see #setLink
     * @see #getLinkString
     */
    public URI getLink()
    {
        return _link;
    }

    /**
     * Returns the item synopsis.
     * 0.92 allows entity-encoded HTML in the description of an item, to reflect actual practice by bloggers, who are often proficient HTML coders.
     * Example: "Some of the most heated chatter at the Venice Film Festival this week was about the way that the arrival of the stars at the Palazzo del Cinema was being staged.".
     * No default value.
     * @return the item's description. May be <code>null</code>.
     * @see #setDescription
     * @castor.field
     *  get-method="getDescription"
     *  set-method="setDescription"
     * @castor.field-xml
     *  name="description"
     *  node="element"
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Initializes the item synopsis.
     * @param description the item's description. May be <code>null</code>.
     * @see #getDescription
     */
    public void setDescription(final String description)
    {
        _description = description;
    }

    /**
     * Returns the email address of the author of the item.
     * For newspapers and magazines syndicating via RSS, the author is the person who wrote the article that the item describes.
     * For collaborative weblogs, the author of the item might be different from the managing editor or webmaster.
     * For a weblog authored by a single individual it would make sense to omit the element.
     * Example: "lawyer@boyer.net (Lawyer Boyer)".
     * No default value.
     * @return the author's email. May be <code>null</code>.
     * @see #setAuthor
     * @since 2.0
     * @castor.field
     *  get-method="getAuthor"
     *  set-method="setAuthor"
     * @castor.field-xml
     *  name="author"
     *  node="element"
     */
    public String getAuthor()
    {
        return _author;
    }

    /**
     * Initializes the email address of the author of the item.
     * @param author the author's email. May be <code>null</code>.
     * @see #getAuthor
     * @since 2.0
     */
    public void setAuthor(final String author)
    {
        _author = author;
    }

    /**
     * Returns a list of one or more item's categories.
     * You may include as many category elements as you need to, for different domains,
     * and to have an item cross-referenced in different parts of the same domain.
     * The list is initially empty.
     * @return a list of categories. May be empty but not <code>null</code>.
     * @see #addCategory
     * @since 0.92
     * @castor.field
     *  get-method="getCategories"
     *  set-method="addCategory"
     *  type="christophedelory.rss.Category"
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
     * Includes the item in a category.
     * @param category an item's category. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>category</code> is <code>null</code>.
     * @see #getCategories
     * @since 0.92
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
     * Returns an URL of a page for comments relating to the item.
     * If present, it is the url of the comments page for the item.
     * Example: "http://ekzemplo.com/entry/4403/comments".
     * More about comments <a href="http://backend.userland.com/weblogComments">here</a>.
     * No default value.
     * @return an URL as a string. May be <code>null</code>.
     * @see #setComments
     * @since 2.0
     * @castor.field
     *  get-method="getComments"
     *  set-method="setComments"
     * @castor.field-xml
     *  name="comments"
     *  node="element"
     */
    public String getComments()
    {
        return _comments;
    }

    /**
     * Initializes an URL of a page for comments relating to the item.
     * @param comments an URL as a string. May be <code>null</code>.
     * @see #getComments
     * @since 2.0
     */
    public void setComments(final String comments)
    {
        _comments = comments;
    }

    /**
     * Describes a media object that is attached to the item.
     * No default value.
     * @return a media descriptor. May be <code>null</code>.
     * @see #setEnclosure
     * @since 0.92
     * @castor.field
     *  get-method="getEnclosure"
     *  set-method="setEnclosure"
     * @castor.field-xml
     *  name="enclosure"
     *  node="element"
     */
    public Enclosure getEnclosure()
    {
        return _enclosure;
    }

    /**
     * Describes a media object that is attached to the item.
     * @param enclosure a media descriptor. May be <code>null</code>.
     * @see #getEnclosure
     * @since 0.92
     */
    public void setEnclosure(final Enclosure enclosure)
    {
        _enclosure = enclosure;
    }

    /**
     * Returns a string that uniquely identifies the item.
     * A frequently asked question about guids is how do they compare to links.
     * Aren't they the same thing?
     * Yes, in some content systems, and no in others.
     * In some systems, link is a permalink to a weblog item.
     * However, in other systems, each item is a synopsis of a longer article, link points to the article, and guid is the permalink to the weblog entry.
     * In all cases, it's recommended that you provide the guid, and if possible make it a permalink.
     * This enables aggregators to not repeat items, even if there have been editing changes.
     * No default value.
     * @return a GUID. May be <code>null</code>.
     * @see #setGuid
     * @since 2.0
     * @castor.field
     *  get-method="getGuid"
     *  set-method="setGuid"
     * @castor.field-xml
     *  name="guid"
     *  node="element"
     */
    public GUID getGuid()
    {
        return _guid;
    }

    /**
     * Initializes a string that uniquely identifies the item.
     * @param guid a GUID. May be <code>null</code>.
     * @see #getGuid
     * @since 2.0
     */
    public void setGuid(final GUID guid)
    {
        _guid = guid;
    }

    /**
     * Indicates when the item was published.
     * @param pubDate a date as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>pubDate</code> is <code>null</code>.
     * @see #getPubDateString
     * @see #setPubDate
     * @since 2.0
     */
    public void setPubDateString(final String pubDate)
    {
        _pubDate = RFC822.valueOf(pubDate); // Throws NullPointerException if pubDate is null.
    }

    /**
     * Indicates when the item was published.
     * @return a date as a string. May be <code>null</code>.
     * @see #setPubDateString
     * @see #getPubDate
     * @since 2.0
     * @castor.field
     *  get-method="getPubDateString"
     *  set-method="setPubDateString"
     * @castor.field-xml
     *  name="pubDate"
     *  node="element"
     */
    public String getPubDateString()
    {
        String ret = null;

        if (_pubDate != null)
        {
            ret = RFC822.toString(_pubDate);
        }

        return ret;
    }

    /**
     * Indicates when the item was published.
     * Its value is a <a href="http://asg.web.cmu.edu/rfc/rfc822.html">date</a>, indicating when the item was published.
     * If it's a date in the future, aggregators may choose to not display the item until that date.
     * Example: "Sun, 19 May 2002 15:21:36 GMT".
     * No default value.
     * @return a date. May be <code>null</code>.
     * @see #setPubDate
     * @see #getPubDateString
     * @since 2.0
     */
    public Date getPubDate()
    {
        return _pubDate;
    }

    /**
     * Indicates when the item was published.
     * @param pubDate a date. May be <code>null</code>.
     * @see #getPubDate
     * @see #setPubDateString
     * @since 2.0
     */
    public void setPubDate(final Date pubDate)
    {
        _pubDate = pubDate;
    }

    /**
     * Returns the RSS channel that the item came from.
     * No default value.
     * @return the item's source. May be <code>null</code>.
     * @see #setSource
     * @since 0.92
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
     * Initializes the RSS channel that the item came from.
     * @param source the item's source. May be <code>null</code>.
     * @see #getSource
     * @since 0.92
     */
    public void setSource(final Source source)
    {
        _source = source;
    }

	/**
	 * Adds a media content.
     * @param mediaContent a media content. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaContent</code> is <code>null</code>.
	 * @see #getMediaContents
	 */
	public void addMediaContent(final Content mediaContent)
	{
        if (mediaContent == null)
        {
            throw new NullPointerException("no media content");
        }

		_mediaContents.add(mediaContent);
	}

	/**
	 * Returns the list of media contents.
     * @return a list of media contents. May be empty but not <code>null</code>.
	 * @see #addMediaContent
     * @castor.field
     *  type="christophedelory.rss.media.Content"
     *  get-method="getMediaContents"
     *  set-method="addMediaContent"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:content"
     *  node="element"
	 */
	public List<Content> getMediaContents()
	{
		return _mediaContents;
	}

	/**
     * Returns the list of media groups.
     * @return a list of media groups. May be empty but not <code>null</code>.
	 * @see #addMediaGroup
     * @castor.field
     *  type="christophedelory.rss.media.Group"
     *  get-method="getMediaGroups"
     *  set-method="addMediaGroup"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:group"
     *  node="element"
	 */
	public List<Group> getMediaGroups()
	{
		return _mediaGroups;
	}

	/**
	 * Adds a media group.
     * @param mediaGroup a media group. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaGroup</code> is <code>null</code>.
	 * @see #getMediaGroups
	 */
	public void addMediaGroup(final Group mediaGroup)
	{
        if (mediaGroup == null)
        {
            throw new NullPointerException("no media group");
        }

		_mediaGroups.add(mediaGroup);
	}

    /**
     * Initializes the parent channel.
     * @param channel the parent channel. May be <code>null</code>.
     * @see #getChannel
     */
    void setChannel(final Channel channel)
    {
        _channel = channel;
    }

    /**
     * Returns the parent channel, if any.
     * @return the parent channel. May be <code>null</code>.
     */
    public Channel getChannel()
    {
        return _channel;
    }
}
