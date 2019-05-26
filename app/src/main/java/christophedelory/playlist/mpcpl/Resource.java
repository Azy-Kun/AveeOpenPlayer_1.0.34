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
package christophedelory.playlist.mpcpl;

/**
 * A MPCPL resource definition.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @since 0.3.0
 */
public class Resource
{
    /**
     * The file name of the resource.
     */
    private String _filename = null;

    /**
     * The resource type.
     */
    private String _type = "0";

    /**
     * The file name of an optional subtitle.
     */
    private String _subtitle = null;

    /**
     * Returns the file name of this resource.
     * @return the resource file name. May be <code>null</code> if not yet initialized.
     * @see #setFilename
     */
    public String getFilename()
    {
        return _filename;
    }

    /**
     * Initializes the file name of this resource.
     * @param filename a resource file name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>filename</code> is <code>null</code>.
     * @see #getFilename
     */
    public void setFilename(final String filename)
    {
        _filename = filename.trim(); // Throws NullPointerException if filename is null.
    }

    /**
     * Returns the type of this resource.
     * Defaults to "0".
     * @return the resource type. Shall not be <code>null</code>.
     * @see #setType
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Initializes the type of this resource.
     * @param type a resource type. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>type</code> is <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type.trim(); // Throws NullPointerException if type is null.
    }

    /**
     * Returns the file name of an optional subtitle.
     * Example: "sub1.srt".
     * @return a subtitle file name. May be <code>null</code>.
     * @see #setSubtitle
     */
    public String getSubtitle()
    {
        return _subtitle;
    }

    /**
     * Initializes the file name of an associated subtitle.
     * @param subtitle a subtitle file name. May be <code>null</code>.
     * @see #getSubtitle
     */
    public void setSubtitle(final String subtitle)
    {
        _subtitle = subtitle;
    }
}
