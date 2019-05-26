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
 * Conveys information about a category associated with an entry or feed.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="category" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Category extends Common
{
    /**
     * The category to which the entry or feed belongs.
     */
    private String _term = null;

    /**
     * An URI that identifies a categorization scheme.
     */
    private String _scheme = null;

    /**
     * A human-readable label for display in end-user applications.
     */
    private String _label = null;

    /**
     * Returns the category to which the entry or feed belongs.
     * No default value.
     * @return the category term. May be <code>null</code> if not yet initialized.
     * @see #setTerm
     * @castor.field
     *  get-method="getTerm"
     *  set-method="setTerm"
     *  required="true"
     * @castor.field-xml
     *  name="term"
     *  node="attribute"
     */
    public String getTerm()
    {
        return _term;
    }

    /**
     * Initializes the category to which the entry or feed belongs.
     * @param term the category term. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>term</code> is <code>null</code>.
     * @see #getTerm
     */
    public void setTerm(final String term)
    {
        _term = term.trim(); // Throws NullPointerException if term is null.
    }

    /**
     * Returns an URI that identifies a categorization scheme.
     * @return a category scheme. May be <code>null</code>.
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
     * Initializes an URI that identifies a categorization scheme.
     * @param scheme a category scheme. May be <code>null</code>.
     * @see #getScheme
     */
    public void setScheme(final String scheme)
    {
        _scheme = scheme;
    }

    /**
     * Returns a human-readable label for display in end-user applications.
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
     * Initializes a human-readable label for display in end-user applications.
     * @param label a category label. May be <code>null</code>.
     * @see #getLabel
     */
    public void setLabel(final String label)
    {
        _label = label;
    }
}
