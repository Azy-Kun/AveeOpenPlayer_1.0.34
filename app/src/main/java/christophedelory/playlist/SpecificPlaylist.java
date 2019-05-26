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

import java.io.OutputStream;

/**
 * The base definition of a specific playlist implementation.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public interface SpecificPlaylist
{
    /**
     * Initializes the provider of this specific playlist.
     * @param provider the provider of this playlist. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>provider</code> is <code>null</code>.
     * @see #getProvider
     */
    void setProvider(final SpecificPlaylistProvider provider);

    /**
     * Returns the provider of this specific playlist.
     * @return the provider of this playlist. May be <code>null</code>.
     * @see #setProvider
     */
    SpecificPlaylistProvider getProvider();

    /**
     * Writes this specific playlist to the specified output stream.
     * When done, the stream may be flushed, but not closed.
     * @param out an output stream. Shall not be <code>null</code>.
     * @param encoding the content encoding of the output resource, or <code>null</code> if not known.
     * @throws NullPointerException if <code>out</code> is <code>null</code>.
     * @throws Exception if any error occurs during the marshalling process.
     * @see SpecificPlaylistFactory#readFrom
     * @see SpecificPlaylistProvider#readFrom
     */
    void writeTo(final OutputStream out, final String encoding) throws Exception;

    /**
     * Builds a generic representation from this specific playlist.
     * @return a generic playlist. Shall not be <code>null</code>.
     * @see SpecificPlaylistProvider#toSpecificPlaylist
     */
    Playlist toPlaylist();
}
