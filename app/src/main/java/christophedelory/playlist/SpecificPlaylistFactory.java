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
package christophedelory.playlist;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import org.myapache.commons.logging.Log;
import mychristophedelory.logging.LogFactory;

import mychristophedelory.content.type.ContentType;

/**
 * The {@link SpecificPlaylist playlist} factory.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public final class SpecificPlaylistFactory
{
    /**
     * The singleton instance.
     */
    private static SpecificPlaylistFactory _instance = null;

    /**
     * Returns the unique class instance.
     * @return an instance of this class. Shall not be <code>null</code>.
     */
    public static SpecificPlaylistFactory getInstance()
    {
        synchronized(SpecificPlaylistFactory.class)
        {
            if (_instance == null)
            {
                _instance = new SpecificPlaylistFactory();
            }
        }

        return _instance;
    }

    /**
     * The associated service providers loader.
     */
    private final ServiceLoader<SpecificPlaylistProvider> _serviceLoader;

    /**
     * The logger attached to this provider factory.
     */
    private final Log _logger;

    /**
     * Builds a new specific playlist factory.
     */
    private SpecificPlaylistFactory()
    {
        _logger = LogFactory.getLog(getClass()); // May throw LogConfigurationException.
        _serviceLoader = ServiceLoader.load(SpecificPlaylistProvider.class);
    }

    /**
     * Refreshes the list of playlist providers managed by this factory.
     * If new providers are added after the instantiation of this factory, you will need to call this method manually.
     */
    public void reloadProviders()
    {
        _serviceLoader.reload();
    }

    /**
     * Reads a playlist from the specified URL.
     * @param url an URL to playlist contents. Shall not be <code>null</code>.
     * @return a new playlist instance, or <code>null</code> if the format has been recognized, but the playlist is malformed.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @throws IOException if an I/O exception occurs.
     * @see SpecificPlaylistProvider#readFrom
     * @see #readFrom(File)
     */
    public SpecificPlaylist readFrom(final URL url) throws IOException
    {
        SpecificPlaylist ret = null;

        for (SpecificPlaylistProvider service : _serviceLoader)
        {
            final URLConnection urlConnection = url.openConnection(); //  Throws NullPointerException if url is null. May throw IOException.
            urlConnection.setAllowUserInteraction(false); // Shall not throw IllegalStateException.
            urlConnection.setConnectTimeout(10000); // Shall not throw IllegalArgumentException.
            urlConnection.setDoInput(true); // Shall not throw IllegalStateException.
            urlConnection.setDoOutput(false); // Shall not throw IllegalStateException.
            urlConnection.setReadTimeout(60000); // Shall not throw IllegalArgumentException.
            urlConnection.setUseCaches(true); // Shall not throw IllegalStateException.

            urlConnection.connect(); // May throw SocketTimeoutException, IOException.

            final String contentEncoding = urlConnection.getContentEncoding(); // May be null.
            //final int contentLength = urlConnection.getContentLength(); // May be negative.
            //final String contentType = urlConnection.getContentType(); // May be null.

            final InputStream in = urlConnection.getInputStream(); // May throw IOException, UnknownServiceException.

            try
            {
                ret = service.readFrom(in, contentEncoding, _logger); // May throw Exception. Shall not throw NullPointerException because of in.
                // Returns it even if null.
                break;
            }
            catch (Exception e)
            {
                // Ignore it.
                if (_logger.isTraceEnabled())
                {
                    _logger.trace("Playlist provider " + service.getId() + " cannot unmarshal <" + url + ">", e);
                }
                else if (_logger.isDebugEnabled())
                {
                    _logger.debug("Playlist provider " + service.getId() + " cannot unmarshal <" + url + ">: " + e);
                }
            }
            finally
            {
                in.close(); // May throw IOException.
            }
        }

        return ret;
    }

    /**
     * Reads a playlist from the specified file instance.
     * @param file an file representing playlist contents. Shall not be <code>null</code>.
     * @return a new playlist instance, or <code>null</code> if the format has been recognized, but the playlist is malformed.
     * @throws NullPointerException if <code>file</code> is <code>null</code>.
     * @throws SecurityException if a required system property value cannot be accessed.
     * @throws IOException if an I/O exception occurs.
     * @see #readFrom(URL)
     */
    public SpecificPlaylist readFrom(final File file) throws IOException
    {
        return readFrom(file.toURI().toURL()); // Throws NullPointerException if file is null. May throw SecurityException, IOException. Shall not throw IllegalArgumentException.
    }

    /**
     * Searches for a provider handling the type of specific playlists identified by the given string.
     * @param id the unique string identifying a type of specific playlists. Not case sensitive. Shall not be <code>null</code>.
     * @return a provider, or <code>null</code> if none was found.
     * @throws NullPointerException if <code>id</code> is <code>null</code>.
     * @see #findProviderByExtension
     */
    public SpecificPlaylistProvider findProviderById(final String id)
    {
        SpecificPlaylistProvider ret = null;

        for (SpecificPlaylistProvider service : _serviceLoader)
        {
            if (id.equalsIgnoreCase(service.getId())) // Throws NullPointerException if id is null.
            {
                ret = service;
                break;
            }
        }

        return ret;
    }

    /**
     * Searches for a provider handling the specific playlist files with the given extension string.
     * @param filename a playlist file name, or a simple extension string (with the leading '.' character, if appropriate). Not case sensitive. Shall not be <code>null</code>.
     * @return a provider, or <code>null</code> if none was found.
     * @throws NullPointerException if <code>filename</code> is <code>null</code>.
     * @see #findProviderById
     */
    public SpecificPlaylistProvider findProviderByExtension(final String filename)
    {
        SpecificPlaylistProvider ret = null;
        final String name = filename.toLowerCase(Locale.ENGLISH); // Throws NullPointerException if filename is null.

        for (SpecificPlaylistProvider service : _serviceLoader)
        {
            final ContentType[] types = service.getContentTypes();

            for (ContentType type : types)
            {
                if (type.matchExtension(name))
                {
                    ret = service;
                    break;
                }
            }

            if (ret != null)
            {
                break;
            }
        }

        return ret;
    }

    /**
     * Lists all currently installed playlist providers.
     * @return a list of specific playlist providers. May be empty but not <code>null</code>.
     * @since 0.2.0
     * @see #findProviderByExtension
     * @see #findProviderById
     */
    public List<SpecificPlaylistProvider> getProviders()
    {
        final ArrayList<SpecificPlaylistProvider> ret = new ArrayList<SpecificPlaylistProvider>();

        for (SpecificPlaylistProvider service : _serviceLoader)
        {
            ret.add(service);
        }

        return ret;
    }
}
