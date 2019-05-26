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
 * Copyright information for media object.
 * <br>
 * Example:
 * <pre>
 * &lt;media:copyright url="http://blah.com/additional-info.html"&gt;2005 FooBar Media&lt;/media:copyright&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:copyright" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Copyright
{
    /**
     * The URL for a terms of use page or additional copyright information.
     */
    private URI _url = null;

    /**
     * The copyright information.
     */
    private String _value = null;

    /**
     * Initializes the URL for a terms of use page or additional copyright information, as a string.
     * @param url an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getURLString
     * @see #setURL
     */
    public void setURLString(final String url) throws URISyntaxException
    {
        _url = new URI(url); // May throw NullPointerException, URISyntaxException.
    }

    /**
     * Returns the URL for a terms of use page or additional copyright information, as a string.
     * If the media is operating under a Creative Commons license, the Creative Commons module should be used instead.
     * @return an URL as a string. May be <code>null</code>.
     * @see #setURLString
     * @see #getURL
     * @castor.field
     *  get-method="getURLString"
     *  set-method="setURLString"
     * @castor.field-xml
     *  name="url"
     *  node="attribute"
     */
    public String getURLString()
    {
        String ret = null;

        if (_url != null)
        {
            ret = _url.toString();
        }

        return ret;
    }

    /**
     * Initializes the URL for a terms of use page or additional copyright information.
     * @param url an URL. May be <code>null</code>.
     * @see #getURL
     * @see #setURLString
     */
    public void setURL(final URI url)
    {
        _url = url;
    }

    /**
     * Returns the URL for a terms of use page or additional copyright information.
     * @return an URL. May be <code>null</code>.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * 
     * @return the copyright information. May be <code>null</code> if not yet initialized.
     * @see #setValue
     * @castor.field
     *  get-method="getValue"
     *  set-method="setValue"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * 
     * @param value the copyright information. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }
}
