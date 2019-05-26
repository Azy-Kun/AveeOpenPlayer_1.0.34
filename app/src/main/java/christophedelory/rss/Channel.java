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

/**
 * Contains information about the channel (metadata) and its contents.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="channel"
 */
public class Channel extends BaseMedia // I don't want regular RSS item to directly extend the group element (Group), as they do not share the same namespace.
{
    /**
     * The name of the channel.
     */
    private String _title = null;

    /**
     * The URL to the HTML website corresponding to the channel.
     */
    private URI _link = null;

    /**
     * Phrase or sentence describing the channel.
     */
    private String _description = null;

    /**
     * The language the channel is written in.
     */
    private String _language = null;

    /**
     * Copyright notice for content in the channel.
     */
    private String _copyright = null;

    /**
     * Email address for person responsible for editorial content.
     */
    private String _managingEditor = null;

    /**
     * Email address for person responsible for technical issues relating to channel.
     */
    private String _webMaster = null;

    /**
     * The publication date for the content in the channel.
     */
    private Date _pubDate = null;

    /**
     * The last time the content of the channel changed.
     */
    private Date _lastBuildDate = null;

    /**
     * Specify one or more categories that the channel belongs to.
     */
    private final List<Category> _categories = new ArrayList<Category>();

    /**
     * A string indicating the program used to generate the channel.
     */
    private String _generator = null;

    /**
     * An URL that points to the documentation for the format used in the RSS file.
     */
    private String _docs = "http://blogs.law.harvard.edu/tech/rss";

    /**
     * Allows processes to register with a cloud to be notified of updates to the channel, implementing a lightweight publish-subscribe protocol for RSS feeds.
     */
    private Cloud _cloud = null;

    /**
     * It's a number of minutes that indicates how long a channel can be cached before refreshing from the source.
     */
    private Integer _ttl = null;

    /**
     * Specifies a GIF, JPEG or PNG image that can be displayed with the channel.
     */
    private Image _image = null;

    /**
     * The <a href="http://www.w3.org/PICS/">PICS</a> rating for the channel.
     */
    private String _rating = null;

    /**
     * Specifies a text input box that can be displayed with the channel.
     */
    private TextInput _textInput = null;

    /**
     * A hint for aggregators telling them which hours they can skip.
     */
    private final List<Integer> _skipHours = new ArrayList<Integer>();

    /**
     * A hint for aggregators telling them which days they can skip.
     */
    private final List<String> _skipDays = new ArrayList<String>();

    /**
     * A channel may contain any number of {@link Item items}.
     */
    private final List<Item> _items = new ArrayList<Item>();

    /**
     * A reference to the top-level RSS document.
     */
    private transient RSS _rss = null;

    /**
     * Returns the name of the channel.
     * It's how people refer to your service.
     * If you have an HTML website that contains the same information as your RSS file,
     * the title of your channel should be the same as the title of your website.
     * Example: "GoUpstate.com News Headlines".
     * No default value.
     * @return the channel's title. May be <code>null</code> if not yet initialized.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     *  required="true"
     * @castor.field-xml
     *  name="title"
     *  node="element"
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Initializes the name of the channel.
     * @param title the channel's title. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>title</code> is <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = title.trim(); // Throws NullPointerException if title is null.
    }

    /**
     * Initializes the URL to the HTML website corresponding to the channel.
     * @param link an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getLinkString
     * @see #setLink
     */
    public void setLinkString(final String link) throws URISyntaxException
    {
        _link = new URI(link); // May throw NullPointerException, URISyntaxException.
    }

    /**
     * Returns the URL to the HTML website corresponding to the channel.
     * Example: "http://www.goupstate.com/".
     * No default value.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL has been defined for this channel.
     * @see #setLinkString
     * @see #getLink
     * @castor.field
     *  get-method="getLinkString"
     *  set-method="setLinkString"
     *  required="true"
     * @castor.field-xml
     *  name="link"
     *  node="element"
     */
    public String getLinkString()
    {
        return _link.toString(); // Throws NullPointerException if _link is null.
    }

    /**
     * Initializes the URL to the HTML website corresponding to the channel.
     * @param link an URL. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @see #getLink
     * @see #setLinkString
     */
    public void setLink(final URI link)
    {
        if (link == null)
        {
            throw new NullPointerException("No link");
        }

        _link = link;
    }

    /**
     * Returns the URL to the HTML website corresponding to the channel.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setLink
     * @see #getLinkString
     */
    public URI getLink()
    {
        return _link;
    }

    /**
     * Returns the phrase or sentence describing the channel.
     * Example: "The latest news from GoUpstate.com, a Spartanburg Herald-Journal Web site".
     * No default value.
     * @return the channel's description. May be <code>null</code> if not yet initialized.
     * @see #setDescription
     * @castor.field
     *  get-method="getDescription"
     *  set-method="setDescription"
     *  required="true"
     * @castor.field-xml
     *  name="description"
     *  node="element"
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Initializes the phrase or sentence describing the channel.
     * @param description the channel's description. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>description</code> is <code>null</code>.
     * @see #getDescription
     */
    public void setDescription(final String description)
    {
        _description = description.trim(); // Throws NullPointerException if description is null.
    }

    /**
     * Returns the language the channel is written in.
     * This allows aggregators to group all Italian language sites, for example, on a single page.
     * A list of allowable values for this element, as provided by Netscape, is <a href="http://cyber.law.harvard.edu/rss/languages.html">here</a>.
     * You may also use <a href="http://www.w3.org/TR/REC-html40/struct/dirlang.html#langcodes">values defined</a> by the W3C.
     * In 0.91, the language element is required.
     * In 0.92 it is optional.
     * Why? If a channel is formed from various input sources there's no way to know that it's going to only contain a single language.
     * Example: "en-us".
     * No default value.
     * @return a language. May be <code>null</code>.
     * @see #setLanguage
     * @castor.field
     *  get-method="getLanguage"
     *  set-method="setLanguage"
     * @castor.field-xml
     *  name="language"
     *  node="element"
     */
    public String getLanguage()
    {
        return _language;
    }

    /**
     * Initializes the language the channel is written in.
     * @param language a language. May be <code>null</code>.
     * @see #getLanguage
     */
    public void setLanguage(final String language)
    {
        _language = language;
    }

    /**
     * Returns the copyright notice for content in the channel.
     * Example: "Copyright 2002, Spartanburg Herald-Journal".
     * No default value.
     * @return a copyright notice. May be <code>null</code>.
     * @see #setCopyright
     * @castor.field
     *  get-method="getCopyright"
     *  set-method="setCopyright"
     * @castor.field-xml
     *  name="copyright"
     *  node="element"
     */
    public String getCopyright()
    {
        return _copyright;
    }

    /**
     * Initializes the copyright notice for content in the channel.
     * @param copyright a copyright notice. May be <code>null</code>.
     * @see #getCopyright
     */
    public void setCopyright(final String copyright)
    {
        _copyright = copyright;
    }

    /**
     * Returns the email address for person responsible for editorial content.
     * Example: "geo@herald.com (George Matesky)".
     * No default value.
     * @return the managing editor email. May be <code>null</code>.
     * @see #setManagingEditor
     * @castor.field
     *  get-method="getManagingEditor"
     *  set-method="setManagingEditor"
     * @castor.field-xml
     *  name="managingEditor"
     *  node="element"
     */
    public String getManagingEditor()
    {
        return _managingEditor;
    }

    /**
     * Initializes the email address for person responsible for editorial content.
     * @param managingEditor the managing editor email. May be <code>null</code>.
     * @see #getManagingEditor
     */
    public void setManagingEditor(final String managingEditor)
    {
        _managingEditor = managingEditor;
    }

    /**
     * Returns the email address for person responsible for technical issues relating to channel.
     * Example: "betty@herald.com (Betty Guernsey)".
     * No default value.
     * @return the Web master email. May be <code>null</code>.
     * @see #setWebMaster
     * @castor.field
     *  get-method="getWebMaster"
     *  set-method="setWebMaster"
     * @castor.field-xml
     *  name="webMaster"
     *  node="element"
     */
    public String getWebMaster()
    {
        return _webMaster;
    }

    /**
     * Initializes the email address for person responsible for technical issues relating to channel.
     * @param webMaster the Web master email. May be <code>null</code>.
     * @see #getWebMaster
     */
    public void setWebMaster(final String webMaster)
    {
        _webMaster = webMaster;
    }

    /**
     * Initializes the publication date for the content in the channel.
     * @param pubDate a date as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>pubDate</code> is <code>null</code>.
     * @see #getPubDateString
     * @see #setPubDate
     */
    public void setPubDateString(final String pubDate)
    {
        _pubDate = RFC822.valueOf(pubDate); // Throws NullPointerException if pubDate is null.
    }

    /**
     * Returns the publication date for the content in the channel.
     * @return a date as a string. May be <code>null</code>.
     * @see #setPubDateString
     * @see #getPubDate
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
     * Initializes the publication date for the content in the channel.
     * @param pubDate a date. May be <code>null</code>.
     * @see #getPubDate
     * @see #setPubDateString
     */
    public void setPubDate(final Date pubDate)
    {
        _pubDate = pubDate;
    }

    /**
     * Returns the publication date for the content in the channel.
     * For example, the New York Times publishes on a daily basis, the publication date flips once every 24 hours.
     * That's when the pubDate of the channel changes.
     * All date-times in RSS conform to the Date and Time Specification of <a href="http://asg.web.cmu.edu/rfc/rfc822.html">RFC 822</a>,
     * with the exception that the year may be expressed with two characters or four characters (four preferred).
     * Example: "Sat, 07 Sep 2002 00:00:01 GMT".
     * No default value.
     * @return a date. May be <code>null</code>.
     * @see #setPubDate
     * @see #getPubDateString
     */
    public Date getPubDate()
    {
        return _pubDate;
    }

    /**
     * Initializes the last time the content of the channel changed.
     * @param lastBuildDate a date as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>lastBuildDate</code> is <code>null</code>.
     * @see #getLastBuildDateString
     * @see #setLastBuildDate
     */
    public void setLastBuildDateString(final String lastBuildDate)
    {
        _lastBuildDate = RFC822.valueOf(lastBuildDate); // Throws NullPointerException if lastBuildDate is null.
    }

    /**
     * Returns the last time the content of the channel changed.
     * @return a date as a string. May be <code>null</code>.
     * @see #setLastBuildDateString
     * @see #getLastBuildDate
     * @castor.field
     *  get-method="getLastBuildDateString"
     *  set-method="setLastBuildDateString"
     * @castor.field-xml
     *  name="lastBuildDate"
     *  node="element"
     */
    public String getLastBuildDateString()
    {
        String ret = null;

        if (_lastBuildDate != null)
        {
            ret = RFC822.toString(_lastBuildDate);
        }

        return ret;
    }

    /**
     * Initializes the last time the content of the channel changed.
     * @param lastBuildDate a date. May be <code>null</code>.
     * @see #getLastBuildDate
     * @see #setLastBuildDateString
     */
    public void setLastBuildDate(final Date lastBuildDate)
    {
        _lastBuildDate = lastBuildDate;
    }

    /**
     * Returns the last time the content of the channel changed.
     * Example: "Sat, 07 Sep 2002 09:42:31 GMT".
     * No default value.
     * @return a date. May be <code>null</code>.
     * @see #setLastBuildDate
     * @see #getLastBuildDateString
     */
    public Date getLastBuildDate()
    {
        return _lastBuildDate;
    }

    /**
     * Returns a list of one or more categories that the channel belongs to.
     * In RSS 2.0, a provision is made for linking a channel to its identifier in a cataloging system, using the category feature.
     * For example, to link a channel to its Syndic8 identifier,
     * include a category element as a sub-element of {@link Channel}, with domain "Syndic8",
     * and value the identifier for your channel in the Syndic8 database.
     * The appropriate category element for Scripting News would be &lt;category domain="Syndic8"&gt;1765&lt;/category&gt;.
     * Example: "Newspapers".
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
     * Specifies a category that the channel belongs to.
     * @param category a category. Shall not be <code>null</code>.
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
     * Initializes the string indicating the program used to generate the channel.
     * @param generator the channel's generator. May be <code>null</code>.
     * @see #getGenerator
     * @since 2.0
     */
    public void setGenerator(final String generator)
    {
        _generator = generator;
    }

    /**
     * Returns a string indicating the program used to generate the channel.
     * Example: "MightyInHouse Content System v2.3".
     * No default value.
     * @return the channel's generator. May be <code>null</code>.
     * @see #setGenerator
     * @since 2.0
     * @castor.field
     *  get-method="getGenerator"
     *  set-method="setGenerator"
     * @castor.field-xml
     *  name="generator"
     *  node="element"
     */
    public String getGenerator()
    {
        return _generator;
    }

    /**
     * Initializes the URL that points to the documentation for the format used in the RSS file.
     * @param docs an URL as a string. May be <code>null</code>.
     * @see #getDocs
     */
    public void setDocs(final String docs)
    {
        _docs = docs;
    }

    /**
     * Returns an URL that points to the documentation for the format used in the RSS file.
     * It's probably a pointer to <a href="http://blogs.law.harvard.edu/tech/rss"></a>.
     * It's for people who might stumble across an RSS file on a Web server 25 years from now and wonder what it is.
     * Example: "http://blogs.law.harvard.edu/tech/rss".
     * Defaults to "http://blogs.law.harvard.edu/tech/rss".
     * @return an URL as a string. May be <code>null</code>.
     * @see #setDocs
     * @castor.field
     *  get-method="getDocs"
     *  set-method="setDocs"
     * @castor.field-xml
     *  name="docs"
     *  node="element"
     */
    public String getDocs()
    {
        return _docs;
    }

    /**
     * Initializes the RSS cloud descriptor.
     * @param cloud a cloud descriptor. May be <code>null</code>.
     * @see #getCloud
     * @since 0.92
     */
    public void setCloud(final Cloud cloud)
    {
        _cloud = cloud;
    }

    /**
     * Returns a RSS cloud descriptor.
     * Allows processes to register with a cloud to be notified of updates to the channel, implementing a lightweight publish-subscribe protocol for RSS feeds.
     * No default value.
     * @return a cloud descriptor. May be <code>null</code>.
     * @see #setCloud
     * @since 0.92
     * @castor.field
     *  get-method="getCloud"
     *  set-method="setCloud"
     * @castor.field-xml
     *  name="cloud"
     *  node="element"
     */
    public Cloud getCloud()
    {
        return _cloud;
    }

    /**
     * Initializes the number of minutes that indicates how long a channel can be cached before refreshing from the source.
     * @param ttl the channel's TTL. May be <code>null</code>.
     * @see #getTTL
     * @since 2.0
     */
    public void setTTL(final Integer ttl)
    {
        _ttl = ttl;
    }

    /**
     * Returns a number of minutes that indicates how long a channel can be cached before refreshing from the source.
     * TTL stands for time to live.
     * This makes it possible for RSS sources to be managed by a file-sharing network such as <a href="http://www.gnutellanews.com/information/what_is_gnutella.shtml">Gnutella</a>.
     * Example: "60".
     * No default value.
     * @return the channel's TTL. May be <code>null</code>.
     * @see #setTTL
     * @since 2.0
     * @castor.field
     *  get-method="getTTL"
     *  set-method="setTTL"
     * @castor.field-xml
     *  name="ttl"
     *  node="element"
     */
    public Integer getTTL()
    {
        return _ttl;
    }

    /**
     * Initializes a GIF, JPEG or PNG image that can be displayed with the channel.
     * @param image an image descriptor. May be <code>null</code>.
     * @see #getImage
     */
    public void setImage(final Image image)
    {
        _image = image;
    }

    /**
     * Returns a GIF, JPEG or PNG image that can be displayed with the channel.
     * No default value.
     * @return an image descriptor. May be <code>null</code>.
     * @see #setImage
     * @castor.field
     *  get-method="getImage"
     *  set-method="setImage"
     * @castor.field-xml
     *  name="image"
     *  node="element"
     */
    public Image getImage()
    {
        return _image;
    }

    /**
     * Initializes the <a href="http://www.w3.org/PICS/">PICS</a> rating for the channel.
     * @param rating a PICS rating. May be <code>null</code>.
     * @see #getRating
     */
    public void setRating(final String rating)
    {
        _rating = rating;
    }

    /**
     * Returns the <a href="http://www.w3.org/PICS/">PICS</a> rating for the channel.
     * No default value.
     * @return a PICS rating. May be <code>null</code>.
     * @see #setRating
     * @castor.field
     *  get-method="getRating"
     *  set-method="setRating"
     * @castor.field-xml
     *  name="rating"
     *  node="element"
     */
    public String getRating()
    {
        return _rating;
    }

    /**
     * Initializes a text input box that can be displayed with the channel.
     * @param textInput the text input descriptor. May be <code>null</code>.
     * @see #getTextInput
     */
    public void setTextInput(final TextInput textInput)
    {
        _textInput = textInput;
    }

    /**
     * Returns a text input box that can be displayed with the channel.
     * No default value.
     * @return the text input descriptor. May be <code>null</code>.
     * @see #setTextInput
     * @castor.field
     *  get-method="getTextInput"
     *  set-method="setTextInput"
     * @castor.field-xml
     *  name="textInput"
     *  node="element"
     */
    public TextInput getTextInput()
    {
        return _textInput;
    }

    /**
     * Returns a hint for aggregators telling them which hours they can skip.
     * Contains up to 24 "hour" sub-elements whose value is a number between 0 and 23, representing a time in GMT,
     * when aggregators, if they support the feature, may not read the channel on hours listed in the skipHours element.
     * The hour beginning at midnight is hour zero.
     * The list is initially empty.
     * <br>
     * This element came from scriptingNews format, designed in late 1997, and adopted by Netscape in RSS 0.91 in the spring of 1999.
     * @return a list of "hour" elements. May be empty but not <code>null</code>.
     * @see #addSkipHour
     * @castor.field
     *  get-method="getSkipHours"
     *  set-method="addSkipHour"
     *  type="java.lang.Integer"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="hour"
     *  location="skipHours"
     *  node="element"
     */
    public List<Integer> getSkipHours()
    {
        return _skipHours;
    }

    /**
     * Adds an "hour" sub-element which value is a number between 0 and 23, representing a time in GMT.
     * @param skipHour an "hour" element. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>skipHour</code> is <code>null</code>.
     * @see #getSkipHours
     */
    public void addSkipHour(final Integer skipHour)
    {
        if (skipHour == null)
        {
            throw new NullPointerException("no skipHour");
        }

        _skipHours.add(skipHour);
    }

    /**
     * Returns a hint for aggregators telling them which days they can skip.
     * Contains up to seven "day" sub-elements whose value is Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday.
     * Aggregators may not read the channel during days listed in the skipDays element.
     * No default value.
     * The list is initially empty.
     * <br>
     * This element came from scriptingNews format, designed in late 1997, and adopted by Netscape in RSS 0.91 in the spring of 1999.
     * @return a list of "day" elements. May be empty but not <code>null</code>.
     * @see #addSkipDay
     * @castor.field
     *  get-method="getSkipDays"
     *  set-method="addSkipDay"
     *  type="java.lang.String"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="day"
     *  location="skipDays"
     *  node="element"
     */
    public List<String> getSkipDays()
    {
        return _skipDays;
    }

    /**
     * Adds a "day" sub-element which value is Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday.
     * @param skipDay a "day" element. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>skipDay</code> is <code>null</code>.
     * @see #getSkipDays
     */
    public void addSkipDay(final String skipDay)
    {
        if (skipDay == null)
        {
            throw new NullPointerException("no skipDay");
        }

        _skipDays.add(skipDay);
    }

    /**
     * Returns the list of channel's {@link Item items}.
     * There can be no more than 15 items in a 0.91 channel (not checked).
     * There are no XML-level limits in RSS 0.92 and greater.
     * Processors may impose their own limits, and generators may have preferences that say no more than a certain number of {@link Item items} can appear in a channel.
     * The list is initially empty.
     * @return a list of items. May be empty but not <code>null</code>.
     * @see #addItem
     * @castor.field
     *  get-method="getItems"
     *  set-method="addItem"
     *  type="christophedelory.rss.Item"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="item"
     *  node="element"
     */
    public List<Item> getItems()
    {
        return _items;
    }

    /**
     * Adds an {@link Item item} to this channel.
     * @param item a channel's item. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>item</code> is <code>null</code>.
     * @see #getItems
     */
    public void addItem(final Item item)
    {
        item.setChannel(this); // Throws NullPointerException if item is null.
        _items.add(item);
    }

    /**
     * Initializes the parent RSS document.
     * @param rss the parent RSS element. May be <code>null</code>.
     * @see #getRSS
     */
    void setRSS(final RSS rss)
    {
        _rss = rss;
    }

    /**
     * Returns the parent RSS document, if any.
     * @return the parent RSS document. May be <code>null</code>.
     */
    public RSS getRSS()
    {
        return _rss;
    }
}
