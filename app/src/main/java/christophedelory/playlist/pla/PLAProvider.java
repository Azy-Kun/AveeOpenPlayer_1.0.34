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

import java.io.InputStream;
import java.util.List;

import org.myapache.commons.logging.Log;

import mychristophedelory.content.type.ContentType;
import christophedelory.player.PlayerSupport;
import christophedelory.playlist.AbstractPlaylistComponent;
import christophedelory.playlist.Media;
import christophedelory.playlist.Parallel;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.Sequence;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;

/**
 * Playlist format for iRiver devices.
 * @version $Revision: 91 $
 * @author Christophe Delory
 * @since 0.2.0
 */
public class PLAProvider implements SpecificPlaylistProvider
{
    /**
     * A list of compatible content types.
     */
    private static final ContentType[] FILETYPES =
    {
        new ContentType(new String[] { ".pla" },
                        new String[] { "application/octet-stream" }, // FIXME
                        new PlayerSupport[]
                        {
                        },
                        "iRiver iQuickList File"),
    };

    @Override
    public String getId()
    {
        return "pla";
    }

    @Override
    public ContentType[] getContentTypes()
    {
        return FILETYPES.clone();
    }

    @Override
    public SpecificPlaylist readFrom(final InputStream in, final String encoding, final Log logger) throws Exception
    {
        PLA ret = new PLA();
        ret.setProvider(this);

        // The playlist format is very simple.
        // A conforming playlist file consists of 1+N null-padded 512-byte frames, where N is the number of songs in the playlist.
        final byte[] array = new byte[512];

        if (in.read(array) != 512) // Throws NullPointerException if in is null. May throw IOException.
        {
            throw new IllegalArgumentException("Not a PLA playlist format (file too small)");
        }

        // First frame is a header starting with a 32-bit big-endian unsigned integer specifying the number of songs in the playlist.
        // Immediately after this there is an ASCII string "iriver UMS PLA", and that's all for the header frame.
        final String magic = new String(array, 4, 14, "US-ASCII"); // Shall not throw UnsupportedEncodingException, IndexOutOfBoundsException.

        if (!"iriver UMS PLA".equals(magic))
        {
            throw new IllegalArgumentException("Not a PLA playlist format (bad magic)");
        }

        // In addition, player's own Quick Lists have an apparently superfluous extra string "Quick List" starting from 0x20
        //magic = new String(array, 32, 10); // May equal "Quick List".

        final int nbSongs =   (((int) array[3] & 0x0ff) << 0) |
                        (((int) array[2] & 0x0ff) << 8) |
                        (((int) array[1] & 0x0ff) << 16) |
                        (((int) array[0] & 0x0ff) << 24);

        for (int i = 0; i < nbSongs; i++)
        {
            if (in.read(array) != 512) // May throw IOException.
            {
                logger.error("Malformed PLA playlist (file too small)");
                ret = null;
                break;
            }

            // Each song frame begins with a 16-bit big-endian unsigned integer
            // specifying the index of the first non-directory character of the song's full filename.
            // This index is one-based.
            // The index is there probably just to help the player to strip the directory part out of the song's filename,
            // if there is no title tag in the song file.
            //int fileIndex = (((int) array[1] & 0x0ff) << 0) | (((int) array[0] & 0x0ff) << 8);

            // Immediately after the index comes the song's null-terminated full filename.
            // As the filesystem type is VFAT, it is encoded as big-endian UTF-16 without a byte order mark.
            // I have not tried whether the player recognizes wider than two-byte characters.
            // Also, I have used only absolute paths, I don't know if relative paths would work.
            final String songFilename = new String(array, 2, 510, "UTF-16BE"); // Shall not throw UnsupportedEncodingException, IndexOutOfBoundsException. NOPMD Avoid instantiating new objects inside loops

            // The index and filename are everything there is in a single song frame.
            // Note that the filename must fit into one 512-byte frame.
            // So the filename, including the directory part, can have at most 255 (two-byte) characters.
            ret.getFilenames().add(songFilename); // Shall not throw UnsupportedOperationException, ClassCastException, NullPointerException, IllegalArgumentException.
        }

        return ret;
    }

    @Override
    public SpecificPlaylist toSpecificPlaylist(final Playlist playlist) throws Exception
    {
        final PLA ret = new PLA();
        ret.setProvider(this);

        addToPlaylist(ret.getFilenames(), playlist.getRootSequence()); // May throw Exception.

        return ret;
    }

    /**
     * Adds the song file names referenced in the specified generic playlist component to the input list.
     * @param filenames the resulting list of file names. Shall not be <code>null</code>.
     * @param component the generic playlist component to handle. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>filenames</code> is <code>null</code>.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @throws Exception if this service provider is unable to represent the input playlist.
     */
    private void addToPlaylist(final List<String> filenames, final AbstractPlaylistComponent component) throws Exception
    {
        if (component instanceof Sequence)
        {
            final Sequence seq = (Sequence) component;

            if (seq.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A PLA playlist cannot handle a sequence repeated indefinitely");
            }

            final AbstractPlaylistComponent[] components = seq.getComponents();

            for (int iter = 0; iter < seq.getRepeatCount(); iter++)
            {
                for (AbstractPlaylistComponent c : components)
                {
                    addToPlaylist(filenames, c); // May throw Exception.
                }
            }
        }
        else if (component instanceof Parallel)
        {
            throw new IllegalArgumentException("A parallel time container is incompatible with a PLA playlist");
        }
        else if (component instanceof Media)
        {
            final Media media = (Media) component;

            if (media.getDuration() != null)
            {
                throw new IllegalArgumentException("A PLA playlist cannot handle a timed media");
            }

            if (media.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A PLA playlist cannot handle a media repeated indefinitely");
            }

            if (media.getSource() != null)
            {
                for (int iter = 0; iter < media.getRepeatCount(); iter++)
                {
                    filenames.add(media.getSource().toString()); // Shall not throw UnsupportedOperationException, ClassCastException, NullPointerException, IllegalArgumentException.
                }
            }
        }
    }
}
