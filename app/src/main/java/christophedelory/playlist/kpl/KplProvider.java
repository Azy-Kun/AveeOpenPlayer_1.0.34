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
package christophedelory.playlist.kpl;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.myapache.commons.logging.Log;

import mychristophedelory.content.type.ContentType;
import christophedelory.io.IOUtils;
import christophedelory.player.PlayerSupport;
import christophedelory.playlist.AbstractPlaylistComponent;
import christophedelory.playlist.Media;
import christophedelory.playlist.Parallel;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.Sequence;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;
import mychristophedelory.xml.Version;

/**
 * The Kalliope Playlist is a media file playlist format similar to M3U or PLS but based on XML language.
 * Kalliope Playlist was developed to speed up playlist loading process and in some cases can be much more efficient than popular M3U or PLS.
 * @since 0.3.0
 * @version $Revision: 91 $
 * @author Christophe Delory
 */
public class KplProvider implements SpecificPlaylistProvider
{
    /**
     * A list of compatible content types.
     */
    private static final ContentType[] FILETYPES =
    {
        new ContentType(new String[] { ".kpl" },
                        new String[] { "text/xml" }, // FIXME Something better?
                        new PlayerSupport[]
                        {
                        },
                        "Kalliope PlayList"),
    };

    @Override
    public String getId()
    {
        return "kpl";
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
            enc = "UTF-8";
        }

        String str = IOUtils.toString(in, enc); // May throw IOException. Throws NullPointerException if in is null.

        // Replace all occurrences of a single '&' with "&amp;" (or leave this construct as is).
        // First replace blindly all '&' to its corresponding character reference.
        str = str.replace("&", "&amp;");
        // Then restore any existing character reference.
        str = str.replaceAll("&amp;([a-zA-Z0-9#]+;)", "&$1"); // Shall not throw PatternSyntaxException.

        // An XML element name cannot begin with a digit (like in "0").
        // Thus the document we are about to parse is NOT well-formed.
        // But I can't set the "well-formed" parameter to the DOMConfiguration before parsing, nor call setStrictErrorChecking(false).
        // Thus this trick.
        str = str.replaceAll("<([0-9]+) ", "<x$1 "); // Shall not throw PatternSyntaxException.
        str = str.replaceAll("</([0-9]+)", "</x$1"); // Shall not throw PatternSyntaxException.

        // Unmarshal the KPL playlist.
        final StringReader reader = new StringReader(str);

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); // May throw FactoryConfigurationError.
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder(); // May throw ParserConfigurationException.
        documentBuilder.setErrorHandler(new DefaultHandler()); // To avoid logs on System.err like "[Fatal Error] :1:1: Content is not allowed in prolog."
        final Document document = documentBuilder.parse(new InputSource(reader)); // May throw IOException, SAXException. Shall not throw IllegalArgumentException.

        if (!"xml".equals(document.getDocumentElement().getTagName())) // Throws NullPointerException if getDocumentElement() is null.
        {
            throw new IllegalArgumentException("Not a Kalliope playlist (root element is not named 'xml')");
        }

        final Xml ret = new Xml();
        ret.setProvider(this);

        int nb = 0;
        NodeList nodeList = document.getElementsByTagName('x' + Integer.toString(nb));

        while (nodeList.getLength() > 0)
        {
            final Entry entry = new Entry(); // NOPMD Avoid instantiating new objects inside loops
            final Element element = (Element) nodeList.item(0); // Shall not be null.
            entry.setFilename(element.getAttribute("filename"));
            ret.getEntries().add(entry);

            final NodeList tagNodeList = element.getElementsByTagName("tag");

            if (tagNodeList.getLength() > 0)
            {
                final Tag tag = new Tag(); // NOPMD Avoid instantiating new objects inside loops
                final Element tagElement = (Element) tagNodeList.item(0); // Shall not be null.
                tag.setArtist(tagElement.getAttribute("artist"));
                tag.setAlbum(tagElement.getAttribute("album"));
                tag.setTitle(tagElement.getAttribute("title"));
                tag.setYear(tagElement.getAttribute("year"));
                tag.setComment(tagElement.getAttribute("comment"));
                tag.setGenre(tagElement.getAttribute("genre"));
                tag.setTrack(tagElement.getAttribute("track"));
                tag.setGid(tagElement.getAttribute("gid"));
                tag.setHasTag(tagElement.getAttribute("has_tag"));
                entry.setTag(tag);
            }

            nb++;
            nodeList = document.getElementsByTagName('x' + Integer.toString(nb));
        }

        nodeList = document.getElementsByTagName("info");

        if (nodeList.getLength() > 0)
        {
            final Element infoElement = (Element) nodeList.item(0); // Shall not be null.
            ret.getInfo().setCreationDayString(infoElement.getAttribute("creation_day")); // May throw ParseException.
            ret.getInfo().setModifiedDayString(infoElement.getAttribute("modified_day")); // May throw ParseException.
            ret.getInfo().setAuthor(infoElement.getAttribute("author"));
            ret.getInfo().setPlayer(infoElement.getAttribute("player"));
            ret.getInfo().setPlayerVersion(infoElement.getAttribute("player_version"));
            ret.getInfo().setKplVersion(infoElement.getAttribute("kpl_version"));
        }

        return ret;
    }

    @Override
    public SpecificPlaylist toSpecificPlaylist(final Playlist playlist) throws Exception
    {
        final Xml ret = new Xml();
        ret.setProvider(this);

        final Date day = new Date();
        ret.getInfo().setCreationDay(day);
        ret.getInfo().setModifiedDay(day);
        ret.getInfo().setAuthor("Lizzy v" + Version.CURRENT);

        addToPlaylist(ret.getEntries(), playlist.getRootSequence()); // May throw Exception.

        return ret;
    }

    /**
     * Adds the specified generic playlist component, and all its childs if any, to the input list.
     * @param entries the parent list of entries. Shall not be <code>null</code>.
     * @param component the generic playlist component to handle. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>entries</code> is <code>null</code>.
     * @throws NullPointerException if <code>component</code> is <code>null</code>.
     * @throws Exception if this service provider is unable to represent the input playlist.
     */
    private void addToPlaylist(final List<Entry> entries, final AbstractPlaylistComponent component) throws Exception
    {
        if (component instanceof Sequence)
        {
            final Sequence sequence = (Sequence) component;

            if (sequence.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A KPL playlist cannot handle a sequence repeated indefinitely");
            }

            final AbstractPlaylistComponent[] components = sequence.getComponents();

            for (int iter = 0; iter < sequence.getRepeatCount(); iter++)
            {
                for (AbstractPlaylistComponent c : components)
                {
                    addToPlaylist(entries, c); // May throw Exception.
                }
            }
        }
        else if (component instanceof Parallel)
        {
            throw new IllegalArgumentException("A KPL playlist cannot play different media at the same time");
        }
        else if (component instanceof Media)
        {
            final Media media = (Media) component;

            if (media.getDuration() != null)
            {
                throw new IllegalArgumentException("A KPL playlist cannot handle a timed media");
            }

            if (media.getRepeatCount() < 0)
            {
                throw new IllegalArgumentException("A KPL playlist cannot handle a media repeated indefinitely");
            }

            if (media.getSource() != null)
            {
                for (int iter = 0; iter < media.getRepeatCount(); iter++)
                {
                    final Entry entry = new Entry(); // NOPMD Avoid instantiating new objects inside loops
                    entry.setFilename(media.getSource().toString());
                    entries.add(entry);

                    final Tag tag = new Tag(); // NOPMD Avoid instantiating new objects inside loops
                    tag.setGid(Integer.toString(System.identityHashCode(entry)));
                    tag.setGenre("Other");
                    tag.setYear("Unknown Year");
                    tag.setTitle(media.getSource().toString());
                    entry.setTag(tag);
                }
            }
        }
    }
}
