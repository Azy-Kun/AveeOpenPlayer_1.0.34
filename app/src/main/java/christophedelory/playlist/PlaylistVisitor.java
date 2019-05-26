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

/**
 * A {@link Playlist playlist} hierarchy visitor.
 * @see Playlist#acceptDown
 * @see Playlist#acceptUp
 * @see Parallel#acceptDown
 * @see Parallel#acceptUp
 * @see Sequence#acceptDown
 * @see Sequence#acceptUp
 * @see Media#acceptDown
 * @see Media#acceptUp
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public interface PlaylistVisitor
{
    /**
     * Starts the visit of the specified playlist.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #endVisitPlaylist
     */
    void beginVisitPlaylist(final Playlist target) throws Exception;

    /**
     * Finishes the visit of the specified playlist.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #beginVisitPlaylist
     */
    void endVisitPlaylist(final Playlist target) throws Exception;

    /**
     * Starts the visit of the specified parallel timing container.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #endVisitParallel
     */
    void beginVisitParallel(final Parallel target) throws Exception;

    /**
     * Finishes the visit of the specified parallel timing container.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #beginVisitParallel
     */
    void endVisitParallel(final Parallel target) throws Exception;

    /**
     * Starts the visit of the specified sequence.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #endVisitSequence
     */
    void beginVisitSequence(final Sequence target) throws Exception;

    /**
     * Finishes the visit of the specified sequence.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #beginVisitSequence
     */
    void endVisitSequence(final Sequence target) throws Exception;

    /**
     * Starts the visit of the specified media.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #endVisitMedia
     */
    void beginVisitMedia(final Media target) throws Exception;

    /**
     * Finishes the visit of the specified media.
     * @param target the element being visited. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>target</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     * @see #beginVisitMedia
     */
    void endVisitMedia(final Media target) throws Exception;
}
