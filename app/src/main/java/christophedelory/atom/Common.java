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
 * The common definitions in an Atom element.
 * @author Christophe Delory
 * @version $Revision: 92 $
 */
public class Common
{
    /**
     * The base URI for resolving any relative references found within the effective scope of this attribute.
     */
    private String _base = null;

    /**
     * The natural language for this element and its descendents.
     */
    private String _lang = null;

    /**
     * Returns the base URI for resolving any relative references found within the effective scope of this attribute.
     * <br>
     * Refer to <a href="http://www.w3.org/TR/2001/REC-xmlbase-20010627/">XML Base</a> for more details.
     * @return an URI as a string. May be <code>null</code>.
     * @see #setBaseString
     * @castor.field
     *  get-method="getBaseString"
     *  set-method="setBaseString"
     * @castor.field-xml
     *  name="xml:base"
     *  node="attribute"
     */
    public String getBaseString()
    {
        return _base;
    }

    /**
     * Initializes the base URI for resolving any relative references found within the effective scope of this attribute.
     * @param base an URI as a string. May be <code>null</code>.
     * @see #getBaseString
     */
    public void setBaseString(final String base)
    {
        _base = StringUtils.normalize(base);
    }

    /**
     * Returns the natural language for this element and its descendents.
     * <br>
     * Refer to <a href="http://www.w3.org/TR/2004/REC-xml-20040204/#sec-lang-tag">Extensible Markup Language (XML) 1.0 (Third Edition)</a> for more details.
     * @return a language tag. May be <code>null</code>.
     * @see #setLang
     * @castor.field
     *  get-method="getLang"
     *  set-method="setLang"
     * @castor.field-xml
     *  name="xml:lang"
     *  node="attribute"
     */
    public String getLang()
    {
        return _lang;
    }

    /**
     * Initializes the natural language for this element and its descendents.
     * @param lang a language tag. May be <code>null</code>.
     * @see #setLang
     */
    public void setLang(final String lang)
    {
        _lang = StringUtils.normalize(lang);
    }
}
