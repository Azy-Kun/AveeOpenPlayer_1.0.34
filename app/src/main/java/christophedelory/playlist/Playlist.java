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

import org.myapache.commons.logging.Log;
import mychristophedelory.logging.LogFactory;

/**
 * The definition of the top-level element: the playlist.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class Playlist
{
    /**
     * The instance logger.
     */
    private final static Log _logger = LogFactory.getLog(Playlist.class); // May throw LogConfigurationException.

    /**
     * The normalization process.
     */
    private static final PlaylistVisitor NORMALIZATION = new Normalization();

    /**
     * The root sequence of this playlist.
     */
    private final Sequence _rootSequence;

    /**
     * Builds a new and empty playlist.
     */
    public Playlist()
    {
        _rootSequence = new Sequence();
    }

    /**
     * Returns the root sequence of this playlist.
     * @return a playlist sequence. Shall not be <code>null</code>.
     */
    public Sequence getRootSequence()
    {
        return _rootSequence;
    }

    /**
     * Normalizes this playlist.
     */
    public void normalize()
    {
        try
        {
            // By-pass the playlist itself.
            _rootSequence.acceptDown(NORMALIZATION);

            // Re-process it now, as we may have merged two or more sequences before.
            // So just to detect singleton sequences to be moved one level up...
            _rootSequence.acceptDown(NORMALIZATION);
        }
        catch (Exception e)
        {
            // Should not occur.
            _logger.error("Unexpected error condition", e);
        }
    }

    /**
     * The normalization process.
     */
    private static class Normalization extends BasePlaylistVisitor
    {
        @Override
        public void endVisitMedia(final Media target)
        {
            // Suppress media components with unspecified URI.
            if (target.getSource() == null)
            {
                _logger.info("Removing media with no source: " + target);
                target.getParent().removeComponent(target);
            }
        }

        @Override
        public void endVisitParallel(final Parallel target) throws Exception
        {
            endVisitTimeContainer(target);
        }

        @Override
        public void endVisitSequence(final Sequence target) throws Exception
        {
            endVisitTimeContainer(target);

            // Special case of the root sequence (thus with no parent) which owns a single sequence:
            // merge the child sequence into the root sequence.
            // Multiply their repeat count if needed.
            if ((target.getParent() == null) && (target.getComponentsNumber() == 1))
            {
                final AbstractPlaylistComponent[] targetComponents = target.getComponents();

                if (targetComponents[0] instanceof Sequence)
                {
                    final Sequence sequence = (Sequence) targetComponents[0];

                    _logger.info("Merging root sequence " + target + " with its single child sequence " + sequence);
                    target.setRepeatCount(target.getRepeatCount() * sequence.getRepeatCount());
                    final AbstractPlaylistComponent[] components = sequence.getComponents();
                    target.removeComponent(sequence);

                    for (AbstractPlaylistComponent component : components)
                    {
                        target.addComponent(component);
                    }
                }
            }

            mergeConsecutiveIdenticalMedia(target);
            mergeConsecutiveSequences(target);
        }

        /**
         * Finishes the visit of the specified timing container.
         * @param target the element being visited. Shall not be <code>null</code>.
         * @throws NullPointerException if <code>target</code> is <code>null</code>.
         * @see #endVisitSequence
         * @see #endVisitParallel
         */
        private void endVisitTimeContainer(final AbstractTimeContainer target)
        {
            final AbstractTimeContainer targetParent = target.getParent(); // May be null.

            // Do not remove or even handle the root sequence.
            if (targetParent != null)
            {
                final int componentsNumber = target.getComponentsNumber();

                if (componentsNumber == 0)
                {
                    // Suppress empty time containers.
                    _logger.info("Removing empty time container " + target);
                    targetParent.removeComponent(target);
                    // Done with this time container, as we just removed it from its parent's list.
                }
                else if (componentsNumber == 1)
                {
                    // Suppress a time container with a single media in it (in fact put it one level up), and multiply their repeat count if needed.
                    final AbstractPlaylistComponent[] targetComponents = target.getComponents();
                    _logger.info("Replacing time container " + target + " with its single child component " + targetComponents[0]);
                    targetComponents[0].setRepeatCount(targetComponents[0].getRepeatCount() * target.getRepeatCount());
                    target.removeComponent(targetComponents[0]);
                    targetParent.removeComponent(target);
                    // CAUTION: we must keep a reference to the target's parent, as the last call has reset it.
                    targetParent.addComponent(targetComponents[0]);
                    // Done with this time container, as we just removed it from its parent's list.
                }
            }
        }

        /**
         * Merges two or more consecutive and identical medias in the specified sequence.
         * Identical means: the same source, and the same duration.
         * @param target the target sequence. Shall not be <code>null</code>.
         * @throws NullPointerException if <code>target</code> is <code>null</code>.
         */
        private void mergeConsecutiveIdenticalMedia(final Sequence target)
        {
            final AbstractPlaylistComponent[] targetComponents = target.getComponents(); // Throws NullPointerException if target is null.

            for (int i = 0; i < (targetComponents.length - 1); i++)
            {
                if (targetComponents[i] instanceof Media)
                {
                    final Media media1 = (Media) targetComponents[i];
                    int upTo = i;

                    for (int j = (i + 1); j < targetComponents.length; j++)
                    {
                        // Not a media: stop here.
                        if (!(targetComponents[j] instanceof Media))
                        {
                            break;
                        }

                        final Media media2 = (Media) targetComponents[j];

                        // Not the same source, or no source at all: stop here.
                        if ((media2.getSource() == null) || !media2.getSource().equals(media1.getSource()))
                        {
                            break;
                        }

                        // Not the same duration: stop here.
                        if (((media2.getDuration() == null) && (media1.getDuration() != null)) ||
                            ((media2.getDuration() != null) && !media2.getDuration().equals(media1.getDuration())))
                        {
                            break;
                        }

                        // Found one! Merge media1 up to this one. And continue iterating other the next ones.
                        upTo = j;
                    }

                    if (upTo > i)
                    {
                        final Sequence newSequence = new Sequence(); // NOPMD Avoid instantiating new objects inside loops
                        newSequence.setRepeatCount(1 + upTo - i);
                        _logger.info("Merging " + newSequence.getRepeatCount() + " identical media in a new sequence");
                        target.addComponent(i, newSequence); // Shall not throw IndexOutOfBoundsException.

                        for (int j = i; j <= upTo; j++)
                        {
                            target.removeComponent(i + 1); // Shall not throw IndexOutOfBoundsException.
                            newSequence.addComponent(targetComponents[j]);
                        }

                        // Skip the merged media.
                        i = upTo;
                    }
                }
            }
        }

        /**
         * Merge two consecutive sequences, if not in parallel, with the same repeat count.
         * @FIXME We should handle the case where 2 sequences, in sequence, don't have the same repeat count, but use the same components in the same order. Harder.
         * @param target the target sequence. Shall not be <code>null</code>.
         * @throws NullPointerException if <code>target</code> is <code>null</code>.
         */
        private void mergeConsecutiveSequences(final Sequence target)
        {
            final AbstractPlaylistComponent[] targetComponents = target.getComponents();

            // Iterate from last to second.
            for (int i = (targetComponents.length - 1); i > 0 ; i--)
            {
                if ((targetComponents[i - 1] instanceof Sequence) && (targetComponents[i] instanceof Sequence))
                {
                    final Sequence seq1 = (Sequence) targetComponents[i - 1];
                    final Sequence seq2 = (Sequence) targetComponents[i];

                    if (seq1.getRepeatCount() == seq2.getRepeatCount())
                    {
                        // Append the components of the last (second) sequence to the first one.
                        _logger.info("Merging sequence " + seq2 + " in sequence " + seq1);
                        final AbstractPlaylistComponent[] components = seq2.getComponents();

                        for (AbstractPlaylistComponent component : components)
                        {
                            seq1.addComponent(component);
                        }

                        // Then finally remove the dead sequence.
                        target.removeComponent(seq2);
                    }
                }
            }
        }
    }

    /**
     * Accepts the specified playlist visitor.
     * @param visitor a visitor. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>visitor</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     */
    public void acceptDown(final PlaylistVisitor visitor) throws Exception
    {
        visitor.beginVisitPlaylist(this); // Throws NullPointerException if visitor is null. May throw Exception.

        _rootSequence.acceptDown(visitor); // May throw Exception.

        visitor.endVisitPlaylist(this); // May throw Exception.
    }

    /**
     * Accepts the specified playlist visitor.
     * @param visitor a visitor. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>visitor</code> is <code>null</code>.
     * @throws Exception if any error occurs during the visit.
     */
    public void acceptUp(final PlaylistVisitor visitor) throws Exception
    {
        visitor.beginVisitPlaylist(this); // Throws NullPointerException if visitor is null. May throw Exception.

        visitor.endVisitPlaylist(this); // May throw Exception.
    }
}
