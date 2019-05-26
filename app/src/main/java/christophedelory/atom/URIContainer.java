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

/**
 * An URI container.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="uricontainer" ns-uri="http://www.w3.org/2005/Atom"
 */
public class URIContainer extends Common
{
    /**
     * The associated URI.
     */
    private String _uri = null;

    /**
     * Returns the associated URI.
     * @return an URI as a string. May be <code>null</code> if not yet initialized.
     * @see #setURIString
     * @castor.field
     *  get-method="getURIString"
     *  set-method="setURIString"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getURIString()
    {
        return _uri;
    }

    /**
     * Initializes the associated URI.
     * @param uri an URI as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>uri</code> is <code>null</code>.
     * @see #getURIString
     */
    public void setURIString(final String uri)
    {
        _uri = uri.trim(); // Throws NullPointerException if uri is null.
    }
}
