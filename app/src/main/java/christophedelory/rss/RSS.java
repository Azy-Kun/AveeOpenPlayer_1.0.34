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

/**
 * A RSS document.
 * RSS is a Web content syndication format.
 * Its name is an acronym for <i>Really Simple Syndication</i>.
 * See the <a href="http://cyber.law.harvard.edu/rss/rss.html">RSS 2.0 specification</a>.
 * In RSS 0.91 various elements are restricted to 500 or 100 characters.
 * There are no string-length or XML-level limits in RSS 0.92.
 * Processors may impose their own limits, and generators may have preferences that say no more than a certain number of items can appear in a channel, or that strings are limited in length.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="rss"
 */
public class RSS
{
    /**
     * The RSS version 2.0 identifier.
     */
    public static final String VERSION_2_0 = "2.0";

    /**
     * The RSS version 0.92 identifier.
     */
    public static final String VERSION_0_92 = "0.92";

    /**
     * The RSS version 0.91 identifier.
     */
    public static final String VERSION_0_91 = "0.91";

    /**
     * Contains information about the channel (metadata) and its contents.
     */
    private Channel _channel = new Channel();

    /**
     * Specifies the version of RSS that this document conforms to.
     */
    private String _version = VERSION_2_0;

    /**
     * Builds a new and empty RSS feed.
     */
    public RSS()
    {
        _channel.setRSS(this);
    }

    /**
     * Returns the version of RSS that this document conforms to.
     * Defaults to {@link #VERSION_2_0}.
     * @return a version number. Shall not be <code>null</code>.
     * @see #setVersion
     * @castor.field
     *  get-method="getVersion"
     *  set-method="setVersion"
     *  required="true"
     * @castor.field-xml
     *  name="version"
     *  node="attribute"
     */
    public String getVersion()
    {
        return _version;
    }

    /**
     * Initializes the version of RSS that this document conforms to.
     * @param version a version number. SHall not be <code>null</code>.
     * @throws NullPointerException if <code>version</code> is <code>null</code>.
     * @see #getVersion
     * @see #VERSION_0_91
     * @see #VERSION_0_92
     * @see #VERSION_2_0
     */
    public void setVersion(final String version)
    {
        _version = version.trim(); // Throws NullPointerException if version is null.
    }

    /**
     * Returns the channel assigned to this RSS document.
     * Contains information about the channel (metadata) and its contents.
     * Defaults to an empty channel.
     * @return a RSS channel. Shall not be <code>null</code>.
     * @see #setChannel
     * @castor.field
     *  get-method="getChannel"
     *  set-method="setChannel"
     *  required="true"
     * @castor.field-xml
     *  name="channel"
     *  node="element"
     */
    public Channel getChannel()
    {
        return _channel;
    }

    /**
     * Assigns the specified channel to this RSS document.
     * @param channel a RSS channel. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>channel</code> is <code>null</code>.
     * @see #getChannel
     */
    public void setChannel(final Channel channel)
    {
        channel.setRSS(this); // Throws NullPointerException if channel is null.
        _channel = channel;
    }
}
