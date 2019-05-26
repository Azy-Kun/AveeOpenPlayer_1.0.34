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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Used to specify the enclosed media content.
 * The sequence of these items implies the order of presentation.
 * Contains the primary metadata entries needed to index and organize media content.
 * Additional supported attributes for describing images, audio, and video may be added in future revisions.
 * While many of the attributes appear to be audio/video specific, this element can be used to publish any type of media.
 * <br>
 * Example:
 * <pre>
 * &lt;media:content
 *  url="http://www.foo.com/movie.mov"
 *  fileSize="12216320"
 *  type="video/quicktime"
 *  medium="video"
 *  isDefault="true"
 *  expression="full"
 *  bitrate="128"
 *  framerate="25"
 *  samplingrate="44.1"
 *  channels="2"
 *  duration="185"
 *  height="200"
 *  width="300"
 *  lang="en"/&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:content" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Content extends BaseMedia
{
    /**
     * The direct URL to the media object.
     */
    private URI _url = null;

    /**
     * An integer which represents the runtime of the video in seconds.
     */
    private Integer _duration = null;

    /**
     * An integer representing the number of bytes of the media object.
     */
    private Long _fileSize = null;

    /**
     * Integer representing the value in pixels for the width of the media object.
     */
    private Integer _width = null;

    /**
     * Integer representing the value in pixels for the height of the media object.
     */
    private Integer _height = null;

    /**
     * Number of audio channels in the media object.
     */
    private Integer _channels = null;

    /**
     * An integer representing the kilobits per second rate of media.
     */
    private Integer _bitrate = null;

    /**
     * An integer that specifies the number of frames per second of the media object.
     */
    private Integer _framerate = null;

    /**
     * The number of samples per second taken to create the media object.
     */
    private Float _samplingrate = null;

    /**
     * A string containing the standard MIME type of the media object.
     */
    private String _type = null;

    /**
     * The type of the media object.
     */
    private String _medium = null;

    /**
     * Specifies whether you're linking to a short sample of a longer video ("sample"), or if you're linking to the full thing ("full"),
     * or if you're linking to a live stream ("nonstop").
     */
    private String _expression = null;

    /**
     * The primary language encapsulated in the media object.
     */
    private String _lang = null;

    /**
     * Specifies if this is the default object that should be used for the media group.
     */
    private Boolean _isDefault = null;

    /**
     * Initializes the direct URL to the media object.
     * @param url an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>url</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getURLString
     * @see #setURL
     */
    public void setURLString(final String url) throws URISyntaxException
    {
        _url = new URI(url); // May throw NullPointerException, URISyntaxException.
    }

    /**
     * Returns the direct URL to the media object, and is required unless the media player tag is used.
     * This is the URL that is passed to a media player so it can play the video.
     * Example: "<code>rtsp://www.hosthame.com/video.rm</code>".
     * @return an URL as a string. May be <code>null</code>.
     * @see #setURLString
     * @see #getURL
     * @castor.field
     *  get-method="getURLString"
     *  set-method="setURLString"
     * @castor.field-xml
     *  name="url"
     *  node="attribute"
     */
    public String getURLString()
    {
        String ret = null;

        if (_url != null)
        {
            ret = _url.toString();
        }

        return ret;
    }

    /**
     * Initializes the direct URL to the media object.
     * @param url an URL. May be <code>null</code>.
     * @see #getURL
     * @see #setURLString
     */
    public void setURL(final URI url)
    {
        _url = url;
    }

    /**
     * Returns the direct URL to the media object.
     * @return an URL. May be <code>null</code>.
     * @see #setURL
     * @see #getURLString
     */
    public URI getURL()
    {
        return _url;
    }

    /**
     * Initializes the integer which represents the runtime of the video in seconds.
     * @param duration a duration.
     * @see #getDuration
     * @see #setDuration(Integer)
     */
    public void setDuration(final int duration)
    {
        _duration = Integer.valueOf(duration);
    }

    /**
     * Initializes the integer which represents the runtime of the video in seconds.
     * @param duration a duration. May be <code>null</code>.
     * @see #getDuration
     * @see #setDuration(int)
     */
    public void setDuration(final Integer duration)
    {
        _duration = duration;
    }

    /**
     * Returns the integer which represents the runtime of the video in seconds.
     * The number of seconds the media object plays.
     * @return a duration. May be <code>null</code>.
     * @see #setDuration
     * @castor.field
     *  get-method="getDuration"
     *  set-method="setDuration"
     * @castor.field-xml
     *  name="duration"
     *  node="attribute"
     */
    public Integer getDuration()
    {
        return _duration;
    }

    /**
     * Initializes the integer representing the kilobits per second rate of media.
     * @param bitrate a bitrate. May be <code>null</code>.
     * @see #getBitrate
     */
    public void setBitrate(final Integer bitrate)
    {
        _bitrate = bitrate;
    }

    /**
     * Returns the integer representing the kilobits per second rate of media.
     * @return a bitrate. May be <code>null</code>.
     * @see #setBitrate
     * @castor.field
     *  get-method="getBitrate"
     *  set-method="setBitrate"
     * @castor.field-xml
     *  name="bitrate"
     *  node="attribute"
     */
    public Integer getBitrate()
    {
        return _bitrate;
    }

    /**
     * Initializes the integer that specifies the number of frames per second of the media object.
     * @param framerate a frame rate. May be <code>null</code>.
     * @see #getFramerate
     */
    public void setFramerate(final Integer framerate)
    {
        _framerate = framerate;
    }

    /**
     * Returns the integer that specifies the number of frames per second of the media object.
     * @return a frame rate. May be <code>null</code>.
     * @see #setFramerate
     * @castor.field
     *  get-method="getFramerate"
     *  set-method="setFramerate"
     * @castor.field-xml
     *  name="framerate"
     *  node="attribute"
     */
    public Integer getFramerate()
    {
        return _framerate;
    }

    /**
     * Initializes the number of samples per second taken to create the media object.
     * It is expressed in thousands of samples per second (kHz).
     * @param samplingrate a sampling rate. May be <code>null</code>.
     * @see #getSamplingrate
     */
    public void setSamplingrate(final Float samplingrate)
    {
        _samplingrate = samplingrate;
    }

    /**
     * Returns the number of samples per second taken to create the media object.
     * It is expressed in thousands of samples per second (kHz).
     * @return a sampling rate. May be <code>null</code>.
     * @see #setSamplingrate
     * @castor.field
     *  get-method="getSamplingrate"
     *  set-method="setSamplingrate"
     * @castor.field-xml
     *  name="samplingrate"
     *  node="attribute"
     */
    public Float getSamplingrate()
    {
        return _samplingrate;
    }

    /**
     * Initializes the integer representing the value in pixels for the width of the media object.
     * @param width a width. May be <code>null</code>.
     * @see #getWidth
     */
    public void setWidth(final Integer width)
    {
        _width = width;
    }

    /**
     * Returns the integer representing the value in pixels for the width of the media object.
     * @return a width. May be <code>null</code>.
     * @see #setWidth
     * @castor.field
     *  get-method="getWidth"
     *  set-method="setWidth"
     * @castor.field-xml
     *  name="width"
     *  node="attribute"
     */
    public Integer getWidth()
    {
        return _width;
    }

    /**
     * Initializes the integer representing the value in pixels for the height of the media object.
     * @param height a height. May be <code>null</code>.
     * @see #getHeight
     */
    public void setHeight(final Integer height)
    {
        _height = height;
    }

    /**
     * Returns the integer representing the value in pixels for the height of the media object.
     * @return a height. May be <code>null</code>.
     * @see #setHeight
     * @castor.field
     *  get-method="getHeight"
     *  set-method="setHeight"
     * @castor.field-xml
     *  name="height"
     *  node="attribute"
     */
    public Integer getHeight()
    {
        return _height;
    }

    /**
     * Initializes the number of audio channels in the media object.
     * @param channels a number of audio channels. May be <code>null</code>.
     * @see #getChannels
     */
    public void setChannels(final Integer channels)
    {
        _channels = channels;
    }

    /**
     * Returns the number of audio channels in the media object.
     * @return a number of audio channels. May be <code>null</code>.
     * @see #setChannels
     * @castor.field
     *  get-method="getChannels"
     *  set-method="setChannels"
     * @castor.field-xml
     *  name="channels"
     *  node="attribute"
     */
    public Integer getChannels()
    {
        return _channels;
    }

    /**
     * Initializes the long integer representing the number of bytes of the media object.
     * @param fileSize a file size. May be <code>null</code>.
     * @see #getFileSize
     */
    public void setFileSize(final Long fileSize)
    {
        _fileSize = fileSize;
    }

    /**
     * Returns the long integer representing the number of bytes of the media object.
     * @return a file size. May be <code>null</code>.
     * @see #setFileSize
     * @castor.field
     *  get-method="getFileSize"
     *  set-method="setFileSize"
     * @castor.field-xml
     *  name="fileSize"
     *  node="attribute"
     */
    public Long getFileSize()
    {
        return _fileSize;
    }

    /**
     * Returns the string containing the standard MIME type of the media object.
     * @return a MIME type. May be <code>null</code>.
     * @see #setType
     * @castor.field
     *  get-method="getType"
     *  set-method="setType"
     * @castor.field-xml
     *  name="type"
     *  node="attribute"
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Initializes the string containing the standard MIME type of the media object.
     * @param type a MIME type. May be <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type;
    }

    /**
     * Returns the type of the media object.
     * While this attribute can at times seem redundant if type is supplied,
     * it is included because it simplifies decision making on the reader side, as well as flushes out any ambiguities between MIME type and object type.
     * Values you can use for this are "image", "audio", "video", "document", or "executable".
     * @return a media object type. May be <code>null</code>.
     * @see #setMedium
     * @castor.field
     *  get-method="getMedium"
     *  set-method="setMedium"
     * @castor.field-xml
     *  name="medium"
     *  node="attribute"
     */
    public String getMedium()
    {
        return _medium;
    }

    /**
     * Initializes the type of the media object.
     * @param medium a media object type. May be <code>null</code>.
     * @see #getMedium
     */
    public void setMedium(final String medium)
    {
        _medium = medium;
    }

    /**
     * Specifies whether you're linking to a short sample of a longer video ("sample"), or if you're linking to the full thing ("full"),
     * or if you're linking to a live stream ("nonstop").
     * Determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop).
     * Default value is "full".
     * @return a media object expression. May be <code>null</code>.
     * @see #setExpression
     * @castor.field
     *  get-method="getExpression"
     *  set-method="setExpression"
     * @castor.field-xml
     *  name="expression"
     *  node="attribute"
     */
    public String getExpression()
    {
        return _expression;
    }

    /**
     * Specifies whether you're linking to a short sample of a longer video ("sample"), or if you're linking to the full thing ("full"),
     * or if you're linking to a live stream ("nonstop").
     * @param expression a media object expression. May be <code>null</code>.
     * @see #getExpression
     */
    public void setExpression(final String expression)
    {
        _expression = expression;
    }

    /**
     * Returns the primary language encapsulated in the media object.
     * Language codes possible are detailed in RFC 3066.
     * This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition).
     * @return a language code. May be <code>null</code>.
     * @see #setLang
     * @castor.field
     *  get-method="getLang"
     *  set-method="setLang"
     * @castor.field-xml
     *  name="lang"
     *  node="attribute"
     */
    public String getLang()
    {
        return _lang;
    }

    /**
     * Initializes the primary language encapsulated in the media object.
     * @param lang a language code. May be <code>null</code>.
     * @see #getLang
     */
    public void setLang(final String lang)
    {
        _lang = lang;
    }

    /**
     * Specifies if this is the default object that should be used for the media group.
     * @return the associated indicator.
     * @see #setDefault
     * @see #getIsDefault
     */
    public boolean isDefault()
    {
        return (_isDefault == null) ? false /*FIXME TBC*/ : _isDefault.booleanValue();
    }

    /**
     * Specifies if this is the default object that should be used for the media group.
     * @param isDefault the associated indicator.
     * @see #isDefault
     * @see #setIsDefault
     */
    public void setDefault(final boolean isDefault)
    {
        _isDefault = Boolean.valueOf(isDefault);
    }

    /**
     * Specifies if this is the default object that should be used for the media group.
     * There should only be one default object per media group.
     * If not specified, defaults to <code>false</code> (FIXME).
     * @return the associated indicator. May be <code>null</code>.
     * @see #setIsDefault
     * @see #isDefault
     * @castor.field
     *  get-method="getIsDefault"
     *  set-method="setIsDefault"
     * @castor.field-xml
     *  name="isDefault"
     *  node="attribute"
     */
    public Boolean getIsDefault()
    {
        return _isDefault;
    }

    /**
     * Specifies if this is the default object that should be used for the media group.
     * @param isDefault the associated indicator. May be <code>null</code>.
     * @see #getIsDefault
     * @see #setDefault
     */
    public void setIsDefault(final Boolean isDefault)
    {
        _isDefault = isDefault;
    }
}
