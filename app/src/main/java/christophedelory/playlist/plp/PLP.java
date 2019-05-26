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
package christophedelory.playlist.plp;

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
 * Music playlist for a SanDisk Sansa portable media player.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @since 0.2.0
 */
public class PLP implements SpecificPlaylist
{
    /**
     * The provider of this specific playlist.
     */
    private transient SpecificPlaylistProvider _provider = null;

    /**
     * The list of song file names.
     */
    private final List<String> _filenames = new ArrayList<String>();

    /**
     * The disk specifier of the playlist.
     */
    private String _diskSpecifier = "HARP";

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
            enc = "UTF-16LE";
        }

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, enc)); // Throws NullPointerException if out is null. May throw UnsupportedEncodingException.

        writer.write("PLP PLAYLIST"); // May throw IOException.
        writer.write('\r'); // May throw IOException.
        writer.write('\n'); // May throw IOException.
        writer.write("VERSION 1.20"); // May throw IOException.
        writer.write('\r'); // May throw IOException.
        writer.write('\n'); // May throw IOException.
        writer.write('\r'); // May throw IOException.
        writer.write('\n'); // May throw IOException.

        for (String filename : _filenames)
        {
            writer.write(_diskSpecifier); // May throw IOException.
            writer.write(", "); // May throw IOException.
            writer.write(filename); // May throw IOException.
            writer.write('\r'); // May throw IOException.
            writer.write('\n'); // May throw IOException.
        }

        writer.flush(); // May throw IOException.
    }

    @Override
    public Playlist toPlaylist()
    {
        final Playlist ret = new Playlist();

        for (String filename : _filenames)
        {
            final Media media = new Media(); // NOPMD Avoid instantiating new objects inside loops
            final Content content = new Content(filename); // NOPMD Avoid instantiating new objects inside loops
            media.setSource(content);

            ret.getRootSequence().addComponent(media);
        }

        ret.normalize();

        return ret;
    }

    /**
     * Returns the list of song file names.
     * @return a list of file names. May be empty but not <code>null</code>.
     */
    public List<String> getFilenames()
    {
        return _filenames;
    }

    /**
     * Returns the disk specifier of this playlist.
     * Defaults to "<code>HARP</code>".
     * @return a disk specifier. Must not be <code>null</code>.
     * @see #setDiskSpecifier
     */
    public String getDiskSpecifier()
    {
        return _diskSpecifier;
    }

    /**
     * Initializes the disk specifier of this playlist.
     * @param diskSpecifier a disk specifier. Must not be <code>null</code>.
     * @throws NullPointerException if <code>diskSpecifier</code> is <code>null</code>.
     * @see #getDiskSpecifier
     */
    public void setDiskSpecifier(final String diskSpecifier)
    {
        _diskSpecifier = diskSpecifier.trim(); // Throws NullPointerException if disk is null.
    }
}
