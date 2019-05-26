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
package christophedelory.rss.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows grouping of media content elements that are effectively the same content, yet different representations.
 * Media objects that are not the same content should not be included in the same media group element.
 * For instance: the same song recorded in both the WAV and MP3 format.
 * It's an optional element that must only be used for this purpose.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:group" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Group extends BaseMedia
{
    /**
     * The list of media contents.
     */
    private final List<Content> _mediaContents = new ArrayList<Content>();

    /**
	 * Adds a media content.
     * @param mediaContent a media content. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaContent</code> is <code>null</code>.
     * @see #getMediaContents
     */
    public void addMediaContent(final Content mediaContent)
    {
        if (mediaContent == null)
        {
            throw new NullPointerException("no media content");
        }

        _mediaContents.add(mediaContent);
    }

    /**
	 * Returns the list of media contents.
     * @return a list of media contents. May be empty but not <code>null</code>.
     * @see #addMediaContent
     * @castor.field
     *  type="christophedelory.rss.media.Content"
     *  get-method="getMediaContents"
     *  set-method="addMediaContent"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:content"
     *  node="element"
     */
    public List<Content> getMediaContents()
    {
        return _mediaContents;
    }
}
