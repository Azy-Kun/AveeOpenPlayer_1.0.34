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

/**
 * Specifies a GIF, JPEG or PNG image that can be displayed with the channel.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="image"
 */
public class Image
{
    /**
     * The URL of a GIF, JPEG or PNG image that represents the channel.
     */
    private URI _url = null;

    /**
     * Describes the image, it's used in the ALT attribute of the HTML &lt;img&gt; tag when the channel is rendered in HTML.
     */
    private String _title = null;

    /**
     * The URL of the site, when the channel is rendered, the image is a link to the site.
     */
    private URI _link = null;

    /**
     * Number indicating the width of the image in pixels.
     */
    private Integer _width = null;

    /**
     * Number indicating the height of the image in pixels.
     */
    private Integer _height = null;

    /**
     * Contains text that is included in the TITLE attribute of the link formed around the image in the HTML rendering.
     */
    private String _description = null;

    /**
     * Initializes the URL of a GIF, JPEG or PNG image that represents the channel.
     * @param url an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getURLString
     * @see #setURL
     */
    public void setURLString(final String url) throws URISyntaxException
    {
        _url = new URI(url); // May throw URISyntaxException. Throws NullPointerException if url is null.
    }

    /**
     * Returns the URL of a GIF, JPEG or PNG image that represents the channel.
     * No default value.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL has been defined in this image description.
     * @see #setURLString
     * @see #getURL
     * @castor.field
     *  get-method="getURLString"
     *  set-method="setURLString"
     *  required="true"
     * @castor.field-xml
     *  name="url"
     *  node="element"
     */
    public String getURLString()
    {
        return _url.toString();
    }

    /**
     * Initializes the URL of a GIF, JPEG or PNG image that represents the channel.
     * @param url an URL. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @see #getURL
     * @see #setURLString
     */
    public void setURL(final URI url)
    {
        if (url == null)
        {
            throw new NullPointerException("No URL");
        }

        _url = url;
    }

    /**
     * Returns the URL of a GIF, JPEG or PNG image that represents the channel.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * Describes the image, it's used in the ALT attribute of the HTML &lt;img&gt; tag when the channel is rendered in HTML.
     * @param title the image's title. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>title</code> is <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = title.trim(); // Throws NullPointerException if title is null.
    }

    /**
     * Describes the image, it's used in the ALT attribute of the HTML &lt;img&gt; tag when the channel is rendered in HTML.
     * No default value.
     * @return the image's title. May be <code>null</code> if not yet initialized.
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
     * Returns the URL of the site, when the channel is rendered, the image is a link to the site.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no link has been defined in this image description.
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
     * Initializes the URL of the site, when the channel is rendered, the image is a link to the site.
     * @param link an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getLinkString
     * @see #setLink
     */
    public void setLinkString(final String link) throws URISyntaxException
    {
        _link = new URI(link); // May throw URISyntaxException. Throws NullPointerException if link is null.
    }

    /**
     * Initializes the URL of the site, when the channel is rendered, the image is a link to the site.
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
     * Returns the URL of the site, when the channel is rendered, the image is a link to the site.
     * Note, in practice the image title and link should have the same value as the channel's title and link.
     * No default value.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setLink
     * @see #getLinkString
     */
    public URI getLink()
    {
        return _link;
    }

    /**
     * Returns the number indicating the width of the image in pixels.
     * Maximum value is 144 (not checked).
     * If not specified, defaults to 88.
     * @return a width. May be <code>null</code>.
     * @see #setWidth
     * @castor.field
     *  get-method="getWidth"
     *  set-method="setWidth"
     * @castor.field-xml
     *  name="width"
     *  node="element"
     */
    public Integer getWidth()
    {
        return _width;
    }

    /**
     * Initializes the number indicating the width of the image in pixels.
     * @param width a width. May be <code>null</code>.
     * @see #getWidth
     */
    public void setWidth(final Integer width)
    {
        _width = width;
    }

    /**
     * Returns the number indicating the height of the image in pixels.
     * Maximum value is 400 (not checked).
     * If not specified, defaults to 31.
     * @return a height. May be <code>null</code>.
     * @see #setHeight
     * @castor.field
     *  get-method="getHeight"
     *  set-method="setHeight"
     * @castor.field-xml
     *  name="height"
     *  node="element"
     */
    public Integer getHeight()
    {
        return _height;
    }

    /**
     * Initializes the number indicating the height of the image in pixels.
     * @param height a height. May be <code>null</code>.
     * @see #getHeight
     */
    public void setHeight(final Integer height)
    {
        _height = height;
    }

    /**
     * Returns the text that is included in the TITLE attribute of the link formed around the image in the HTML rendering.
     * @return the image's description. May be <code>null</code>.
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
     * Initializes the text that is included in the TITLE attribute of the link formed around the image in the HTML rendering.
     * @param description the image's description. May be <code>null</code>.
     * @see #getDescription
     */
    public void setDescription(final String description)
    {
        _description = description;
    }
}
