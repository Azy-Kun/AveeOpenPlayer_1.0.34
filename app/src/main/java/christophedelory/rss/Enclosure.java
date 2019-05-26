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
 * Describes a media object that is attached to the item.
 * A use-case narrative for this element is <a href="http://www.thetwowayweb.com/payloadsforrss">here</a>.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="enclosure"
 */
public class Enclosure
{
    /**
     * Says where the enclosure is located.
     */
    private URI _url = null;

    /**
     * Says how big it is in bytes.
     */
    private long _length = 0L;

    /**
     * Says what its type is, a standard MIME type.
     */
    private String _type = "application/octet-stream";

    /**
     * Specifies where the enclosure is located.
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
     * Returns where the enclosure is located.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL has been specified in this enclosure.
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
     * Specifies where the enclosure is located.
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
     * Returns where the enclosure is located.
     * The url must be an HTTP url.
     * Example: "http://www.scripting.com/mp3s/weatherReportSuite.mp3".
     * No default value.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * Specifies how big it is in bytes.
     * @param length a length.
     * @see #getLength
     */
    public void setLength(final long length)
    {
        _length = length;
    }

    /**
     * Returns how big it is in bytes.
     * Example: "12216320".
     * Defaults to 0.
     * @return a length.
     * @see #setLength
     * @castor.field
     *  get-method="getLength"
     *  set-method="setLength"
     *  required="true"
     * @castor.field-xml
     *  name="length"
     *  node="attribute"
     */
    public long getLength()
    {
        return _length;
    }

    /**
     * Says what its type is, a standard MIME type.
     * Example: "audio/mpeg".
     * Defaults to "application/octet-stream".
     * @return a MIME type. Shall not be <code>null</code>.
     * @see #setType
     * @castor.field
     *  get-method="getType"
     *  set-method="setType"
     *  required="true"
     * @castor.field-xml
     *  name="type"
     *  node="attribute"
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Specifies what its type is, a standard MIME type.
     * @param type a MIME type. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>type</code> is <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type.trim(); // Throws NullPointerException if type is null.
    }
}
