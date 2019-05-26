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

/**
 * Allows the inclusion of a text transcript, closed captioning, or lyrics of the media content.
 * Many of these elements are permitted to provide a time series of text.
 * In such cases, it is encouraged, but not required, that the elements be grouped by language and appear in time sequence order based on the start time.
 * Elements can have overlapping start and end times.
 * <br>
 * Examples:
 * <pre>
 * &lt;media:text type="plain" lang="en" start="00:00:03.000" end="00:00:10.000"&gt; Oh, say, can you see&lt;/media:text&gt;
 * &lt;media:text type="plain" lang="en" start="00:00:10.000" end="00:00:17.000"&gt;By the dawn's early light&lt;/media:text&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:text" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Text
{
    /**
     * The type of text embedded.
     */
    private String _type = null;

    /**
     * The primary language encapsulated in the media object.
     */
    private String _lang = null;

    /**
     * The start time offset that the text starts being relevant to the media object.
     */
    private String _start = null;

    /**
     * The end time that the text is relevant.
     */
    private String _end = null;

    /**
     * The text itself.
     */
    private String _value = null;

    /**
     * Returns the text itself.
     * All html must be entity-encoded.
     * @return the text. May be <code>null</code> if not yet initialized.
     * @see #setValue
     * @castor.field
     *  get-method="getValue"
     *  set-method="setValue"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * Initializes the text itself.
     * @param value the text. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns the type of text embedded.
     * Possible values are either 'plain' or 'html'.
     * Default value is 'plain'.
     * @return the type of text. May be <code>null</code>.
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
     * Initializes the type of text embedded.
     * @param type the type of text. May be <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type;
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
     * Returns the start time offset that the text starts being relevant to the media object.
     * An example of this would be for closed captioning.
     * It uses the NTP time code format (see the time attribute used in media thumbnail: {@link Thumbnail#getTime}).
     * @return a time offset. May be <code>null</code>.
     * @see #setStart
     * @castor.field
     *  get-method="getStart"
     *  set-method="setStart"
     * @castor.field-xml
     *  name="start"
     *  node="attribute"
     */
    public String getStart()
    {
        return _start;
    }

    /**
     * Initializes the start time offset that the text starts being relevant to the media object.
     * @param start a time offset. May be <code>null</code>.
     * @see #getStart
     */
    public void setStart(final String start)
    {
        _start = start;
    }

    /**
     * Returns the end time that the text is relevant.
     * If this attribute is not provided, and a start time is used,
     * it is expected that the end time is either the end of the clip or the start of the next media text element.
     * @return a time offset. May be <code>null</code>.
     * @see #setEnd
     * @castor.field
     *  get-method="getEnd"
     *  set-method="setEnd"
     * @castor.field-xml
     *  name="end"
     *  node="attribute"
     */
    public String getEnd()
    {
        return _end;
    }

    /**
     * Initializes the end time that the text is relevant.
     * @param end a time offset. May be <code>null</code>.
     * @see #getEnd
     */
    public void setEnd(final String end)
    {
        _end = end;
    }
}
