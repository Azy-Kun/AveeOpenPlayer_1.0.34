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
package christophedelory.playlist.pls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import christophedelory.playlist.m3u.Resource;

/**
 * A text playlist similar to .ini (program settings) files.
 * It normally contains only the location of the items in the playlist.
 * <br>
 * The syntax of a PLS file is the same syntax as a Windows .ini file and was probably chosen because of support in the Windows API.
 * Metadata is included in the entry for each song, in a set of parallel arrays where FileN=[address of file]; TitleN=[title of song].
 * @version $Revision: 91 $
 * @author Christophe Delory
 */
public class PLSProvider implements SpecificPlaylistProvider
{
    /**
     * A list of compatible content types.
     */
    private static final ContentType[] FILETYPES =
    {
        new ContentType(new String[] { ".pls" },
                        new String[] { "audio/x-scpls" },
                        new PlayerSupport[]
                        {
                            new PlayerSupport(PlayerSupport.Player.WINAMP, true, null),
                            new PlayerSupport(PlayerSupport.Player.VLC_MEDIA_PLAYER, false, null),
                            new PlayerSupport(PlayerSupport.Player.MEDIA_PLAYER_CLASSIC, true, null),
                            new PlayerSupport(PlayerSupport.Player.FOOBAR2000, false, null),
                            new PlayerSupport(PlayerSupport.Player.MPLAYER, true, null),
                            new PlayerSupport(PlayerSupport.Player.QUICKTIME, true, null),
                            new PlayerSupport(PlayerSupport.Player.ITUNES, true, null),
                            new PlayerSupport(PlayerSupport.Player.REALPLAYER, false, null),
                        },
                        "Winamp PLSv2 Playlist"),
    };

    @Override
    public String getId()
    {
        return "pls";
    }

    @Override
    public ContentType[] getContentTypes()
    {
        return FILETYPES.clone();
    }

    @Override
    public SpecificPlaylist readFrom(final InputStream in, final String encoding, final Log logger) throws Exception
    {
        String enc = encoding;

        if (enc == null)
        {
            enc = "UTF-8"; // FIXME US-ASCII?
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, enc)); // Throws NullPointerException if in is null. May throw UnsupportedEncodingException, IOException.

        PLS ret = new PLS();
        ret.setProvider(this);

        String line;
        boolean magicFound = false;
        int numberOfEntries = -1;

        while ((line = reader.readLine()) != null) // May throw IOException.
        {
            line = line.trim();

            if (line.length() > 0)
            {
                // First the PLS marker string.
                if (!magicFound)
                {
                    if (!line.equalsIgnoreCase("[playlist]")) // NOPMD Deeply nested if..then statements are hard to read
                    {
                        throw new IllegalArgumentException("Not a PLS playlist format");
                    }

                    magicFound = true;
                    continue;
                }

                final int idx = line.indexOf('=');

                if (idx <= 0)
                {
                    logger.error("Malformed PLS playlist");
                    ret = null;
                    break;
                }

                String key = line.substring(0, idx).trim().toLowerCase(); // Shall not throw IndexOutOfBoundsException.
                final String value = line.substring(idx + 1).trim(); // Shall not throw IndexOutOfBoundsException.

                if ("numberofentries".equals(key))
                {
                    int tmpValue;
                    
                    try
                    {
                        tmpValue = Integer.parseInt(value); // May throw NumberFormatException.
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error(e.toString());
                        ret = null;
                        break;
                    }

                    if (tmpValue < 0)
                    {
                        logger.warn("Invalid NumberOfEntries in PLS playlist: " + tmpValue);
                        ret = null;
                        break;
                    }

                    // Test if already found.
                    if ((numberOfEntries >= 0) && (numberOfEntries != tmpValue))
                    {
                        logger.error("PLS playlist number of entries already specified with a different value");
                        ret = null;
                        break;
                    }

                    numberOfEntries = tmpValue;
                }
                else if (key.startsWith("file"))
                {
                    key = key.substring(4); // Shall not throw IndexOutOfBoundsException.
                    int resourceIndex;

                    try
                    {
                        resourceIndex = Integer.parseInt(key) - 1; // May throw NumberFormatException.
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error(e.toString());
                        ret = null;
                        break;
                    }

                    // Ensure that the resource list has enough slots.
                    for (int i = ret.getResources().size(); i < (resourceIndex + 1); i++)
                    {
                        ret.getResources().add(new Resource()); // NOPMD Avoid instantiating new objects inside loops
                    }

                    final Resource resource = ret.getResources().get(resourceIndex); // Shall not throw ArrayIndexOutOfBoundsException.
                    resource.setLocation(value);
                }
                // The Title field is optional.
                else if (key.startsWith("title"))
                {
                    key = key.substring(5); // Shall not throw IndexOutOfBoundsException.
                    int resourceIndex;

                    try
                    {
                        resourceIndex = Integer.parseInt(key) - 1; // May throw NumberFormatException.
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error(e.toString());
                        ret = null;
                        break;
                    }

                    // Ensure that the resource list has enough slots.
                    for (int i = ret.getResources().size(); i < (resourceIndex + 1); i++)
                    {
                        ret.getResources().add(new Resource()); // NOPMD Avoid instantiating new objects inside loops
                    }

                    final Resource resource = ret.getResources().get(resourceIndex); // Shall not throw ArrayIndexOutOfBoundsException.
                    resource.setName(value);
                }
                // The Length field is either the length of the recording in seconds or -1 (unspecified or live stream).
                else if (key.startsWith("length"))
                {
                    key = key.substring(6); // Shall not throw IndexOutOfBoundsException.
                    int resourceIndex;

                    try
                    {
                        resourceIndex = Integer.parseInt(key) - 1; // May throw NumberFormatException.
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error(e.toString());
                        ret = null;
                        break;
                    }

                    // Ensure that the resource list has enough slots.
                    for (int i = ret.getResources().size(); i < (resourceIndex + 1); i++)
                    {
                        ret.getResources().add(new Resource()); // NOPMD Avoid instantiating new objects inside loops
                    }

                    final Resource resource = ret.getResources().get(resourceIndex); // Shall not throw ArrayIndexOutOfBoundsException.

                    try
                    {
                        resource.setLength(Long.parseLong(value)); // May throw NumberFormatException.
                    }
                    catch (NumberFormatException e)
                    {
                        logger.error(e.toString());
                        ret = null;
                        break;
                    }
                }
                else if ("version".equals(key))
                {
                    // If present, shall be "2".
                    if (!"2".equals(value))
                    {
                        logger.error("Unknown PLS version " + value);
                        ret = null;
                        break;
                    }
                }
                else // FIXME "PlaylistName" ???
                {
                    logger.warn("Unknown PLS keyword " + key);
                }
            }
        }

        if (ret != null)
        {
            if (numberOfEntries < 0)
            {
                logger.warn("No number of entries in PLS playlist");
            }
            else
            {
                // Ignore any extra resource, if the number of entries has been specified so far.
                final int extras = ret.getResources().size() - numberOfEntries;

                if (extras > 0)
                {
                    logger.warn("Ignoring " + extras + " extra resources according to the specified number of entries " + numberOfEntries);
                }

                for (int i = 0; i < extras; i++)
                {
                    ret.getResources().remove(numberOfEntries); // Shall not throw UnsupportedOperationException, IndexOutOfBoundsException.
                }
            }
        }

        return ret;
    }

    @Override
    public SpecificPlaylist toSpecificPlaylist(final Playlist playlist) throws Exception
    {
        final PLS ret = new PLS();
        ret.setProvider(this);

        addToPlaylist(ret.getResources(), playlist.getRootSequence()); // May throw Exception.

        return ret;
    }

    /**
     * Adds the resources referenced in the specified generic playlist component to the input list.
     * @param resources the resulting list of resources. Shall not be <code>null</code>.
     * @param component the generic playlist component to handle. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>resources</code> is <code>null</code>.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @throws Exception if this service provider is unable to represent the input playlist.
     */
    private void addToPlaylist(final List<Resource> resources, final AbstractPlaylistComponent component) throws Exception
    {
        if (component instanceof Sequence)
        {
            final Sequence seq = (Sequence) component;

            if (seq.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A PLS playlist cannot handle a sequence repeated indefinitely");
            }

            final AbstractPlaylistComponent[] components = seq.getComponents();

            for (int iter = 0; iter < seq.getRepeatCount(); iter++)
            {
                for (AbstractPlaylistComponent c : components)
                {
                    addToPlaylist(resources, c); // May throw Exception.
                }
            }
        }
        else if (component instanceof Parallel)
        {
            throw new IllegalArgumentException("A parallel time container is incompatible with a PLS playlist");
        }
        else if (component instanceof Media)
        {
            final Media media = (Media) component;

            if (media.getDuration() != null)
            {
                throw new IllegalArgumentException("A PLS playlist cannot handle a timed media");
            }

            if (media.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A PLS playlist cannot handle a media repeated indefinitely");
            }

            if (media.getSource() != null)
            {
                for (int iter = 0; iter < media.getRepeatCount(); iter++)
                {
                    final Resource resource = new Resource(); // NOPMD Avoid instantiating new objects inside loops
                    resource.setLocation(media.getSource().toString());

                    if (media.getSource().getDuration() >= 0L)
                    {
                        resource.setLength((media.getSource().getDuration() + 999L) / 1000L);
                    }

                    resources.add(resource); // Shall not throw UnsupportedOperationException, ClassCastException, NullPointerException, IllegalArgumentException.
                }
            }
        }
    }
}
