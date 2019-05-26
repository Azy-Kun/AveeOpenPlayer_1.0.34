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
package christophedelory.rss.media;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Allows the media object to be accessed through a web browser media player console.
 * Required only if a direct media url attribute is not specified in the media content element.
 * <br>
 * Example:
 * <pre>
 * &lt;media:player url="http://www.foo.com/player?id=1111" height="200" width="400"/&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:player" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Player
{
    /**
     * The URL of the player console that plays the media.
     */
    private URI _url = null;

    /**
     * Integer representing the value in pixels for the width of the video in this item.
     */
    private Integer _width = null;

    /**
     * Integer representing the value in pixels for the height of the video in this item.
     */
    private Integer _height = null;

    /**
     * Initializes the URL of the player console that plays the media, as a string.
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
     * Returns the URL of the player console that plays the media, as a string.
     * No default value.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL has been defined in this element.
     * @see #setURLString
     * @see #getURL
     * @castor.field
     *  get-method="getURLString"
     *  set-method="setURLString"
     *  required="true"
     * @castor.field-xml
     *  name="url"
     *  node="attribute"
     */
    public String getURLString()
    {
        return _url.toString();
    }

    /**
     * Initializes the URL of the player console that plays the media.
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
     * Returns the URL of the player console that plays the media.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * Initializes the integer representing the value in pixels for the width of the video in this item.
     * @param width a video width.
     * @see #getWidth
     * @see #setWidth(Integer)
     */
    public void setWidth(final int width)
    {
        _width = Integer.valueOf(width);
    }

    /**
     * Initializes the integer representing the value in pixels for the width of the video in this item.
     * @param width a video width. May be <code>null</code>.
     * @see #getWidth
     * @see #setWidth(int)
     */
    public void setWidth(final Integer width)
    {
        _width = width;
    }

    /**
     * Returns the integer representing the value in pixels for the width of the video in this item.
     * It is the width of the browser window that the URL should be opened in.
     * No default value.
     * @return a video width. May be <code>null</code>.
     * @see #setWidth
     * @castor.field
     *  get-method="getWidth"
     *  set-method="setWidth"
     * @castor.field-xml
     *  name="width"
     *  node="attribute"
     */
    public Integer getWidth()
    {
        return _width;
    }

    /**
     * Initializes the integer representing the value in pixels for the height of the video in this item.
     * @param height a video height.
     * @see #setHeight(Integer)
     * @see #getHeight
     */
    public void setHeight(final int height)
    {
        _height = Integer.valueOf(height);
    }

    /**
     * Initializes the integer representing the value in pixels for the height of the video in this item.
     * @param height a video height. May be <code>null</code>.
     * @see #getHeight
     * @see #setHeight(int)
     */
    public void setHeight(final Integer height)
    {
        _height = height;
    }

    /**
     * Returns the integer representing the value in pixels for the height of the video in this item.
     * It is the height of the browser window that the URL should be opened in.
     * No default value.
     * @return a video height. May be <code>null</code>.
     * @see #setHeight
     * @castor.field
     *  get-method="getHeight"
     *  set-method="setHeight"
     * @castor.field-xml
     *  name="height"
     *  node="attribute"
     */
    public Integer getHeight()
    {
        return _height;
    }
}
