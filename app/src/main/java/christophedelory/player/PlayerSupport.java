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
package christophedelory.player;

/**
 * Information about a player support of a particular multimedia file.
 * A file is supported if it can be parsed by the specified player.
 * Optionnally, it can also be saved.
 * <br>
 * See also:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Media_player_(application_software)">Media player (application software)</a></li>
 * <li><a href="http://en.wikipedia.org/wiki/Comparison_of_media_players">Comparison of media players</a></li>
 * </ul>
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @since 0.2.0
 */
public class PlayerSupport implements Cloneable
{
    /**
     * The player type.
     */
    public enum Player
    {
        FOOBAR2000,
        ITUNES,
        MEDIA_PLAYER_CLASSIC,
        MPLAYER,
        //MUSICMATCH_JUKEBOX,
        QUICKTIME,
        REALPLAYER,
        VLC_MEDIA_PLAYER,
        WINAMP,
        WINDOWS_MEDIA_PLAYER,
    };

    /**
     * Returns a string representation of the specified player.
     * @param player a player. Shall not be <code>null</code>.
     * @return a string representing the given player. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>player</code> is <code>null</code>.
     */
    public static String toString(final Player player)
    {
        final String ret;

        switch (player) // Throws NullPointerException if player is null.
        {
            case FOOBAR2000:
                ret = "Foobar2000";
                break;
            case ITUNES:
                ret = "iTunes";
                break;
            case MEDIA_PLAYER_CLASSIC:
                ret = "Media Player Classic";
                break;
            case MPLAYER:
                ret = "MPlayer";
                break;
            /*
            case MUSICMATCH_JUKEBOX:
                ret = "MusicMatch Jukebox";
                break;
            */
            case QUICKTIME:
                ret = "QuickTime";
                break;
            case REALPLAYER:
                ret = "RealPlayer";
                break;
            case VLC_MEDIA_PLAYER:
                ret = "VLC Media Player (VideoLAN)";
                break;
            case WINAMP:
                ret = "Winamp";
                break;
            case WINDOWS_MEDIA_PLAYER:
                ret = "Windows Media Player";
                break;
            default:
                ret = null;
        }

        return ret;
    }

    /**
     * The target player.
     */
    private final Player _player;

    /**
     * Specifies if the player can also save the mutimedia file.
     */
    private final boolean _isSaved;

    /**
     * An optional comment.
     */
    private final String _comment;

    /**
     * Builds a new player support information.
     * @param player the target player. Shall not be <code>null</code>.
     * @param isSaved specifies that the player can also save the mutimedia file, or not.
     * @param comment any comment on this player support. May be <code>null</code>.
     * @throws NullPointerException if <code>player</code> is <code>null</code>.
     */
    public PlayerSupport(final Player player, final boolean isSaved, final String comment)
    {
        if (player == null)
        {
            throw new NullPointerException("no player");
        }

        _player = player;
        _isSaved = isSaved;
        _comment = comment;
    }

    /**
     * Returns the target player.
     * @return a player description. Shall not be <code>null</code>.
     */
    public Player getPlayer()
    {
        return _player;
    }

    /**
     * Specifies that the target player can also save the mutimedia file, or not.
     * @return the associated boolean.
     */
    public boolean isSaved()
    {
        return _isSaved;
    }

    /**
     * Returns an optional comment associated to this player support.
     * @return a comment. May be <code>null</code>.
     */
    public String getComment()
    {
        return _comment;
    }

    /**
     * Creates and returns a "shallow" copy of this object.
     * @return a clone of this instance.
     * @throws CloneNotSupportedException shall not be thrown, because this class is cloneable.
     * @since 1.0.0
     * @see Object#clone
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone(); // Should not throw CloneNotSupportedException.
    }
}
