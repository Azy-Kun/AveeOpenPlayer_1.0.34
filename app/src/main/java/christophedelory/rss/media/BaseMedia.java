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
package christophedelory.rss.media;

import java.util.ArrayList;
import java.util.List;

/**
 * The base media description in Media RSS.
 * The following elements are optional and may appear as sub-elements of channel, item, media content and/or media:group.
 * When an element appears at a shallow level, such as channel or item, it means that the element should be applied to every media object within its scope.
 * Duplicated elements appearing at deeper levels of the document tree have higher priority over other levels.
 * For example, media content level elements are favored over item level elements.
 * The priority level is listed from strongest to weakest: media content, media group, item, channel.
 * @author Christophe Delory
 * @version $Revision: 92 $
 */
public class BaseMedia
{
    /**
     * The title of the media object.
     */
    private Title _mediaTitle = null;

    /**
     * The description of the media object.
     */
    private Description _mediaDescription = null;

    /**
     * The player definition, if any.
     */
    private Player _mediaPlayer = null;

    /**
     * A list of thumbnails.
     */
    private final List<Thumbnail> _mediaThumbnails = new ArrayList<Thumbnail>();

    /**
     * This is deprecated, and has been replaced with the more flexible media rating.
     */
    private Boolean _mediaAdult = null;

    /**
     * Includes the item in one or more categories.
     */
    private final List<Category> _mediaCategories = new ArrayList<Category>();

    /**
     * The list of ratings attached to this media object.
     */
    private final List<Rating> _mediaRatings = new ArrayList<Rating>();

    /**
     * Highly relevant keywords describing the media object with typically a maximum of ten words.
     */
    private String _mediaKeywords = null;

    /**
     * The list of hashes attached to this media object.
     */
    private final List<Hash> _mediaHashes = new ArrayList<Hash>();

    /**
     * A list of credits.
     */
    private final List<Credit> _mediaCredits = new ArrayList<Credit>();

    /**
     * Copyright information for media object.
     */
    private Copyright _mediaCopyright = null;

    /**
     * A list of texts.
     */
    private final List<Text> _mediaTexts = new ArrayList<Text>();

    /**
     * A list of restrictions.
     */
    private final List<Restriction> _mediaRestrictions = new ArrayList<Restriction>();

    /**
     * Initializes the player definition.
     * @param mediaPlayer a player definition. May be <code>null</code>.
     * @see #getMediaPlayer
     */
    public void setMediaPlayer(final Player mediaPlayer)
    {
        _mediaPlayer = mediaPlayer;
    }

    /**
     * Returns the player definition, if any.
     * @return a player definition. May be <code>null</code>.
     * @see #setMediaPlayer
     * @castor.field
     *  get-method="getMediaPlayer"
     *  set-method="setMediaPlayer"
     * @castor.field-xml
     *  name="media:player"
     *  node="element"
     */
    public Player getMediaPlayer()
    {
        return _mediaPlayer;
    }

    /**
     * Adds a thumbnail.
     * @param mediaThumbnail a thumbnail. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaThumbnail</code> is <code>null</code>.
     * @see #getMediaThumbnails
     */
    public void addMediaThumbnail(final Thumbnail mediaThumbnail)
    {
        if (mediaThumbnail == null)
        {
            throw new NullPointerException("no media thumbnail");
        }

        _mediaThumbnails.add(mediaThumbnail);
    }

    /**
     * Returns the list of thumbnails.
     * @return a list of thumbnails. May be empty but not <code>null</code>.
     * @see #addMediaThumbnail
     * @castor.field
     *  type="christophedelory.rss.media.Thumbnail"
     *  get-method="getMediaThumbnails"
     *  set-method="addMediaThumbnail"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:thumbnail"
     *  node="element"
     */
    public List<Thumbnail> getMediaThumbnails()
    {
        return _mediaThumbnails;
    }

    /**
     * Initializes the title of the media object.
     * @param mediaTitle a title. May be <code>null</code>.
     * @see #getMediaTitle
     */
    public void setMediaTitle(final Title mediaTitle)
    {
        _mediaTitle = mediaTitle;
    }

    /**
     * Returns the title of the media object.
     * @return a title. May be <code>null</code>.
     * @see #setMediaTitle
     * @castor.field
     *  get-method="getMediaTitle"
     *  set-method="setMediaTitle"
     * @castor.field-xml
     *  name="media:title"
     *  node="element"
     */
    public Title getMediaTitle()
    {
        return _mediaTitle;
    }

    /**
     * Returns the description of the media object.
     * @return a description. May be <code>null</code>.
     * @see #setMediaDescription
     * @castor.field
     *  get-method="getMediaDescription"
     *  set-method="setMediaDescription"
     * @castor.field-xml
     *  name="media:description"
     *  node="element"
     */
    public Description getMediaDescription()
    {
        return _mediaDescription;
    }

    /**
     * Initializes the description of the media object.
     * @param mediaDescription a description. May be <code>null</code>.
     * @see #getMediaDescription
     */
    public void setMediaDescription(final Description mediaDescription)
    {
        _mediaDescription = mediaDescription;
    }

    /**
     * Specifies if this media object is rated "adult" or not.
     * @return an adult rating indicator.
     * @see #setMediaAdult
     * @see #getMediaAdult
     */
    public boolean isMediaAdult()
    {
        return (_mediaAdult == null) ? false : _mediaAdult.booleanValue();
    }

    /**
     * Specifies if this media object is rated "adult" or not.
     * @param mediaAdult an adult rating indicator.
     * @see #isMediaAdult
     * @see #setMediaAdult
     */
    public void setMediaAdult(final boolean mediaAdult)
    {
        _mediaAdult = Boolean.valueOf(mediaAdult);
    }

    /**
     * Specifies if this media object is rated "adult" or not.
     * This is deprecated, and has been replaced with the more flexible media rating.
     * If not specified, defaults to <code>false</code>.
     * @return an adult rating indicator. May be <code>null</code>.
     * @see #setMediaAdult
     * @see #isMediaAdult
     * @castor.field
     *  get-method="getMediaAdult"
     *  set-method="setMediaAdult"
     * @castor.field-xml
     *  name="media:adult"
     *  node="element"
     */
    public Boolean getMediaAdult()
    {
        return _mediaAdult;
    }

    /**
     * Specifies if this media object is rated "adult" or not.
     * @param mediaAdult an adult rating indicator. May be <code>null</code>.
     * @see #getMediaAdult
     * @see #setMediaAdult
     */
    public void setMediaAdult(final Boolean mediaAdult)
    {
        _mediaAdult = mediaAdult;
    }

    /**
     * Returns a list of media object categories.
     * You may include as many category elements as you need to, for different schemes, and to have a content cross-referenced in different parts of the same scheme.
     * @return a list of media object categories. May be empty but not <code>null</code>.
     * @since 0.92
     * @see #addMediaCategory
     * @castor.field
     *  type="christophedelory.rss.media.Category"
     *  get-method="getMediaCategories"
     *  set-method="addMediaCategory"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:category"
     *  node="element"
     */
    public List<Category> getMediaCategories()
    {
        return _mediaCategories;
    }

    /**
     * Includes this media object in the specified category.
     * @param mediaCategory a category. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>category</code> is <code>null</code>.
     * @see #getMediaCategories
     */
    public void addMediaCategory(final Category mediaCategory)
    {
        if (mediaCategory == null)
        {
            throw new NullPointerException("no media category");
        }

        _mediaCategories.add(mediaCategory);
    }

    /**
     * Returns a list of media object ratings.
     * @return a list of media object ratings. May be empty but not <code>null</code>.
     * @see #addMediaRating
     * @castor.field
     *  type="christophedelory.rss.media.Rating"
     *  get-method="getMediaRatings"
     *  set-method="addMediaRating"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:rating"
     *  node="element"
     */
    public List<Rating> getMediaRatings()
    {
        return _mediaRatings;
    }

    /**
     * Adds a rating to this media object.
     * @param mediaRating a rating. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaRating</code> is <code>null</code>.
     * @see #getMediaRatings
     */
    public void addMediaRating(final Rating mediaRating)
    {
        if (mediaRating == null)
        {
            throw new NullPointerException("no media rating");
        }

        _mediaRatings.add(mediaRating);
    }

    /**
     * Returns highly relevant keywords describing the media object with typically a maximum of ten words.
     * The keywords and phrases should be comma delimited.
     * Example:
     * <pre>
     * &lt;media:keywords&gt;kitty, cat, big dog, yarn, fluffy&lt;/media:keywords&gt;
     * </pre>
     * @return some keywords. May be <code>null</code>
     * @see #setMediaKeywords
     * @castor.field
     *  get-method="getMediaKeywords"
     *  set-method="setMediaKeywords"
     * @castor.field-xml
     *  name="media:keywords"
     *  node="element"
     */
    public String getMediaKeywords()
    {
        return _mediaKeywords;
    }

    /**
     * Initializes highly relevant keywords describing the media object with typically a maximum of ten words.
     * @param mediaKeywords some keywords. May be <code>null</code>
     * @see #setMediaKeywords
     */
    public void setMediaKeywords(final String mediaKeywords)
    {
        _mediaKeywords = mediaKeywords;
    }

    /**
     * Returns a list of media object hashes.
     * @return a list of media object hashes. May be empty but not <code>null</code>.
     * @see #addMediaHash
     * @castor.field
     *  type="christophedelory.rss.media.Hash"
     *  get-method="getMediaHashes"
     *  set-method="addMediaHash"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:hash"
     *  node="element"
     */
    public List<Hash> getMediaHashes()
    {
        return _mediaHashes;
    }

    /**
     * Adds a hash to this media object.
     * @param mediaHash a hash. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaHash</code> is <code>null</code>.
     * @see #getMediaHashes
     */
    public void addMediaHash(final Hash mediaHash)
    {
        if (mediaHash == null)
        {
            throw new NullPointerException("no media hash");
        }

        _mediaHashes.add(mediaHash);
    }

    /**
     * Returns a list of credits.
     * @return a list of credits. May be empty but not <code>null</code>.
     * @see #addMediaCredit
     * @castor.field
     *  type="christophedelory.rss.media.Credit"
     *  get-method="getMediaCredits"
     *  set-method="addMediaCredit"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:credit"
     *  node="element"
     */
    public List<Credit> getMediaCredits()
    {
        return _mediaCredits;
    }

    /**
     * Adds a credit.
     * @param mediaCredit a credit. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaCredit</code> is <code>null</code>.
     * @see #getMediaCredits
     */
    public void addMediaCredit(final Credit mediaCredit)
    {
        if (mediaCredit == null)
        {
            throw new NullPointerException("no media credit");
        }

        _mediaCredits.add(mediaCredit);
    }

    /**
     * Returns copyright information for media object.
     * @return a copyright element. May be <code>null</code>
     * @see #setMediaCopyright
     * @castor.field
     *  get-method="getMediaCopyright"
     *  set-method="setMediaCopyright"
     * @castor.field-xml
     *  name="media:copyright"
     *  node="element"
     */
    public Copyright getMediaCopyright()
    {
        return _mediaCopyright;
    }

    /**
     * Initializes copyright information for media object.
     * @param mediaCopyright a copyright element. May be <code>null</code>
     * @see #setMediaCopyright
     */
    public void setMediaCopyright(final Copyright mediaCopyright)
    {
        _mediaCopyright = mediaCopyright;
    }

    /**
     * Returns a list of texts.
     * @return a list of texts. May be empty but not <code>null</code>.
     * @see #addMediaText
     * @castor.field
     *  type="christophedelory.rss.media.Text"
     *  get-method="getMediaTexts"
     *  set-method="addMediaText"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:text"
     *  node="element"
     */
    public List<Text> getMediaTexts()
    {
        return _mediaTexts;
    }

    /**
     * Adds a text.
     * @param mediaText a text. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaText</code> is <code>null</code>.
     * @see #getMediaTexts
     */
    public void addMediaText(final Text mediaText)
    {
        if (mediaText == null)
        {
            throw new NullPointerException("no media text");
        }

        _mediaTexts.add(mediaText);
    }

    /**
     * Returns a list of restrictions.
     * @return a list of restrictions. May be empty but not <code>null</code>.
     * @see #addMediaRestriction
     * @castor.field
     *  type="christophedelory.rss.media.Restriction"
     *  get-method="getMediaRestrictions"
     *  set-method="addMediaRestriction"
     *  collection="arraylist"
     * @castor.field-xml
     *  name="media:restriction"
     *  node="element"
     */
    public List<Restriction> getMediaRestrictions()
    {
        return _mediaRestrictions;
    }

    /**
     * Adds a restriction.
     * @param mediaRestriction a restriction. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>mediaRestriction</code> is <code>null</code>.
     * @see #getMediaRestrictions
     */
    public void addMediaRestriction(final Restriction mediaRestriction)
    {
        if (mediaRestriction == null)
        {
            throw new NullPointerException("no media restriction");
        }

        _mediaRestrictions.add(mediaRestriction);
    }
}
