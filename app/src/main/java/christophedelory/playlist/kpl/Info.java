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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import christophedelory.lang.StringUtils;

/**
 * The information section in a Kalliope playlist.
 * It describes some useful information, for example a specification number that denotes which spec version was used to write the playlist
 * so other media players that load it will know exactly what specification to use when parsing the playlist.
 * Other info data is optional and used only for statistical purposes.
 * @since 0.3.0
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Info
{
    /**
     * The internal date format.
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * The creation day.
     */
    private Date _creation_day = null;

    /**
     * The modified day.
     */
    private Date _modified_day = null;

    /**
     * The author name.
     */
    private String _author = null;

    /**
     * The player name.
     */
    private String _player = null;

    /**
     * The player version.
     */
    private String _player_version = null;

    /**
     * The KPL version.
     */
    private String _kpl_version = "1";

    /**
     * Returns the creation day.
     * @return a day. May be <code>null</code>.
     * @see #setCreationDayString
     * @see #getCreationDay
     */
    public String getCreationDayString()
    {
        String ret = null;

        if (_creation_day != null)
        {
            synchronized(DATE_FORMAT)
            {
                ret = DATE_FORMAT.format(_creation_day); // Should not throw NullPointerException because of _creation_day.
            }
        }

        return ret;
    }

    /**
     * Initializes the creation day.
     * @param creationDay a day. May be <code>null</code>.
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     * @see #getCreationDayString
     * @see #setCreationDay
     */
    public void setCreationDayString(final String creationDay) throws ParseException
    {
        final String day = StringUtils.normalize(creationDay);

        if (day == null)
        {
            _creation_day = null;
        }
        else
        {
            synchronized(DATE_FORMAT)
            {
                _creation_day = DATE_FORMAT.parse(day); // May throw ParseException.
            }
        }
    }

    /**
     * Returns the creation day.
     * @return a day. May be <code>null</code>.
     * @see #setCreationDay
     * @see #getCreationDayString
     */
    public Date getCreationDay()
    {
        return _creation_day;
    }

    /**
     * Initializes the creation day.
     * @param creationDay a day. May be <code>null</code>.
     * @see #getCreationDay
     * @see #setCreationDayString
     */
    public void setCreationDay(final Date creationDay)
    {
        _creation_day = creationDay;
    }

    /**
     * Returns the modified day.
     * @return a day. May be <code>null</code>.
     * @see #setModifiedDayString
     * @see #getModifiedDay
     */
    public String getModifiedDayString()
    {
        String ret = null;

        if (_modified_day != null)
        {
            synchronized(DATE_FORMAT)
            {
                ret = DATE_FORMAT.format(_modified_day); // Should not throw NullPointerException because of _modified_day.
            }
        }

        return ret;
    }

    /**
     * Initializes the modified day.
     * @param modifiedDay a day. May be <code>null</code>.
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     * @see #getModifiedDayString
     * @see #setModifiedDay
     */
    public void setModifiedDayString(final String modifiedDay) throws ParseException
    {
        final String day = StringUtils.normalize(modifiedDay);

        if (day == null)
        {
            _modified_day = null;
        }
        else
        {
            synchronized(DATE_FORMAT)
            {
                _modified_day = DATE_FORMAT.parse(day); // May throw ParseException.
            }
        }
    }

    /**
     * Returns the modified day.
     * @return a day. May be <code>null</code>.
     * @see #setModifiedDay
     * @see #getModifiedDayString
     */
    public Date getModifiedDay()
    {
        return _modified_day;
    }

    /**
     * Initializes the modified day.
     * @param modifiedDay a day. May be <code>null</code>.
     * @see #getModifiedDay
     * @see #setModifiedDayString
     */
    public void setModifiedDay(final Date modifiedDay)
    {
        _modified_day = modifiedDay;
    }

    /**
     * Returns the author name.
     * @return an author name. May be <code>null</code>.
     * @see #setAuthor
     */
    public String getAuthor()
    {
        return _author;
    }

    /**
     * Initializes the author name.
     * @param author an author name. May be <code>null</code>.
     * @see #getAuthor
     */
    public void setAuthor(final String author)
    {
        _author = StringUtils.normalize(author);
    }

    /**
     * Returns the player name.
     * @return a player name. May be <code>null</code>.
     * @see #setPlayer
     */
    public String getPlayer()
    {
        return _player;
    }

    /**
     * Initializes the player name.
     * @param player a player name. May be <code>null</code>.
     * @see #getPlayer
     */
    public void setPlayer(final String player)
    {
        _player = StringUtils.normalize(player);
    }

    /**
     * Returns the player version.
     * @return a player version. May be <code>null</code>.
     * @see #setPlayerVersion
     */
    public String getPlayerVersion()
    {
        return _player_version;
    }

    /**
     * Initializes the player version.
     * @param playerVersion a player version. May be <code>null</code>.
     * @see #getPlayerVersion
     */
    public void setPlayerVersion(final String playerVersion)
    {
        _player_version = StringUtils.normalize(playerVersion);
    }

    /**
     * Returns the KPL version.
     * @return a KPL version. May be <code>null</code>.
     * @see #setKplVersion
     */
    public String getKplVersion()
    {
        return _kpl_version;
    }

    /**
     * Initializes the KPL version.
     * @param kplVersion a KPL version. May be <code>null</code>.
     * @see #getKplVersion
     */
    public void setKplVersion(final String kplVersion)
    {
        _kpl_version = StringUtils.normalize(kplVersion);
    }
}
