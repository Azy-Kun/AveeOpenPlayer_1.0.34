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
package christophedelory.atom;

import christophedelory.lang.StringUtils;

/**
 * An element that describes a person, corporation, or similar entity.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="person" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Person extends Common
{
    /**
     * A human-readable name for the person.
     */
    private String _name = null;

    /**
     * The URI associated with the person.
     */
    private String _uri = null;

    /**
     * The e-mail address associated with the person.
     */
    private String _email = null;

    /**
     * Returns a human-readable name for the person.
     * No default value.
     * @return a person name. May be <code>null</code> if not yet initialized.
     * @see #setName
     * @castor.field
     *  get-method="getName"
     *  set-method="setName"
     *  required="true"
     * @castor.field-xml
     *  name="name"
     *  node="element"
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Initializes a human-readable name for the person.
     * @param name a person name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @see #setName
     */
    public void setName(final String name)
    {
        _name = name.trim(); // Throws NullPointerException if name is null.
    }

    /**
     * Returns an URI associated with the person.
     * @return an URI as a string. May be <code>null</code>.
     * @see #setURIString
     * @castor.field
     *  get-method="getURIString"
     *  set-method="setURIString"
     * @castor.field-xml
     *  name="uri"
     *  node="element"
     */
    public String getURIString()
    {
        return _uri;
    }

    /**
     * Initializes the URI associated with the person.
     * @param uri an URI as a string. May be <code>null</code>.
     * @see #getURIString
     */
    public void setURIString(final String uri)
    {
        _uri = StringUtils.normalize(uri);
    }

    /**
     * Returns an e-mail address associated with the person.
     * Its content must conform to the "addr-spec" production in <a href="http://www.ietf.org/rfc/rfc3987.txt">RFC2822</a>.
     * @return an e-mail address. May be <code>null</code>.
     * @see #setEmail
     * @castor.field
     *  get-method="getEmail"
     *  set-method="setEmail"
     * @castor.field-xml
     *  name="email"
     *  node="element"
     */
    public String getEmail()
    {
        return _email;
    }

    /**
     * Initializes the e-mail address associated with the person.
     * @param email an e-mail address. May be <code>null</code>.
     * @see #getEmail
     */
    public void setEmail(final String email)
    {
        _email = StringUtils.normalize(email);
    }
}
