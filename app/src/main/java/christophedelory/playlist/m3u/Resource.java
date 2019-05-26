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
package christophedelory.playlist.m3u;

import christophedelory.lang.StringUtils;

/**
 * A M3U or PLS playlist resource definition.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Resource
{
    /**
     * The title or name of the resource.
     */
    private String _name = null;

    /**
     * The resource location.
     */
    private String _location = null;

    /**
     * The playtime in seconds.
     */
    private long _seconds = -1L;

    /**
     * Returns the name (or title) of this resource.
     * It is likely to be displayed by the player.
     * @return the resource name. May be <code>null</code>.
     * @see #setName
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Initializes the name (or title) of this resource.
     * @param name a resource name. May be <code>null</code>.
     * @see #getName
     */
    public void setName(final String name)
    {
        _name = StringUtils.normalize(name);
    }

    /**
     * Returns the location of this resource, as a string.
     * @return the resource location. May be <code>null</code> if not yet initialized.
     * @see #setLocation
     */
    public String getLocation()
    {
        return _location;
    }

    /**
     * Initializes the location of this resource from the specified string.
     * The string may represent a file or an URL.
     * @param location a resource location. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>location</code> is <code>null</code>.
     * @see #getLocation
     */
    public void setLocation(final String location)
    {
        _location = location.trim().replace('\\', '/'); // Throws NullPointerException if location is null.
    }

    /**
     * Returns the resource playtime in seconds.
     * Defaults to -1 (unknown).
     * @return a time length in seconds.
     * @see #setLength
     */
    public long getLength()
    {
        return _seconds;
    }

    /**
     * Initializes the resource playtime in seconds.
     * @param seconds a time length in seconds.
     * @see #getLength
     */
    public void setLength(final long seconds)
    {
        // Enforce the value of -1.
        if (seconds < 0L)
        {
            _seconds = -1L;
        }
        else
        {
            _seconds = seconds;
        }
    }
}
