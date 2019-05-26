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
 * Notable entity and the contribution to the creation of the media object.
 * Current entities can include people, companies, locations, etc.
 * Specific entities can have multiple roles, and several entities can have the same role.
 * These should appear as distinct media credit elements.
 * <br>
 * Example:
 * <pre>
 * &lt;media:credit role="producer" scheme="urn:ebu"&gt;entity name&lt;/media:credit&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:credit" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Credit
{
    /**
     * The URI that identifies the role scheme.
     */
    private String _scheme = null;

    /**
     * Specifies the role the entity played.
     */
    private String _role = null;

    /**
     * The credit itself.
     */
    private String _value = null;

    /**
     * Returns the credit itself.
     * @return a credit value. May be <code>null</code> if not yet initialized.
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
     * Initializes the credit itself.
     * @param value a credit value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns the URI that identifies the role scheme.
     * If this attribute is not included, the default scheme is 'urn:ebu'.
     * See: <a href="http://www.ebu.ch/en/technical/metadata/specifications/role_codes.php">European Broadcasting Union Role Codes</a>.
     * @return the credit's scheme. May be <code>null</code>.
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
     * Initializes the URI that identifies the role scheme.
     * @param scheme the credit's scheme. May be <code>null</code>.
     * @see #getScheme
     */
    public void setScheme(final String scheme)
    {
        _scheme = scheme;
    }

    /**
     * Returns the role the entity played.
     * Must be lowercase.
     * <br>
     * Example roles:
     * <ul>
     * <li>actor</li>
     * <li>anchor person</li>
     * <li>author</li>
     * <li>choreographer</li>
     * <li>composer</li>
     * <li>conductor</li>
     * <li>director</li>
     * <li>editor</li>
     * <li>graphic designer</li>
     * <li>grip</li>
     * <li>illustrator</li>
     * <li>lyricist</li>
     * <li>music arranger</li>
     * <li>music group</li>
     * <li>musician</li>
     * <li>orchestra</li>
     * <li>performer</li>
     * <li>photographer</li>
     * <li>producer</li>
     * <li>reporter</li>
     * <li>vocalist</li>
     * </ul>
     * Additional roles: <a href="http://www.ebu.ch/en/technical/metadata/specifications/role_codes.php">European Broadcasting Union Role Codes</a>.
     * @return the entity role. May be <code>null</code>.
     * @see #setRole
     * @castor.field
     *  get-method="getRole"
     *  set-method="setRole"
     * @castor.field-xml
     *  name="role"
     *  node="attribute"
     */
    public String getRole()
    {
        return _role;
    }

    /**
     * Initializes the role the entity played.
     * @param role the entity role. May be <code>null</code>.
     * @see #getRole
     */
    public void setRole(final String role)
    {
        _role = role;
    }
}
