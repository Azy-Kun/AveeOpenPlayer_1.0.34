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
 * Includes the media content in one or more categories.
 * Allows a taxonomy to be set that gives an indication of the type of media content, and its particular contents.
 * <br>
 * Examples:
 * <pre>
 * &lt;media:category scheme="http://search.yahoo.com/mrss/category_schema"&gt;music/artist/album/song&lt;/media:category&gt;
 * &lt;media:category scheme="http://dmoz.org" label="Ace Ventura - Pet Detective"&gt;Arts/Movies/Titles/A/Ace_Ventura_Series/Ace_Ventura_-_Pet_Detective&lt;/media:category&gt;
 * &lt;media:category scheme="urn:flickr:tags"&gt;ycantparkmobile&lt;/media:category&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:category" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Category
{
    /**
     * A string that identifies a categorization taxonomy.
     */
    private String _scheme = null;

    /**
     * The human readable label that can be displayed in end user applications.
     */
    private String _label = null;

    /**
     * A forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     */
    private String _value = null;

    /**
     * Returns a forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     * Processors may establish conventions for the interpretation of categories.
     * Examples: "Grateful Dead", "MSFT", "1765".
     * @return the category's value. May be <code>null</code> if not yet initialized.
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
     * Initializes a forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     * @param value the category's value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns a string (URI) that identifies a categorization taxonomy.
     * If this attribute is not included, the default scheme is 'http://search.yahoo.com/mrss/category_schema'.
     * Examples: "http://www.fool.com/cusips", "Syndic8".
     * @return the category's scheme. May be <code>null</code>.
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
     * Initializes a string (URI) that identifies a categorization taxonomy.
     * @param scheme the category's scheme. May be <code>null</code>.
     * @see #getScheme
     */
    public void setScheme(final String scheme)
    {
        _scheme = scheme;
    }

    /**
     * Returns the human readable label that can be displayed in end user applications.
     * @return a category label. May be <code>null</code>.
     * @see #setLabel
     * @castor.field
     *  get-method="getLabel"
     *  set-method="setLabel"
     * @castor.field-xml
     *  name="label"
     *  node="attribute"
     */
    public String getLabel()
    {
        return _label;
    }

    /**
     * Initializes the human readable label that can be displayed in end user applications.
     * @param label a category label. May be <code>null</code>.
     * @see #getLabel
     */
    public void setLabel(final String label)
    {
        _label = label;
    }
}
