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
 * This allows the permissible audience to be declared.
 * If this element is not included, it assumes that no restrictions are necessary.
 * <br>
 * Examples:
 * <pre>
 * &lt;media:rating scheme="urn:simple"&gt;adult&lt;/media:rating&gt;
 * &lt;media:rating scheme="urn:icra"&gt;r (cz 1 lz 1 nz 1 oz 1 vz 1)&lt;/media:rating&gt;
 * &lt;media:rating scheme="urn:mpaa"&gt;pg&lt;/media:rating&gt;
 * &lt;media:rating scheme="urn:v-chip"&gt;tv-y7-fv&lt;/media:rating&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:rating" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Rating
{
    /**
     * The URI that identifies the rating scheme.
     */
    private String _scheme = null;

    /**
     * The value itself.
     */
    private String _value = null;

    /**
     * Returns the rating value itself.
     * @return the rating's value. May be <code>null</code> if not yet initialized.
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
     * Initializes the rating value itself.
     * @param value the rating's value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns the URI that identifies the rating scheme.
     * If this attribute is not included, the default scheme is urn:simple (adult | nonadult).
     * @return the rating's scheme. May be <code>null</code>.
     * @see #setScheme
     * @castor.field
     *  get-method="getScheme"
     *  set-method="setScheme"
     * @castor.field-xml
     *  name="scheme"
     *  node="attribute"
     */
    public String getScheme()
    {
        return _scheme;
    }

    /**
     * Initializes the URI that identifies the rating scheme.
     * @param scheme the rating's scheme. May be <code>null</code>.
     * @see #getScheme
     */
    public void setScheme(final String scheme)
    {
        _scheme = scheme;
    }
}
