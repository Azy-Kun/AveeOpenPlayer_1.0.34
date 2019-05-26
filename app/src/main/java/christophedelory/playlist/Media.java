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

import mychristophedelory.content.Content;

/**
 * The definition of a media content.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Media extends AbstractPlaylistComponent
{
    /**
     * The media source.
     */
    private Content _source = null;

    /**
     * The play duration in milliseconds.
     */
    private Long _duration = null;

    /**
     * Returns the media source.
     * @return a media source. May be <code>null</code> if not yet initialized.
     * @see #setSource
     */
    public Content getSource()
    {
        return _source;
    }

    /**
     * Initializes the media source.
     * @param source a media source. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>source</code> is null</code>.
     * @see #getSource
     */
    public void setSource(final Content source)
    {
        if (source == null)
        {
            throw new NullPointerException("No media source");
        }

        _source = source;
    }

    /**
     * Returns the play duration in milliseconds.
     * <br>
     * Not supported by all playlist formats.
     * @return a duration value. May be <code>null</code>.
     * @see #setDuration
     */
    public Long getDuration()
    {
        return _duration;
    }

    /**
     * Initializes the play duration in milliseconds.
     * If specified, shall not be negative or null.
     * @param millis a duration value. May be <code>null</code>.
     * @throws IllegalArgumentException if the specified duration value is negative or null.
     * @see #getDuration
     * @see #setDuration
     */
    public void setDuration(final Long millis)
    {
        if ((millis != null) && (millis.longValue() <= 0L))
        {
            throw new IllegalArgumentException("Negative or null duration " + millis);
        }

        _duration = millis;
    }

    /**
     * Initializes the play duration in milliseconds.
     * Shall not be negative or null.
     * @param millis a duration value.
     * @throws IllegalArgumentException if the specified duration is negative or null.
     * @see #getDuration
     * @see #setDuration
     */
    public void setDuration(final long millis)
    {
        if (millis <= 0L)
        {
            throw new IllegalArgumentException("Negative or null duration " + millis);
        }

        _duration = Long.valueOf(millis);
    }

    @Override
    public void acceptDown(final PlaylistVisitor visitor) throws Exception
    {
        visitor.beginVisitMedia(this); // Throws NullPointerException if visitor is null. May throw Exception.

        visitor.endVisitMedia(this); // May throw Exception.
    }

    @Override
    public void acceptUp(final PlaylistVisitor visitor) throws Exception
    {
        visitor.beginVisitMedia(this); // Throws NullPointerException if visitor is null. May throw Exception.

        super.acceptUp(visitor); // May throw Exception.

        visitor.endVisitMedia(this); // May throw Exception.
    }
}
