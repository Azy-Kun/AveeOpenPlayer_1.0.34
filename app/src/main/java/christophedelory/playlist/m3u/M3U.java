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
package christophedelory.playlist.m3u;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import mychristophedelory.content.Content;
import christophedelory.playlist.Media;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;

/**
 * A M3U, M4U, or Real Audio Metadata playlist.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class M3U implements SpecificPlaylist
{
    /**
     * The provider of this specific playlist.
     */
    private transient SpecificPlaylistProvider _provider = null;

    /**
     * The list of child resources.
     */
    private final List<Resource> _resources = new ArrayList<Resource>();

    /**
     * <code>true</code> if the marshalled playlist must be an Extension M3U, <code>false</code> otherwise.
     */
    private boolean _extensionM3U = false;

    @Override
    public void setProvider(final SpecificPlaylistProvider provider)
    {
        _provider = provider;
    }

    @Override
    public SpecificPlaylistProvider getProvider()
    {
        return _provider;
    }

    @Override
    public void writeTo(final OutputStream out, final String encoding) throws Exception
    {
        String enc = encoding;

        if (enc == null)
        {
            enc = "UTF-8"; // For the M3U8 case. FIXME US-ASCII?
        }

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, enc)); // Throws NullPointerException if out is null. May throw UnsupportedEncodingException.

        if (_extensionM3U)
        {
            writer.write("#EXTM3U"); // May throw IOException.
            writer.newLine(); // May throw IOException.
        }

        for (Resource resource : _resources)
        {
            if (_extensionM3U)
            {
                writer.write("#EXTINF:"); // May throw IOException.
                writer.write(Long.toString(resource.getLength())); // May throw IOException.
                writer.write(","); // May throw IOException.

                if (resource.getName() == null)
                {
                    writer.write(resource.getLocation()); // May throw NullPointerException, IOException.
                }
                else
                {
                    writer.write(resource.getName()); // May throw IOException.
                }

                writer.newLine(); // May throw IOException.
            }

            writer.write(resource.getLocation()); // May throw NullPointerException, IOException.
            writer.newLine(); // May throw IOException.
        }

        writer.flush(); // May throw IOException.
    }

    @Override
    public Playlist toPlaylist()
    {
        final Playlist ret = new Playlist();

        for (Resource resource : _resources)
        {
            if (resource.getLocation() != null)
            {
                final Media media = new Media(); // NOPMD Avoid instantiating new objects inside loops
                final Content content = new Content(resource.getLocation()); // NOPMD Avoid instantiating new objects inside loops
                media.setSource(content);
                content.setDuration(resource.getLength() * 1000L);
                ret.getRootSequence().addComponent(media);
            }
        }

        ret.normalize();

        return ret;
    }

    /**
     * Specifies if the marshalled playlist must have the Extension M3U format or not.
     * @param extensionM3U <code>true</code> if the marshalled playlist must be an Extension M3U, <code>false</code> otherwise.
     * @see #isExtensionM3U
     */
    public void setExtensionM3U(final boolean extensionM3U)
    {
        _extensionM3U = extensionM3U;
    }

    /**
     * Specifies if the marshalled playlist must have the Extension M3U format or not.
     * Defaults to <code>false</code>.
     * @return <code>true</code> if the marshalled playlist must be an Extension M3U, <code>false</code> otherwise.
     * @see #setExtensionM3U
     */
    public boolean isExtensionM3U()
    {
        return _extensionM3U;
    }

    /**
     * Returns the list of playlist resources.
     * @return a list of child resources. May be empty but not <code>null</code>.
     */
    public List<Resource> getResources()
    {
        return _resources;
    }
}
