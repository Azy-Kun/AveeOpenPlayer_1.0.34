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
 * The RSS channel that the item came from.
 * The purpose of this element is to propagate credit for links, to publicize the sources of news items.
 * It can be used in the Post command of an aggregator.
 * It should be generated automatically when forwarding an item from an aggregator to a weblog authoring tool.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="source"
 */
public class Source
{
    /**
     * Links to the XMLization of the source.
     */
    private URI _url = null;

    /**
     * Name of the RSS channel that the item came from, derived from its title.
     */
    private String _channelName = null;

    /**
     * Initializes the name of the RSS channel that the item came from, derived from its title.
     * @param channelName the channel's name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>channelName</code> is <code>null</code>.
     * @see #getChannelName
     */
    public void setChannelName(final String channelName)
    {
        _channelName = channelName.trim(); // Throws NullPointerException if channelName is null.
    }

    /**
     * Returns the name of the RSS channel that the item came from, derived from its title.
     * Example: "Tomalak's Realm".
     * No default value.
     * @return the channel's name. May be <code>null</code> if not yet initialized.
     * @see #setChannelName
     * @castor.field
     *  get-method="getChannelName"
     *  set-method="setChannelName"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getChannelName()
    {
        return _channelName;
    }

    /**
     * Initializes a link to the XMLization of the source.
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
     * Returns a link to the XMLization of the source.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no URL has been defined in this source element.
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
     * Initializes a link to the XMLization of the source.
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
     * Returns a link to the XMLization of the source.
     * Example: "http://www.tomalak.org/links2.xml".
     * No default value.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }
}
