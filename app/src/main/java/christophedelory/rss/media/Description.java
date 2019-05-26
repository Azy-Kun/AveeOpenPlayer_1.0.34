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
 * A short description describing the media object, typically a sentence in length.
 * Example:
 * <pre>
 * &lt;media:description type="plain"&gt;This was some really bizarre band I listened to as a young lad.&lt;/media:description&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:description" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Description
{
    /**
     * The type of the text embedded.
     */
    private String _type = null;

    /**
     * The description itself.
     */
    private String _value = null;

    /**
     * Initializes the description itself.
     * @param value a description text. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns the description itself.
     * All html must be entity-encoded.
     * @return a description text. May be <code>null</code> if not yet initialized.
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
     * Initializes the type of text embedded.
     * @param type a description's type. May be <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type;
    }

    /**
     * Returns the type of text embedded.
     * Possible values are either 'plain' or 'html'.
     * Default value is 'plain'.
     * @return a description's type. May be <code>null</code>.
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
}
