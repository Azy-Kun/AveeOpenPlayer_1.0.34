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


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import mychristophedelory.content.Content;
import christophedelory.playlist.Media;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;

/**
 * The definition of a Kalliope playlist (KPL).
 * According to the Kalliope Playlist 1.0 specification, the .KPL file is divided into two logical parts: a playlist section and an information section.
 * The most important part of the file is the information section; see {@link Info}.
 * The second part of the file is the playlist section; see {@link Entry}.
 * @since 0.3.0
 * @version $Revision: 91 $
 * @author Christophe Delory
 */
public class Xml implements SpecificPlaylist
{
    /**
     * The provider of this specific playlist.
     */
    private transient SpecificPlaylistProvider _provider = null;

    /**
     * The list of entries in this playlist.
     */
    private final List<Entry> _entries = new ArrayList<Entry>();

    /**
     * The information section.
     */
    private final Info _info = new Info();

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
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); // May throw FactoryConfigurationError.
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder(); // May throw ParserConfigurationException.
        final Document document = documentBuilder.newDocument();
        document.setStrictErrorChecking(false);
        final Element xmlElement = document.createElement("xml"); // May throw DOMException.
        document.appendChild(xmlElement); // May throw DOMException.
        int nb = 0;

        for (Entry entry : _entries)
        {
            if (entry.getFilename() != null)
            {
                final Element element = document.createElement(Integer.toString(nb)); // May throw DOMException.
                element.setAttribute("filename", entry.getFilename()); // May throw DOMException.
                xmlElement.appendChild(element); // May throw DOMException.

                if (entry.getTag() != null)
                {
                    final Element tagElement = document.createElement("tag"); // May throw DOMException.
                    tagElement.setAttribute("artist", entry.getTag().getArtist()); // May throw DOMException.
                    tagElement.setAttribute("album", entry.getTag().getAlbum()); // May throw DOMException.
                    tagElement.setAttribute("title", entry.getTag().getTitle()); // May throw DOMException.
                    tagElement.setAttribute("year", entry.getTag().getYear()); // May throw DOMException.
                    tagElement.setAttribute("comment", entry.getTag().getComment()); // May throw DOMException.
                    tagElement.setAttribute("genre", entry.getTag().getGenre()); // May throw DOMException.
                    tagElement.setAttribute("track", entry.getTag().getTrack()); // May throw DOMException.
                    tagElement.setAttribute("gid", entry.getTag().getGid()); // May throw DOMException.
                    tagElement.setAttribute("has_tag", entry.getTag().getHasTag()); // May throw DOMException.
                    element.appendChild(tagElement); // May throw DOMException.
                }

                nb++;
            }
        }

        final Element infoElement = document.createElement("info"); // May throw DOMException.
        infoElement.setAttribute("creation_day", _info.getCreationDayString()); // May throw DOMException.
        infoElement.setAttribute("modified_day", _info.getModifiedDayString()); // May throw DOMException.
        infoElement.setAttribute("author", _info.getAuthor()); // May throw DOMException.
        infoElement.setAttribute("player", _info.getPlayer()); // May throw DOMException.
        infoElement.setAttribute("player_version", _info.getPlayerVersion()); // May throw DOMException.
        infoElement.setAttribute("kpl_version", _info.getKplVersion()); // May throw DOMException.
        xmlElement.appendChild(infoElement); // May throw DOMException.

        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(out);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance(); // May throw TransformerFactoryConfigurationError.
        final Transformer transformer = transformerFactory.newTransformer(); // May throw TransformerConfigurationException.
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Should not throw IllegalArgumentException.
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Should not throw IllegalArgumentException.

        if (encoding != null)
        {
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding); // Should not throw IllegalArgumentException.
        }

        transformer.transform(source, result); // May throw TransformerException.

        out.flush(); // May throw IOException.
    }

    @Override
    public Playlist toPlaylist()
    {
        final Playlist ret = new Playlist();

        for (Entry entry : _entries)
        {
            if (entry.getFilename() != null)
            {
                final Media media = new Media(); // NOPMD Avoid instantiating new objects inside loops
                final Content content = new Content(entry.getFilename()); // NOPMD Avoid instantiating new objects inside loops
                media.setSource(content);
                ret.getRootSequence().addComponent(media);
            }
        }

        // We don't really need it.
        ret.normalize();

        return ret;
    }

    /**
     * Returns the list of entries in this playlist.
     * @return a list of entries. May be empty but not <code>null</code>.
     */
    public List<Entry> getEntries()
    {
        return _entries;
    }

    /**
     * Returns the information section of this playlist.
     * @return an information section. Shall not be <code>null</code>.
     */
    public Info getInfo()
    {
        return _info;
    }
}
