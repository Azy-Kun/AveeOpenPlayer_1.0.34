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

import christophedelory.lang.StringUtils;

/**
 * A tag in a Kalliope playlist entry.
 * @since 0.3.0
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Tag
{
    /**
     * The artist name.
     */
    private String _artist = null;

    /**
     * The album name.
     */
    private String _album = null;

    /**
     * The title.
     */
    private String _title = null;

    /**
     * The year.
     */
    private String _year = null;

    /**
     * The comment.
     */
    private String _comment = null;

    /**
     * The genre.
     */
    private String _genre = null;

    /**
     * The track number.
     */
    private String _track = null;

    /**
     * The GID.
     */
    private String _gid = null;

    /**
     * The tag indicator.
     */
    private String _has_tag = "True";

    /**
     * Returns the artist name.
     * @return an artist name. May be <code>null</code>.
     * @see #setArtist
     */
    public String getArtist()
    {
        return _artist;
    }

    /**
     * Initializes the artist name.
     * @param artist an artist name. May be <code>null</code>.
     * @see #getArtist
     */
    public void setArtist(final String artist)
    {
        _artist = StringUtils.normalize(artist);
    }

    /**
     * Returns the album name.
     * @return an album name. May be <code>null</code>.
     * @see #setAlbum
     */
    public String getAlbum()
    {
        return _album;
    }

    /**
     * Initializes the album name.
     * @param album an album name. May be <code>null</code>.
     * @see #getAlbum
     */
    public void setAlbum(final String album)
    {
        _album = StringUtils.normalize(album);
    }

    /**
     * Returns the title.
     * @return a title. May be <code>null</code>.
     * @see #setTitle
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Initializes the title.
     * @param title a title. May be <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = StringUtils.normalize(title);
    }

    /**
     * Returns the year.
     * @return a year. May be <code>null</code>.
     * @see #setYear
     */
    public String getYear()
    {
        return _year;
    }

    /**
     * Initializes the year.
     * @param year a year. May be <code>null</code>.
     * @see #getYear
     */
    public void setYear(final String year)
    {
        _year = StringUtils.normalize(year);
    }

    /**
     * Returns the comment.
     * @return a comment. May be <code>null</code>.
     * @see #setComment
     */
    public String getComment()
    {
        return _comment;
    }

    /**
     * Initializes the comment.
     * @param comment a comment. May be <code>null</code>.
     * @see #getComment
     */
    public void setComment(final String comment)
    {
        _comment = StringUtils.normalize(comment);
    }

    /**
     * Returns the genre.
     * @return a genre. May be <code>null</code>.
     * @see #setGenre
     */
    public String getGenre()
    {
        return _genre;
    }

    /**
     * Initializes the genre.
     * @param genre a genre. May be <code>null</code>.
     * @see #getGenre
     */
    public void setGenre(final String genre)
    {
        _genre = StringUtils.normalize(genre);
    }

    /**
     * Returns the track number.
     * @return a track number. May be <code>null</code>.
     * @see #setTrack
     */
    public String getTrack()
    {
        return _track;
    }

    /**
     * Initializes the track number.
     * @param track a track number. May be <code>null</code>.
     * @see #getTrack
     */
    public void setTrack(final String track)
    {
        _track = StringUtils.normalize(track);
    }

    /**
     * Returns the GID.
     * @return a GID. May be <code>null</code>.
     * @see #setGid
     */
    public String getGid()
    {
        return _gid;
    }

    /**
     * Initializes the GID.
     * @param gid a GID. May be <code>null</code>.
     * @see #getGid
     */
    public void setGid(final String gid)
    {
        _gid = StringUtils.normalize(gid);
    }

    /**
     * Returns the tag indicator.
     * @return a tag indicator. May be <code>null</code>.
     * @see #setHasTag
     */
    public String getHasTag()
    {
        return _has_tag;
    }

    /**
     * Initializes the tag indicator.
     * @param hasTag a tag indicator. May be <code>null</code>.
     * @see #getHasTag
     */
    public void setHasTag(final String hasTag)
    {
        _has_tag = StringUtils.normalize(hasTag);
    }
}
