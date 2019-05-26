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
 * Includes the item in one or more categories.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="category"
 */
public class Category
{
    /**
     * A string that identifies a categorization taxonomy.
     */
    private String _domain = null;

    /**
     * A forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     */
    private String _value = null;

    /**
     * Returns a forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     * Processors may establish conventions for the interpretation of categories.
     * Examples: "Grateful Dead", "MSFT", "1765".
     * No default value.
     * @return the category value. May be <code>null</code> if not yet initialized.
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
     * Initializes the forward-slash-separated string that identifies a hierarchic location in the indicated taxonomy.
     * @param value the category value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns a string that identifies a categorization taxonomy.
     * Examples: "http://www.fool.com/cusips", "Syndic8".
     * No default value.
     * @return the category domain. May be <code>null</code>.
     * @see #setDomain
     * @castor.field
     *  get-method="getDomain"
     *  set-method="setDomain"
     * @castor.field-xml
     *  name="domain"
     *  node="attribute"
     */
    public String getDomain()
    {
        return _domain;
    }

    /**
     * Initializes the string that identifies a categorization taxonomy.
     * @param domain the category domain. May be <code>null</code>.
     * @see #getDomain
     */
    public void setDomain(final String domain)
    {
        _domain = domain;
    }
}
