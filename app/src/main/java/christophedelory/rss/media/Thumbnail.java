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
 * Image to represent the media.
 * Allows particular images to be used as representative images for the media object.
 * If multiple thumbnails are included, and time coding is not at play, it is assumed that the images are in order of importance.
 * It is possible to use an optional "time" attribute to change the image as the media plays.
 * <br>
 * Example:
 * <pre>
 * &lt;media:thumbnail url="http://www.foo.com/keyframe.jpg" width="75" height="50" time="12:05:01.123"/&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:thumbnail" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Thumbnail
{
    /**
     * The URL of the thumbnail.
     */
    private URI _url = null;

    /**
     * Integer representing the value in pixels for the width of the thumbnail.
     */
    private Integer _width = null;

    /**
     * Integer representing the value in pixels for the height of the thumbnail.
     */
    private Integer _height = null;

    /**
     * The time offset in relation to the media object.
     */
    private String _time = null;

    /**
     * Initializes the URL of the thumbnail as a string.
     * @param url an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getURLString
     * @see #setURL
     */
    public void setURLString(final String url) throws URISyntaxException
    {
        _url = new URI(url); // Throws NullPointerException if url is null. May throw URISyntaxException.
    }

    /**
     * Returns the URL of the thumbnail as a string.
     * No default value.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL is defined in this thumbnail.
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
        return _url.toString(); // Throws NullPointerException if _url is null.
    }

    /**
     * Initializes the URL of the thumbnail.
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
     * Returns the URL of the thumbnail.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * Initializes the integer representing the value in pixels for the width of the thumbnail.
     * @param width a thumbnail width.
     * @see #getWidth
     * @see #setWidth(Integer)
     */
    public void setWidth(final int width)
    {
        _width = Integer.valueOf(width);
    }

    /**
     * Initializes the integer representing the value in pixels for the width of the thumbnail.
     * @param width a thumbnail width. May be <code>null</code>.
     * @see #getWidth
     * @see #setWidth(int)
     */
    public void setWidth(final Integer width)
    {
        _width = width;
    }

    /**
     * Returns the integer representing the value in pixels for the width of the thumbnail.
     * No default value.
     * @return a thumbnail width. May be <code>null</code>.
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
     * Initializes the integer representing the value in pixels for the height of the thumbnail.
     * @param height a thumbnail height.
     * @see #setHeight(Integer)
     * @see #getHeight
     */
    public void setHeight(final int height)
    {
        _height = Integer.valueOf(height);
    }

    /**
     * Initializes the integer representing the value in pixels for the height of the thumbnail.
     * @param height a thumbnail height. May be <code>null</code>.
     * @see #getHeight
     * @see #setHeight(int)
     */
    public void setHeight(final Integer height)
    {
        _height = height;
    }

    /**
     * Returns the integer representing the value in pixels for the height of the thumbnail.
     * No default value.
     * @return a thumbnail height. May be <code>null</code>.
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

    /**
     * Returns the time offset in relation to the media object.
     * Typically this is used when creating multiple keyframes within a single video.
     * The format for this attribute should be in the DSM-CC's Normal Play Time (NTP) as used in RTSP
     * [<a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a>].
     * <br>
     * <u>Notes</u>:
     * <br>
     * NTP has a second or subsecond resolution.
     * It is specified as H:M:S.h (npt-hhmmss) or S.h (npt-sec), where H=hours, M=minutes, S=second and h=fractions of a second.
     * <br>
     * A possible alternative to NTP would be SMPTE.
     * It is believed that NTP is simpler and easier to use.
     * @return a time offset. May be <code>null</code>.
     * @see #setTime
     * @castor.field
     *  get-method="getTime"
     *  set-method="setTime"
     * @castor.field-xml
     *  name="time"
     *  node="attribute"
     */
    public String getTime()
    {
        return _time;
    }

    /**
     * Initializes the time offset in relation to the media object.
     * @param time a time offset. May be <code>null</code>.
     * @see #getTime
     */
    public void setTime(final String time)
    {
        _time = time;
    }
}
