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
package christophedelory.playlist.pla;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import mychristophedelory.content.Content;
import christophedelory.playlist.Media;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;

/**
 * iRiver T-series or S-series-compatible playlist.
 * @version $Revision: 91 $
 * @author Christophe Delory
 * @since 0.2.0
 */
public class PLA implements SpecificPlaylist
{
    /**
     * The provider of this specific playlist.
     */
    private transient SpecificPlaylistProvider _provider = null;

    /**
     * The list of song file names.
     */
    private final List<String> _filenames = new ArrayList<String>();

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
        byte[] array = new byte[512];
        Arrays.fill(array, (byte) 0);
        array[4] = 'i';
        array[5] = 'r';
        array[6] = 'i';
        array[7] = 'v';
        array[8] = 'e';
        array[9] = 'r';
        array[10] = ' ';
        array[11] = 'U';
        array[12] = 'M';
        array[13] = 'S';
        array[14] = ' ';
        array[15] = 'P';
        array[16] = 'L';
        array[17] = 'A';

        final int nbSongs = _filenames.size();
        array[3] = (byte)((nbSongs & 0x000000ff) >> 0);
        array[2] = (byte)((nbSongs & 0x0000ff00) >> 8);
        array[1] = (byte)((nbSongs & 0x00ff0000) >> 16);
        array[0] = (byte)((nbSongs & 0xff000000) >> 24);

        out.write(array); // Throws NullPointerException if out is null. May throw IOException.

        for (String filename : _filenames)
        {
            Arrays.fill(array, (byte) 0);

            final int slashIndex = filename.lastIndexOf('/'); // May equal -1.
            final int antislashIndex = filename.lastIndexOf('\\'); // May equal -1.
            int fileIndex = 0; // Default case if none is found.

            if (slashIndex > antislashIndex) // And thus is greater or equal to 0.
            {
                fileIndex = slashIndex + 1;
            }
            else if (antislashIndex > slashIndex) // And thus is greater or equal to 0.
            {
                fileIndex = antislashIndex + 1;
            }

            // File index is one-based.
            fileIndex++;
            array[1] = (byte)((fileIndex & 0x000000ff) >> 0);
            array[0] = (byte)((fileIndex & 0x0000ff00) >> 8);

            final byte[] tmp = filename.getBytes("UTF-16BE"); // Shall not throw UnsupportedEncodingException.
            System.arraycopy(tmp, 0, array, 2, tmp.length); // May throw IndexOutOfBoundsException. Shall not throw ArrayStoreException, NullPointerException.

            out.write(array); // May throw IOException.
        }

        out.flush(); // May throw IOException.
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
}
