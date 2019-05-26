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
package christophedelory.rss;

/**
 * GUID stands for globally unique identifier.
 * It's a string that uniquely identifies the item.
 * When present, an aggregator may choose to use this string to determine if an item is new.
 * There are no rules for the syntax of a guid. Aggregators must view them as a string.
 * It's up to the source of the feed to establish the uniqueness of the string.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="guid"
 */
public class GUID
{
    /**
     * If this attribute has a value of <code>true</code>, the reader may assume that it is a permalink to the item,
     * that is, an url that can be opened in a Web browser, that points to the full item described by the {@link Item item} element.
     */
    private boolean _isPermaLink = true;

    /**
     * A string that uniquely identifies the item.
     */
    private String _value = null;

    /**
     * Returns a string that uniquely identifies the item.
     * Examples: "http://some.server.com/weblogItem3207", "http://inessential.com/2002/09/01.php#a2".
     * No default value.
     * @return the GUID value. May be <code>null</code> if not yet initialized.
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
     * Initializes the string that uniquely identifies the item.
     * @param value a GUID value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Says that this GUID is a permanent link or not.
     * If this attribute has a value of <code>true</code>, the reader may assume that it is a permalink to the item, that is, an url that can be opened in a Web browser, that points to the full item described by the {@link Item item} element.
     * If its value is <code>false</code>, the guid may not be assumed to be an url, or an url to anything in particular.
     * Example: "true".
     * Defaults to <code>true</code>.
     * @return the permalink indicator.
     * @see #setPermaLink
     * @castor.field
     *  get-method="isPermaLink"
     *  set-method="setPermaLink"
     * @castor.field-xml
     *  name="isPermaLink"
     *  node="attribute"
     */
    public boolean isPermaLink()
    {
        return _isPermaLink;
    }

    /**
     * Specifies that this GUID is a permanent link or not.
     * @param isPermaLink the permalink indicator.
     * @see #isPermaLink
     */
    public void setPermaLink(final boolean isPermaLink)
    {
        _isPermaLink = isPermaLink;
    }
}
