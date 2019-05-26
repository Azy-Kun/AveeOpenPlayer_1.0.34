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
 * Allows restrictions to be placed on the aggregator rendering the media in the feed.
 * Currently, restrictions are based on distributor (uri) and country codes.
 * This element is purely informational and no obligation can be assumed or implied.
 * Only one media restriction element of the same type can be applied to a media object - all others will be ignored.
 * Entities in this element should be space separated.
 * To allow the producer to explicitly declare his/her intentions, two literals are reserved: 'all', 'none'.
 * These literals can only be used once.
 * <br>
 * Example:
 * <pre>
 * &lt;media:restriction relationship="allow" type="country"&gt;au us&lt;/media:restriction&gt;
 * </pre>
 * Note: if the restriction element is empty and the type of relationship is "allow",
 * it is assumed that the empty list means "allow nobody" and the media should not be syndicated.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:restriction" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Restriction
{
    /**
     * The type of restriction (country | uri) that the media can be syndicated.
     */
    private String _type = null;

    /**
     * The type of relationship that the restriction represents (allow | deny).
     */
    private String _relationship = null;

    /**
     * The restriction itself.
     */
    private String _value = null;

    /**
     * Returns the restriction itself.
     * @return the restriction. May be <code>null</code>.
     * @see #setValue
     * @castor.field
     *  get-method="getValue"
     *  set-method="setValue"
     * @castor.field-xml
     *  node="text"
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * Initializes the restriction itself.
     * @param value the restriction. May be <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value;
    }

    /**
     * Returns the type of restriction (country | uri) that the media can be syndicated.
     * It is an optional attribute; however can only be excluded when using one of the literal values "all" or "none".
     * <ul>
     * <li>"country" allows restrictions to be placed based on country code
     * [<a href="http://www.iso.org/iso/en/prods-services/iso3166ma/index.html">ISO 3166</a>]</li>
     * <li>"uri" allows restrictions based on URI.
     * Examples: urn:apple, http://images.google.com, urn:yahoo, etc.</li>
     * </ul>
     * @return the type of restriction. May be <code>null</code>.
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
     * Initializes the type of restriction (country | uri) that the media can be syndicated.
     * @param type the type of restriction. May be <code>null</code>.
     * @see #getType
     */
    public void setType(final String type)
    {
        _type = type;
    }

    /**
     * Returns the type of relationship that the restriction represents (allow | deny).
     * In the example above, the media object should only be syndicated in Australia and the United States.
     * <br>
     * Note: if the restriction element is empty and the type of relationship is "allow",
     * it is assumed that the empty list means "allow nobody" and the media should not be syndicated.
     * @return the relationship type. May be <code>null</code> if not yet initialized.
     * @see #setRelationship
     * @castor.field
     *  get-method="getRelationship"
     *  set-method="setRelationship"
     *  required="true"
     * @castor.field-xml
     *  name="relationship"
     *  node="attribute"
     */
    public String getRelationship()
    {
        return _relationship;
    }

    /**
     * Initializes the type of relationship that the restriction represents (allow | deny).
     * @param relationship the relationship type. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>relationship</code> is <code>null</code>.
     * @see #getRelationship
     */
    public void setRelationship(final String relationship)
    {
        _relationship = relationship.trim(); // Throws NullPointerException if relationship is null.
    }
}
