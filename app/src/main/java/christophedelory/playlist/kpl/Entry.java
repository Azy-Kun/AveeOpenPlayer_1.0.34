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

/**
 * A Kalliope playlist entry.
 * Each tag's name is its number in the play order, starting with zero.
 * The only required parameter is the "filename" tag, whose value is a relative path to the media file.
 * Every playlist entry may also have description tags.
 * @since 0.3.0
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Entry
{
    /**
     * The relative path to the media file.
     */
    private String _fileName = null;

    /**
     * The associated tag.
     */
    private Tag _tag = null;

    /**
     * Returns the relative path to the media file.
     * @return a file name. May be <code>null</code> if not yet initialized.
     * @see #setFilename
     */
    public String getFilename()
    {
        return _fileName;
    }

    /**
     * Initializes the relative path to the media file.
     * @param fileName a file name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>fileName</code> is <code>null</code>.
     * @see #getFilename
     */
    public void setFilename(final String fileName)
    {
        _fileName = fileName.trim(); // Throws NullPointerException if fileName is null.
    }

    /**
     * Returns the associated tag, if any.
     * @return a tag. May be <code>null</code>.
     * @see #setTag
     */
    public Tag getTag()
    {
        return _tag;
    }

    /**
     * Initializes the associated tag.
     * @param tag a tag. May be <code>null</code>.
     * @see #getTag
     */
    public void setTag(final Tag tag)
    {
        _tag = tag;
    }
}
