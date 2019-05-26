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
 * The agent used to generate a feed, for debugging and other purposes.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="generator" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Generator extends Common
{
    /**
     * A human-readable name for the generating agent.
     */
    private String _value = null;

    /**
     * The URI representation of the agent.
     */
    private String _uri = null;

    /**
     * The version of the generating agent.
     */
    private String _version = null;

    /**
     * Returns a human-readable name for the generating agent.
     * No default value.
     * @return an agent name. May be <code>null</code> if not yet initialized.
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
     * Initializes a human-readable name for the generating agent.
     * @param value an agent name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #setValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns an URI representing the agent.
     * @return an URI as a string. May be <code>null</code>.
     * @see #setURIString
     * @castor.field
     *  get-method="getURIString"
     *  set-method="setURIString"
     * @castor.field-xml
     *  name="uri"
     *  node="attribute"
     */
    public String getURIString()
    {
        return _uri;
    }

    /**
     * Initializes the URI representing the agent.
     * @param uri an URI as a string. May be <code>null</code>.
     * @see #getURIString
     */
    public void setURIString(final String uri)
    {
        _uri = StringUtils.normalize(uri);
    }

    /**
     * Returns the version of the generating agent.
     * @return a version. May be <code>null</code>.
     * @see #setVersion
     * @castor.field
     *  get-method="getVersion"
     *  set-method="setVersion"
     * @castor.field-xml
     *  name="version"
     *  node="attribute"
     */
    public String getVersion()
    {
        return _version;
    }

    /**
     * Initializes the version of the generating agent.
     * @param version a version. May be <code>null</code>.
     * @see #getVersion
     */
    public void setVersion(final String version)
    {
        _version = StringUtils.normalize(version);
    }
}
