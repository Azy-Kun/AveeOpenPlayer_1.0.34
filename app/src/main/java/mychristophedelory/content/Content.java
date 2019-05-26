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
package mychristophedelory.content;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The definition of a content and its metadata.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Content
{
    /**
     * The content URL, as a string.
     */
    private final String _urlString;

    /**
     * The content URI.
     */
    private transient URI _uri = null;

    /**
     * The content URL.
     */
    private transient URL _url = null;

    /**
     * The content encoding of the resource that the URL references.
     */
    private String _encoding = null;

    /**
     * The content length of the resource that the URL references.
     */
    private long _length = -1L;

    /**
     * The content type of the resource that the URL references.
     */
    private String _type = null;

    /**
     * The date the resource that the URL references was last modified.
     */
    private long _lastModified = 0L;

    /**
     * The content duration in milliseconds.
     */
    private long _duration = -1L;

    /**
     * The content width in pixels.
     */
    private int _width = -1;

    /**
     * The content height in pixels.
     */
    private int _height = -1;

    /**
     * Specifies if we already connected to the URL or not.
     */
    private transient Boolean _connected = null;

    /**
     * Builds a new content from the specified URL.
     * @param url an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     */
    public Content(final String url)
    {
        _urlString = url.trim().replace('\\', '/'); // Throws NullPointerException if url is null.
    }

    /**
     * Builds a new content from the specified URI.
     * @param uri an URI. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>uri</code> is <code>null</code>.
     */
    public Content(final URI uri)
    {
        _uri = uri.normalize(); // Throws NullPointerException if uri is null.
        _urlString = uri.toString();
    }

    /**
     * Builds a new content from the specified URL.
     * @param url an URL. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     */
    public Content(final URL url)
    {
        _urlString = url.toString(); // Throws NullPointerException if url is null.
        _url = url;
    }

    /**
     * Returns the URI of the content.
     * @return an URI. Shall not be <code>null</code>.
     * @throws SecurityException if a required system property value cannot be accessed.
     * @throws URISyntaxException if this URL is not formatted strictly according to to RFC2396 and cannot be converted to a URI.
     * @see #getURL
     * @see #toString
     */
    public URI getURI() throws URISyntaxException
    {
        synchronized(this)
        {
            if (_uri == null)
            {
                URI uri = null;

                if (_url == null)
                {
                    try
                    {
                        uri = new URI(_urlString); // May throw URISyntaxException.
                    }
                    catch (URISyntaxException e)
                    {
                        uri = null;
                    }

                    if ((uri == null) || !uri.isAbsolute())
                    {
                        uri = new File(_urlString).toURI(); // May throw SecurityException. Shall not throw NullPointerException because of _urlString.
                    }
                }
                else
                {
                    uri = _url.toURI(); // May throw URISyntaxException.
                }

                _uri = uri.normalize();
            }
        }

        return _uri;
    }

    /**
     * Returns the URL of the content.
     * @return an URL. Shall not be <code>null</code>.
     * @throws SecurityException if a required system property value cannot be accessed.
     * @throws IllegalArgumentException if the URL is not absolute.
     * @throws MalformedURLException if a protocol handler for the URL could not be found, or if some other error occurred while constructing the URL.
     * @see #getURI
     * @see #toString
     */
    public URL getURL() throws MalformedURLException
    {
        synchronized(this)
        {
            if (_url == null)
            {
                try
                {
                    _url = new URL(_urlString); // May throw MalformedURLException.
                }
                catch (MalformedURLException e)
                {
                    _uri = new File(_urlString).toURI().normalize(); // May throw SecurityException.
                    _url = _uri.toURL(); // May throw IllegalArgumentException, MalformedURLException.
                }
            }
        }

        return _url;
    }

    /**
     * Returns the content encoding of the resource that the URL references.
     * @return a content encoding, or <code>null</code> if unknown.
     * @see #setEncoding
     */
    public String getEncoding()
    {
        return _encoding;
    }

    /**
     * Initializes or overrides the content encoding.
     * @param encoding a content encoding. May be <code>null</code>.
     * @see #getEncoding
     */
    public void setEncoding(final String encoding)
    {
        _encoding = encoding;
    }

    /**
     * Returns the content length in bytes of the resource that the URL references.
     * @return a content length, or <code>-1L</code> if unknown.
     * @see #setLength
     */
    public long getLength()
    {
        return _length;
    }

    /**
     * Initializes or overrides the content length, in bytes.
     * @param length a content length. May be negative.
     * @see #getLength
     */
    public void setLength(final long length)
    {
        _length = length;
    }

    /**
     * Returns the content type of the resource that the URL references.
     * @return a content type, or <code>null</code> if unknown.
     * @see #setType
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Initializes or overrides the content type.
     * @param type a content type. May be <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type;
    }

    /**
     * Returns the date the resource that the URL references was last modified.
     * The value is the number of milliseconds since January 1, 1970 GMT.
     * @return a date, or <code>0L</code> if unknown.
     * @see #setLastModified
     */
    public long getLastModified()
    {
        return _lastModified;
    }

    /**
     * Initializes or overrides the last modified date.
     * @param lastModified a date. May be negative or null.
     * @see #getLastModified
     */
    public void setLastModified(final long lastModified)
    {
        _lastModified = lastModified;
    }

    /**
     * Returns the content duration in milliseconds.
     * @return a duration value. May be negative if unknown.
     * @see #setDuration
     */
    public long getDuration()
    {
        return _duration;
    }

    /**
     * Initializes the content duration in milliseconds.
     * @param duration a duration value. May be negative if unknown.
     * @see #getDuration
     */
    public void setDuration(final long duration)
    {
        _duration = duration;
    }

    /**
     * Returns the content width in pixels.
     * @return a width, or <code>-1</code> if unknown.
     * @since 1.0.0
     * @see #setWidth
     */
    public int getWidth()
    {
        return _width;
    }

    /**
     * Initializes or overrides the content width, in pixels.
     * @param width a width. May be negative if unknown.
     * @since 1.0.0
     * @see #getWidth
     */
    public void setWidth(final int width)
    {
        _width = width;
    }

    /**
     * Returns the content height in pixels.
     * @return an height, or <code>-1</code> if unknown.
     * @since 1.0.0
     * @see #setHeight
     */
    public int getHeight()
    {
        return _height;
    }

    /**
     * Initializes or overrides the content height, in pixels.
     * @param height an height. May be negative if unknown.
     * @since 1.0.0
     * @see #getHeight
     */
    public void setHeight(final int height)
    {
        _height = height;
    }

    /**
     * Specifies if the last connection to the content through its URL has been successful or not.
     * If <code>true</code>, the content metadata may be pertinent.
     * @return <code>true</code> if the content has been successfully accessed through its URL, <code>false</code> otherwise.
     * @see #connect
     */
    public boolean isValid()
    {
        return (_connected == null) ? false : _connected.booleanValue();
    }

    /**
     * Connects to the specified URL, if not already done.
     * @throws SecurityException if a required system property value cannot be accessed.
     * @throws IllegalArgumentException if the URL is not absolute.
     * @throws MalformedURLException if a protocol handler for the URL could not be found, or if some other error occurred while constructing the URL.
     * @throws IOException if any I/O error occurs.
     * @throws //SocketTimeoutException if the timeout expires before the connection can be established.
     * @see #getURL
     */
    public void connect() throws IOException
    {
        boolean connect = false;

        synchronized(this)
        {
            if (_connected == null)
            {
                _connected = Boolean.FALSE;
                connect = true;
            }
        }

        if (connect)
        {
            final URL url = getURL(); // May throw MalformedURLException, IllegalArgumentException, SecurityException.

            // The connection object is created by invoking the openConnection method on a URL.
            final URLConnection conn = url.openConnection(); // May throw IOException.

            // The setup parameters and general request properties are manipulated.
            conn.setAllowUserInteraction(false); // Shall not throw IllegalStateException.
            conn.setDoInput(true); // Shall not throw IllegalStateException.
            conn.setDoOutput(false); // Shall not throw IllegalStateException.
            //conn.setIfModifiedSince(0L); // Shall not throw IllegalStateException.
            conn.setUseCaches(true); // Shall not throw IllegalStateException.
            //conn.setRequestProperty(key, value); // Shall not throw IllegalStateException, NullPointerException because of key.
            //conn.setConnectTimeout(20000); // Shall not throw IllegalArgumentException.
            //conn.setReadTimeout(20000); // Shall not throw IllegalArgumentException.

            // The actual connection to the remote object is made, using the connect method.
            conn.connect(); // May throw SocketTimeoutException, IOException.

            // The remote object becomes available.
            // The header fields and the contents of the remote object can be accessed.
            //Object content = conn.getContent(); // May throw IOException, UnknownServiceException.
            //InputStream in = conn.getInputStream(); // May throw IOException, UnknownServiceException.
            //conn.getHeaderField();
            final String encoding = conn.getContentEncoding(); // May be null.
            final long length = (long) conn.getContentLength(); // May be negative.
            final String type = conn.getContentType(); // May be null.
            final long lastModified = conn.getLastModified(); // 0L or more.

            // Override the metadata only if pertinent.
            if (encoding != null)
            {
                _encoding = encoding;
            }

            if (length >= 0L)
            {
                _length = length;
            }

            if ((type != null) && !"content/unknown".equals(type))
            {
                _type = type;
            }

            if (lastModified > 0L)
            {
                _lastModified = lastModified;
            }

            _connected = Boolean.TRUE;
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two objects are the same if their string representations are the same.
     * Examples of valid instances for <code>obj</code>: <code>Content</code>, <code>URL</code>, <code>URI</code>, <code>String</code>, etc.
     * @param obj the reference object with which to compare. May be <code>null</code>.
     * @return <code>true</code> if this object is the same as the <code>obj</code> argument; <code>false</code> otherwise.
     * @see #toString
     */
    @Override
    public boolean equals(final Object obj)
    {
        return (obj == null) ? false : _urlString.equals(obj.toString());
    }

    /**
     * Returns a hash code value for this object.
     * This method is supported for the benefit of hashtables such as those provided by {@link java.util.Hashtable}.
     * @return a hash code value for this object.
     * @since 1.0.0
     */
    @Override
    public int hashCode()
    {
        return _urlString.hashCode();
    }

    /**
     * Returns the content URL as a string.
     * @return an URL as a string.
     * @see #getURL
     * @see #getURI
     */
    @Override
    public String toString()
    {
        return _urlString;
    }
}
